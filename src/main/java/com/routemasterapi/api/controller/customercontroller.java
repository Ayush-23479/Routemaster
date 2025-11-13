package com.routemasterapi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.routemasterapi.api.entity.customerentity;
import com.routemasterapi.api.model.CustomerRequestBody;
import com.routemasterapi.api.model.CustomerIdRequest;
import com.routemasterapi.api.repositories.CustomerRepository;
import com.routemasterapi.api.service.CustomerService;

import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
public class customercontroller {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Get current logged-in user's profile
     * Endpoint: GET /api/customers/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentCustomer() {
        try {
            // Get email from JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Optional<customerentity> customer = customerRepository.findByEmail(email);
            if (customer.isEmpty()) {
                return ResponseEntity.status(404).body("❌ Customer not found");
            }

            // Don't return password
            customerentity user = customer.get();
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Create new customer
     * Endpoint: POST /api/customers/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequestBody customerReqBody) {
        try {
            return ResponseEntity.ok(customerService.createCustomer(customerReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Update customer
     * Endpoint: PUT /api/customers/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerRequestBody customerReqBody) {
        try {
            return ResponseEntity.ok(customerService.updateCustomer(customerReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Get all customers (paginated)
     * Endpoint: GET /api/customers
     */
    @GetMapping("")
    public ResponseEntity<?> listAllCustomers(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(customerService.listAllCustomersFromDb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Delete customer
     * Endpoint: DELETE /api/customers/delete
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCustomer(@RequestBody CustomerIdRequest customerIdReq) {
        try {
            return ResponseEntity.ok(customerService.deleteCustomer(customerIdReq));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }
}