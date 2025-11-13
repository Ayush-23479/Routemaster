package com.routemasterapi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.routemasterapi.api.model.TrackParcelIdRequest;
import com.routemasterapi.api.model.TrackParcelRequestBody;
import com.routemasterapi.api.service.TrackParcelService;

@RestController
@RequestMapping("/api/track")
@CrossOrigin
public class TrackParcelController {

    @Autowired
    private TrackParcelService trackParcelService;

    /**
     * Create tracking entry
     * Endpoint: POST /api/track/create
     * Requires: JWT Token
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTrackParcel(@RequestBody TrackParcelRequestBody trackReqBody) {
        try {
            // Get current logged-in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            System.out.println("📍 Creating tracking by user: " + currentUserEmail);
            
            return ResponseEntity.ok(trackParcelService.createParcel(trackReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error creating tracking: " + e.getMessage());
        }
    }

    /**
     * Update tracking entry
     * Endpoint: PUT /api/track/update
     * Requires: JWT Token
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateTrackParcel(@RequestBody TrackParcelRequestBody trackReqBody) {
        try {
            return ResponseEntity.ok(trackParcelService.updateParcel(trackReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error updating tracking: " + e.getMessage());
        }
    }

    /**
     * Get all tracking records (paginated)
     * Endpoint: GET /api/track?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("")
    public ResponseEntity<?> listAllTrackParcels(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(trackParcelService.listAllparcelfromdb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing tracking records: " + e.getMessage());
        }
    }

    /**
     * Delete tracking entry
     * Endpoint: DELETE /api/track/delete
     * Requires: JWT Token
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTrackParcel(@RequestBody TrackParcelIdRequest trackIdRequest) {
        try {
            return ResponseEntity.ok(trackParcelService.deleteparcel(trackIdRequest));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error deleting tracking: " + e.getMessage());
        }
    }
}