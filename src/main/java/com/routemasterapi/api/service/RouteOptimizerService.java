package com.routemasterapi.api.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.routemasterapi.api.entity.ParcelEntity;
import com.routemasterapi.api.entity.RouterEntity;
import com.routemasterapi.api.repositories.ParcelRepository;
import com.routemasterapi.api.repositories.RouterRepository;

@Service
public class RouteOptimizerService {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizerService.class);
    private static final double DEFAULT_CAPACITY = 100.0;
    private static final int EARTH_RADIUS_KM = 6371;
    private static final double DEPOT_LAT = 40.7128;
    private static final double DEPOT_LON = -74.0060;
    
    @Autowired
    private ParcelRepository parcelRepository;
    
    @Autowired
    private RouterRepository routerRepository;
    
    static class Location {
        private final double latitude;
        private final double longitude;
        
        Location(double lat, double lon) {
            this.latitude = lat;
            this.longitude = lon;
        }
        
        double distanceTo(Location other) {
            if (other == null) {
                return Double.MAX_VALUE;
            }
            
            double dLat = Math.toRadians(other.latitude - this.latitude);
            double dLon = Math.toRadians(other.longitude - this.longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                      Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                      Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            return EARTH_RADIUS_KM * c;
        }
    }
    
    static class RouteInfo {
        private final int routeId;
        private final String routeName;
        private final Location location;
        private final double capacity;
        private double currentLoad;
        private final List<ParcelEntity> assignedParcels;
        private double totalDistance;
        
        RouteInfo(int id, String name, Location loc, double cap) {
            this.routeId = id;
            this.routeName = name != null ? name : "Route-" + id;
            this.location = loc != null ? loc : new Location(DEPOT_LAT, DEPOT_LON);
            this.capacity = cap > 0 ? cap : DEFAULT_CAPACITY;
            this.currentLoad = 0;
            this.assignedParcels = new ArrayList<>();
            this.totalDistance = 0;
        }
        
        boolean canAccommodate(double weight) {
            return weight > 0 && (currentLoad + weight) <= capacity;
        }
        
        void addParcel(ParcelEntity parcel, double distance) {
            if (parcel != null && canAccommodate(parcel.getWeight())) {
                assignedParcels.add(parcel);
                currentLoad += parcel.getWeight();
                totalDistance += distance;
            }
        }
        
        double getUtilizationPercentage() {
            return capacity > 0 ? (currentLoad / capacity) * 100 : 0;
        }
    }
    
    public static class OptimizationResult {
        public Map<String, List<ParcelEntity>> routeAssignments;
        public Map<String, Double> routeLoads;
        public Map<String, Double> routeUtilization;
        public Map<String, Double> routeDistances;
        public double totalDistance;
        public double efficiency;
        public int assignedParcels;
        public int unassignedParcels;
        public int totalParcels;
        public String status;
        public long executionTimeMs;
        
        public OptimizationResult() {
            this.routeAssignments = new HashMap<>();
            this.routeLoads = new HashMap<>();
            this.routeUtilization = new HashMap<>();
            this.routeDistances = new HashMap<>();
            this.totalDistance = 0;
            this.efficiency = 0;
            this.assignedParcels = 0;
            this.unassignedParcels = 0;
            this.totalParcels = 0;
            this.status = "Pending";
            this.executionTimeMs = 0;
        }
    }
    
    public OptimizationResult optimizeRoutesUsingFloodFill() {
        long startTime = System.currentTimeMillis();
        OptimizationResult result = new OptimizationResult();
        
        try {
            logger.info("=== Starting Flood Fill Route Optimization ===");
            
            List<ParcelEntity> allParcels = validateAndFetchParcels();
            List<RouterEntity> allRoutes = validateAndFetchRoutes();
            
            result.totalParcels = allParcels.size();
            
            if (allParcels.isEmpty() || allRoutes.isEmpty()) {
                result.status = "⚠️ No parcels or routes available in database";
                result.executionTimeMs = System.currentTimeMillis() - startTime;
                logger.warn(result.status);
                return result;
            }
            
            logger.info("Found {} parcels and {} routes", allParcels.size(), allRoutes.size());
            
            Map<Integer, RouteInfo> routeMap = initializeRoutes(allRoutes);
            
            List<ParcelEntity> sortedParcels = allParcels.stream()
                .sorted(Comparator.comparingDouble(ParcelEntity::getWeight).reversed())
                .collect(Collectors.toList());
            
            logger.info("Sorted {} parcels by weight", sortedParcels.size());
            
            for (ParcelEntity parcel : sortedParcels) {
                if (parcel == null || parcel.getWeight() <= 0) {
                    result.unassignedParcels++;
                    continue;
                }
                
                if (floodFillAssignment(parcel, routeMap)) {
                    result.assignedParcels++;
                } else {
                    result.unassignedParcels++;
                    logger.warn("Could not assign parcel ID: {}", parcel.getParcelId());
                }
            }
            
            for (RouteInfo route : routeMap.values()) {
                if (!route.assignedParcels.isEmpty()) {
                    result.routeAssignments.put(route.routeName, new ArrayList<>(route.assignedParcels));
                    result.routeLoads.put(route.routeName, route.currentLoad);
                    result.routeUtilization.put(route.routeName, route.getUtilizationPercentage());
                    result.routeDistances.put(route.routeName, route.totalDistance);
                    result.totalDistance += route.totalDistance;
                }
            }
            
            double totalCapacity = routeMap.values().stream()
                .mapToDouble(r -> r.capacity)
                .sum();
            double totalLoad = routeMap.values().stream()
                .mapToDouble(r -> r.currentLoad)
                .sum();
            
            result.efficiency = totalCapacity > 0 ? (totalLoad / totalCapacity) * 100 : 0;
            result.executionTimeMs = System.currentTimeMillis() - startTime;
            result.status = String.format("✅ Successfully optimized. Assigned: %d/%d parcels | Efficiency: %.2f%%", 
                result.assignedParcels, result.totalParcels, result.efficiency);
            
            logger.info("Optimization complete");
            
        } catch (Exception e) {
            result.status = "❌ Error: " + e.getMessage();
            result.executionTimeMs = System.currentTimeMillis() - startTime;
            logger.error("Error during optimization", e);
        }
        
        return result;
    }
    
    private boolean floodFillAssignment(ParcelEntity parcel, Map<Integer, RouteInfo> routeMap) {
        Location parcelLocation = extractLocationFromAddress(parcel.getDestinationAddress());
        
        if (parcelLocation == null) {
            return false;
        }
        
        RouteInfo bestRoute = null;
        double minDistance = Double.MAX_VALUE;
        
        for (RouteInfo route : routeMap.values()) {
            if (route.canAccommodate(parcel.getWeight())) {
                double distance = route.location.distanceTo(parcelLocation);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestRoute = route;
                }
            }
        }
        
        if (bestRoute != null) {
            bestRoute.addParcel(parcel, minDistance);
            return true;
        }
        
        for (RouteInfo route : routeMap.values()) {
            if (route.canAccommodate(parcel.getWeight())) {
                route.addParcel(parcel, 0);
                return true;
            }
        }
        
        return false;
    }
    
    public OptimizationResult optimizeWithClustering() {
        long startTime = System.currentTimeMillis();
        OptimizationResult result = new OptimizationResult();
        
        try {
            logger.info("=== Starting Clustering Optimization ===");
            
            List<ParcelEntity> allParcels = validateAndFetchParcels();
            List<RouterEntity> allRoutes = validateAndFetchRoutes();
            
            result.totalParcels = allParcels.size();
            
            if (allParcels.isEmpty() || allRoutes.isEmpty()) {
                result.status = "⚠️ No parcels or routes available";
                result.executionTimeMs = System.currentTimeMillis() - startTime;
                return result;
            }
            
            Map<String, List<ParcelEntity>> clusters = clusterParcelsByPincode(allParcels);
            logger.info("Created {} clusters", clusters.size());
            
            for (String pincode : clusters.keySet()) {
                List<ParcelEntity> pincodeParcels = clusters.get(pincode);
                RouterEntity optimalRoute = findOptimalRoute(allRoutes, pincodeParcels);
                
                if (optimalRoute != null) {
                    result.routeAssignments.put(optimalRoute.getName(), new ArrayList<>(pincodeParcels));
                    double totalWeight = 0;
                    for (ParcelEntity p : pincodeParcels) {
                        totalWeight += p.getWeight();
                    }
                    result.routeLoads.put(optimalRoute.getName(), totalWeight);
                    result.assignedParcels += pincodeParcels.size();
                } else {
                    result.unassignedParcels += pincodeParcels.size();
                }
            }
            
            result.efficiency = result.totalParcels > 0 ? 
                ((double) result.assignedParcels / result.totalParcels) * 100 : 0;
            result.executionTimeMs = System.currentTimeMillis() - startTime;
            result.status = String.format("✅ Clustering optimization complete. Assigned: %d/%d", 
                result.assignedParcels, result.totalParcels);
            
        } catch (Exception e) {
            result.status = "❌ Error: " + e.getMessage();
            result.executionTimeMs = System.currentTimeMillis() - startTime;
            logger.error("Error during clustering", e);
        }
        
        return result;
    }
    
    public Map<String, Object> getOptimizationStats() {
        try {
            long totalParcels = 0;
            long totalRoutes = 0;
            
            for (ParcelEntity p : parcelRepository.findAll()) {
                if (p != null) totalParcels++;
            }
            
            for (RouterEntity r : routerRepository.findAll()) {
                if (r != null) totalRoutes++;
            }
            
            double avgParcelsPerRoute = totalRoutes > 0 ? (double) totalParcels / totalRoutes : 0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalParcels", totalParcels);
            stats.put("totalRoutes", totalRoutes);
            stats.put("avgParcelsPerRoute", avgParcelsPerRoute);
            stats.put("optimizationAlgorithm", "Flood Fill");
            stats.put("timestamp", System.currentTimeMillis());
            stats.put("status", "✅ System healthy");
            
            return stats;
        } catch (Exception e) {
            logger.error("Error getting stats", e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", e.getMessage());
            errorStats.put("status", "❌ Error");
            return errorStats;
        }
    }
    
    private List<ParcelEntity> validateAndFetchParcels() {
        List<ParcelEntity> parcels = new ArrayList<>();
        try {
            for (ParcelEntity p : parcelRepository.findAll()) {
                if (p != null && p.getWeight() > 0) {
                    parcels.add(p);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching parcels", e);
        }
        return parcels;
    }
    
    private List<RouterEntity> validateAndFetchRoutes() {
        List<RouterEntity> routes = new ArrayList<>();
        try {
            for (RouterEntity r : routerRepository.findAll()) {
                if (r != null && r.getRouteId() > 0 && r.getName() != null) {
                    routes.add(r);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching routes", e);
        }
        return routes;
    }
    
    private Map<Integer, RouteInfo> initializeRoutes(List<RouterEntity> allRoutes) {
        Map<Integer, RouteInfo> routeMap = new HashMap<>();
        
        for (RouterEntity route : allRoutes) {
            if (route != null) {
                Location routeLoc = new Location(
                    DEPOT_LAT + (Math.random() * 0.1 - 0.05), 
                    DEPOT_LON + (Math.random() * 0.1 - 0.05)
                );
                
                double capacity = DEFAULT_CAPACITY;
                Double distance = route.getTotalDistance();
                if (distance != null && distance > 0) {
                    capacity = distance;
               
                
                }
                
                RouteInfo info = new RouteInfo(
                    route.getRouteId(), 
                    route.getName(), 
                    routeLoc, 
                    capacity
                );
                routeMap.put(route.getRouteId(), info);
            }
        }
        
        return routeMap;
    }
    
    private Location extractLocationFromAddress(String address) {
        if (address == null || address.isEmpty()) {
            return new Location(DEPOT_LAT, DEPOT_LON);
        }
        return new Location(
            DEPOT_LAT + Math.random() * 0.3 - 0.15, 
            DEPOT_LON + Math.random() * 0.3 - 0.15
        );
    }
    
    private Map<String, List<ParcelEntity>> clusterParcelsByPincode(List<ParcelEntity> parcels) {
        Map<String, List<ParcelEntity>> clusters = new HashMap<>();
        
        for (ParcelEntity parcel : parcels) {
            if (parcel != null && parcel.getDestinationPincode() != null) {
                String pincode = parcel.getDestinationPincode();
                if (!clusters.containsKey(pincode)) {
                    clusters.put(pincode, new ArrayList<>());
                }
                clusters.get(pincode).add(parcel);
            }
        }
        
        return clusters;
    }
    
    private RouterEntity findOptimalRoute(List<RouterEntity> routes, List<ParcelEntity> parcels) {
        if (routes == null || routes.isEmpty() || parcels == null || parcels.isEmpty()) {
            return null;
        }
        
        for (RouterEntity route : routes) {
            if (route != null) {
                return route;
            }
        }
        
        return null;
    }
}