package com.routemasterapi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.routemasterapi.api.security.JwtTokenUtil;

@RestController
@RequestMapping("/test")
public class TestTokenController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/authtoken")
    public ResponseEntity<String> getAutoToken() {
        // Generate a test token for user "test@example.com"
        String token = jwtTokenUtil.generateToken("test@example.com");
        return ResponseEntity.ok(token);
    }
}
