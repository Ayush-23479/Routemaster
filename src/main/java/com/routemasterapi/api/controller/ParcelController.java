package com.routemasterapi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.routemasterapi.api.model.ParcelRequestBody;
import com.routemasterapi.api.model.ParcelIdRequest;
import com.routemasterapi.api.service.ParcelService;
import com.routemasterapi.api.entity.customerentity;
import com.routemasterapi.api.repositories.CustomerRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/parcels")
@CrossOrigin
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Create new parcel
     * Endpoint: POST /api/parcels/create
     * Requires: JWT Token
     */
    @PostMapping("/create")
    public ResponseEntity<?> createParcel(@RequestBody ParcelRequestBody parcelReqBody) {
        try {
            // Get current logged-in user's email from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            System.out.println("📦 Creating parcel by user: " + currentUserEmail);
            
            return ResponseEntity.ok(parcelService.createParcel(parcelReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error creating parcel: " + e.getMessage());
        }
    }

    /**
     * Update parcel
     * Endpoint: PUT /api/parcels/update
     * Requires: JWT Token
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateParcel(@RequestBody ParcelRequestBody parcelReqBody) {
        try {
            return ResponseEntity.ok(parcelService.updateParcel(parcelReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error updating parcel: " + e.getMessage());
        }
    }

    /**
     * Get all parcels (paginated) - ADMIN ONLY
     * Endpoint: GET /api/parcels?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("")
    public ResponseEntity<?> listAllParcels(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(parcelService.listallparcelsfromdb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing parcels: " + e.getMessage());
        }
    }

    /**
     * ✅ NEW ENDPOINT - Get current user's parcels
     * Endpoint: GET /api/parcels/my-parcels?pageNumber=0&size=10
     * Requires: JWT Token
     * For: USER and ADMIN
     */
    @GetMapping("/my-parcels")
    public ResponseEntity<?> getMyParcels(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            // Get current logged-in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            System.out.println("📦 User " + currentUserEmail + " requesting their parcels");
            
            // Find customer by email
            Optional<customerentity> customerOpt = customerRepository.findByEmail(currentUserEmail);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(404).body("❌ User profile not found");
            }
            
            // Get their customer ID
            int customerId = customerOpt.get().getCustomerId();
            
            // Return parcels for this customer
            return ResponseEntity.ok(parcelService.listcustomerparcelstatusfromdb(pageNumber, size, customerId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error loading your parcels: " + e.getMessage());
        }
    }

    /**
     * Get parcels for specific customer
     * Endpoint: GET /api/parcels/customer/{customerId}?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> listCustomerParcelStatus(
            @PathVariable int customerId,
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            System.out.println("📦 User " + currentUserEmail + " requesting parcels for customer ID: " + customerId);
            
            return ResponseEntity.ok(parcelService.listcustomerparcelstatusfromdb(pageNumber, size, customerId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing customer parcels: " + e.getMessage());
        }
    }

    /**
     * Get parcels from last month
     * Endpoint: GET /api/parcels/last-month?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("/last-month")
    public ResponseEntity<?> listOneMonthParcels(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(parcelService.listonemonthparcelsfromdb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing last month parcels: " + e.getMessage());
        }
    }

    /**
     * Get delayed parcels from last month
     * Endpoint: GET /api/parcels/delayed?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("/delayed")
    public ResponseEntity<?> listOneMonthDelayedParcels(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(parcelService.listonemonthdelayedparcelsfromdb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing delayed parcels: " + e.getMessage());
        }
    }

    /**
     * Delete parcel
     * Endpoint: DELETE /api/parcels/delete
     * Requires: JWT Token
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteParcel(@RequestBody ParcelIdRequest parcelIdRequest) {
        try {
            return ResponseEntity.ok(parcelService.deleteParcel(parcelIdRequest));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error deleting parcel: " + e.getMessage());
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok("✅ Test endpoint works!");
    }

    @GetMapping("/test-with-auth")
    public ResponseEntity<?> testWithAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return ResponseEntity.ok("✅ Authenticated as: " + userEmail);
    }
}