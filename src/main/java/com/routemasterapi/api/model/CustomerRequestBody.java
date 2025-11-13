package com.routemasterapi.api.model;

public class CustomerRequestBody {
    private int customerId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String password;  // ← THIS LINE IS MISSING IN YOUR FILE!

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ← THESE 3 METHODS ARE MISSING IN YOUR FILE!
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}