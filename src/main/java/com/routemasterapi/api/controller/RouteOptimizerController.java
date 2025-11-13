package com.routemasterapi.api.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.routemasterapi.api.service.RouteOptimizerService;
import com.routemasterapi.api.service.RouteOptimizerService.OptimizationResult;
import com.routemasterapi.api.repositories.ParcelRepository;
import com.routemasterapi.api.repositories.RouterRepository;
import com.routemasterapi.api.entity.ParcelEntity;
import com.routemasterapi.api.entity.RouterEntity;

@RestController
@RequestMapping("/api/optimizer")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RouteOptimizerController {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizerController.class);
    
    @Autowired
    private RouteOptimizerService routeOptimizerService;
    
    @Autowired
    private ParcelRepository parcelRepository;
    
    @Autowired
    private RouterRepository routerRepository;
    
    @GetMapping("/health")
    public ResponseEntity<?> getHealth() {
        logger.info("Health check requested");
        
        try {
            Map<String, String> health = new HashMap<>();
            health.put("status", "✅ Optimizer is running");
            health.put("algorithm", "Flood Fill");
            health.put("version", "2.0");
            health.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.error("Error in health check", e);
            Map<String, String> errorHealth = new HashMap<>();
            errorHealth.put("status", "❌ Error");
            errorHealth.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorHealth);
        }
    }
    
    @GetMapping("/diagnostics")
    public ResponseEntity<?> getDiagnostics() {
        logger.info("Diagnostic request received");
        
        Map<String, Object> diagnostics = new HashMap<>();
        
        try {
            Map<String, Object> parcelDiag = new HashMap<>();
            try {
                List<ParcelEntity> allParcels = new ArrayList<>();
                parcelRepository.findAll().forEach(allParcels::add);
                
                parcelDiag.put("status", "✅ Connected");
                parcelDiag.put("totalCount", allParcels.size());
                
                if (allParcels.isEmpty()) {
                    parcelDiag.put("warning", "⚠️ No parcels found in database");
                } else {
                    ParcelEntity firstParcel = allParcels.get(0);
                    Map<String, Object> firstParcelData = new HashMap<>();
                    firstParcelData.put("parcelId", firstParcel.getParcelId());
                    firstParcelData.put("tracking_number", firstParcel.getTrackingNumber());
                    firstParcelData.put("weight", firstParcel.getWeight());
                    firstParcelData.put("destinationAddress", firstParcel.getDestinationAddress());
                    firstParcelData.put("destinationPincode", firstParcel.getDestinationPincode());
                    
                    parcelDiag.put("firstParcelSample", firstParcelData);
                    
                    long zeroWeights = 0;
                    for (ParcelEntity p : allParcels) {
                        if (p.getWeight() <= 0) {
                            zeroWeights++;
                        }
                    }
                    
                    parcelDiag.put("zeroOrNullWeights", zeroWeights);
                    parcelDiag.put("validParcels", allParcels.size() - zeroWeights);
                }
            } catch (Exception e) {
                parcelDiag.put("status", "❌ Error");
                parcelDiag.put("error", e.getMessage());
                logger.error("Error reading parcel table", e);
            }
            diagnostics.put("parcelTable", parcelDiag);
            
            Map<String, Object> routerDiag = new HashMap<>();
            try {
                List<RouterEntity> allRouters = new ArrayList<>();
                routerRepository.findAll().forEach(allRouters::add);
                
                routerDiag.put("status", "✅ Connected");
                routerDiag.put("totalCount", allRouters.size());
                
                if (allRouters.isEmpty()) {
                    routerDiag.put("warning", "⚠️ No routes found in database");
                } else {
                    RouterEntity firstRouter = allRouters.get(0);
                    Map<String, Object> firstRouterData = new HashMap<>();
                    firstRouterData.put("routeId", firstRouter.getRouteId());
                    firstRouterData.put("name", firstRouter.getName());
                    firstRouterData.put("description", firstRouter.getDescription());
                    firstRouterData.put("pincode", firstRouter.getPincode());
                    firstRouterData.put("totalDistance", firstRouter.getTotalDistance());
                    
                    routerDiag.put("firstRouterSample", firstRouterData);
                    
                    long zeroDistance = 0;
                    for (RouterEntity r : allRouters) {
                        if (r.getTotalDistance() <= 0) {
                            zeroDistance++;
                        }
                    }
                    
                    routerDiag.put("zeroOrNullDistance", zeroDistance);
                    routerDiag.put("validRoutes", allRouters.size() - zeroDistance);
                }
            } catch (Exception e) {
                routerDiag.put("status", "❌ Error");
                routerDiag.put("error", e.getMessage());
                logger.error("Error reading router table", e);
            }
            diagnostics.put("routerTable", routerDiag);
            
            Map<String, Object> connectionDiag = new HashMap<>();
            try {
                long parcelCount = 0;
                for (ParcelEntity p : parcelRepository.findAll()) {
                    parcelCount++;
                }
                
                long routerCount = 0;
                for (RouterEntity r : routerRepository.findAll()) {
                    routerCount++;
                }
                
                connectionDiag.put("status", "✅ Database Connected");
                connectionDiag.put("totalParcels", parcelCount);
                connectionDiag.put("totalRouters", routerCount);
                connectionDiag.put("canOptimize", parcelCount > 0 && routerCount > 0);
            } catch (Exception e) {
                connectionDiag.put("status", "❌ Database Connection Failed");
                connectionDiag.put("error", e.getMessage());
                logger.error("Database connection error", e);
            }
            diagnostics.put("databaseConnection", connectionDiag);
            
            diagnostics.put("overallStatus", "✅ Diagnostic complete");
            
            logger.info("Diagnostics retrieved successfully");
            return ResponseEntity.ok(diagnostics);
            
        } catch (Exception e) {
            logger.error("Error getting diagnostics", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving diagnostics: " + e.getMessage());
        }
    }
    
    @GetMapping("/optimize-routes")
    public ResponseEntity<?> optimizeRoutes() {
        logger.info("Request received for Flood Fill route optimization");
        
        try {
            if (routeOptimizerService == null) {
                logger.error("RouteOptimizerService is null");
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Service not initialized properly");
            }
            
            OptimizationResult result = routeOptimizerService.optimizeRoutesUsingFloodFill();
            
            if (result == null) {
                logger.error("Optimization result is null");
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Optimization returned null result");
            }
            
            logger.info("Optimization successful: {}", result.status);
            return ResponseEntity.ok(result);
            
        } catch (NullPointerException e) {
            logger.error("NullPointerException during optimization", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Null value encountered: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error optimizing routes", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error optimizing routes: " + e.getMessage());
        }
    }
    
    @GetMapping("/optimize-with-clustering")
    public ResponseEntity<?> optimizeWithClustering() {
        logger.info("Request received for clustering-based optimization");
        
        try {
            if (routeOptimizerService == null) {
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Service not initialized");
            }
            
            OptimizationResult result = routeOptimizerService.optimizeWithClustering();
            
            if (result == null) {
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Optimization returned null result");
            }
            
            logger.info("Clustering optimization successful: {}", result.status);
            return ResponseEntity.ok(result);
            
        } catch (NullPointerException e) {
            logger.error("NullPointerException during clustering", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Null value encountered: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error optimizing with clustering", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error with clustering optimization: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getOptimizationStats() {
        logger.info("Request received for optimization stats");
        
        try {
            if (routeOptimizerService == null) {
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Service not initialized");
            }
            
            Map<String, Object> stats = routeOptimizerService.getOptimizationStats();
            
            if (stats == null) {
                stats = new HashMap<>();
                stats.put("error", "Could not retrieve stats");
                stats.put("status", "❌ Error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stats);
            }
            
            logger.info("Stats retrieved successfully");
            return ResponseEntity.ok(stats);
            
        } catch (NullPointerException e) {
            logger.error("NullPointerException while getting stats", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Null value encountered: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error getting stats", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving stats: " + e.getMessage());
        }
    }
    
    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "❌ Error");
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("httpStatus", status.value());
        
        return new ResponseEntity<>(errorResponse, status);
    }
}