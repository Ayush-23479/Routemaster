package com.routemasterapi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.routemasterapi.api.model.EmployeeIdRequest;
import com.routemasterapi.api.model.EmployeeRequestBody;
import com.routemasterapi.api.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Create new employee
     * Endpoint: POST /api/employees/create
     * Requires: JWT Token
     */
    @PostMapping("/create")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeRequestBody employeeReqBody) {
        try {
            // Get current logged-in user (admin)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            System.out.println("👷 Creating employee by user: " + currentUserEmail);
            
            return ResponseEntity.ok(employeeService.createEmployee(employeeReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error creating employee: " + e.getMessage());
        }
    }

    /**
     * Update employee
     * Endpoint: PUT /api/employees/update
     * Requires: JWT Token
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeRequestBody employeeReqBody) {
        try {
            return ResponseEntity.ok(employeeService.updateEmployee(employeeReqBody));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error updating employee: " + e.getMessage());
        }
    }

    /**
     * Get all employees (paginated)
     * Endpoint: GET /api/employees?pageNumber=0&size=10
     * Requires: JWT Token
     */
    @GetMapping("")
    public ResponseEntity<?> listAllEmployees(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer size) {
        try {
            return ResponseEntity.ok(employeeService.listAllEmployeesFromDb(pageNumber, size));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error listing employees: " + e.getMessage());
        }
    }

    /**
     * Delete employee
     * Endpoint: DELETE /api/employees/delete
     * Requires: JWT Token
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEmployee(@RequestBody EmployeeIdRequest employeeIdRequest) {
        try {
            return ResponseEntity.ok(employeeService.deleteEmployee(employeeIdRequest));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error deleting employee: " + e.getMessage());
        }
    }
}