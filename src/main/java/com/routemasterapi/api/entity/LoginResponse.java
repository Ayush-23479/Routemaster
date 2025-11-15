package com.routemasterapi.api.model;

public class LoginResponse {
    private String token;
    private String role;
    private int customerId;
    private String email;
    private String firstName;
    private String lastName;

    // Constructor
    public LoginResponse(String token, String role, int customerId, String email, 
                        String firstName, String lastName) {
        this.token = token;
        this.role = role;
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public String getToken() { 
        return token; 
    }

    public String getRole() { 
        return role; 
    }

    public int getCustomerId() { 
        return customerId; 
    }

    public String getEmail() { 
        return email; 
    }

    public String getFirstName() { 
        return firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }

    // Setters
    public void setToken(String token) { 
        this.token = token; 
    }

    public void setRole(String role) { 
        this.role = role; 
    }

    public void setCustomerId(int customerId) { 
        this.customerId = customerId; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
}
