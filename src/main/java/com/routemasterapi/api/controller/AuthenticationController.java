
package com.routemasterapi.api.controller;

import com.routemasterapi.api.entity.customerentity;
import com.routemasterapi.api.model.LoginResponse;
import com.routemasterapi.api.repositories.CustomerRepository;
import com.routemasterapi.api.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * ✅ Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody customerentity user) {
        // Validate required fields
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Email is required.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Password is required.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ First name is required.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Last name is required.");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Phone is required.");
        }
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Address is required.");
        }

        // Check if email already exists
        if (customerRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("❌ User already exists with this email.");
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ SET DEFAULT ROLE TO USER
        user.setRole("USER");

        // Save user
        customerRepository.save(user);
        return ResponseEntity.ok("✅ User registered successfully!");
    }

    /**
     * ✅ Login user and return role + user info
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody customerentity loginRequest) {
        // Validate required fields
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Email is required.");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Password is required.");
        }

        var userOpt = customerRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid email or password.");
        }

        var user = userOpt.get();

        // ✅ Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("❌ Invalid email or password.");
        }

        // ✅ Generate JWT token
        String token = jwtTokenUtil.generateToken(user.getEmail());
        
        // ✅ Get user role (default to USER if null)
        String userRole = user.getRole() != null ? user.getRole() : "USER";
        
        // ✅ Create and return LoginResponse with role
        LoginResponse response = new LoginResponse(
            token,
            userRole,
            user.getCustomerId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
        
        return ResponseEntity.ok(response);
    }
}