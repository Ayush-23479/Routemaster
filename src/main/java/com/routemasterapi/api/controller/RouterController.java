package com.routemasterapi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.routemasterapi.api.model.RouterIdRequest;
import com.routemasterapi.api.model.RouterRequestBody;
import com.routemasterapi.api.service.RouterService;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin
public class RouterController {

    @Autowired
    private RouterService routerService;

    /**
     * Create new route
     * Endpoint: POST /api/routes/create
     * Requires: JWT Token
     */
    @PostMapping("/create")
    public ResponseEntity<?> createRouter(@RequestBody RouterRequestBody routerReqBody) {
        try {
            // Get current logged-in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            System.out.println("🗺️ Creating route by user: " + currentUserEmail);
            
            return ResponseEntity.ok(routerService.createRouter(routerReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error creating route: " + e.getMessage());
        }
    }

    /**
     * Update route
     * Endpoint: PUT /api/routes/update
     * Requires: JWT Token
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateRouter(@RequestBody RouterRequestBody routerReqBody) {
        try {
            return ResponseEntity.ok(routerService.updateRouter(routerReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error updating route: " + e.getMessage());
        }
    }

    /**
     * Get all routes (paginated)
     * Endpoint: GET /api/routes?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("")
    public ResponseEntity<?> listAllRoutes(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(routerService.listAllrouterfromdb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing routes: " + e.getMessage());
        }
    }

    /**
     * Delete route
     * Endpoint: DELETE /api/routes/delete
     * Requires: JWT Token
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRouter(@RequestBody RouterIdRequest routerIdReq) {
        try {
            return ResponseEntity.ok(routerService.deleteRouter(routerIdReq));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error deleting route: " + e.getMessage());
        }
    }
}