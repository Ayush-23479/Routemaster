package com.routemasterapi.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ayush_customer")
public class customerentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private int customerId;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName", nullable = false)
    private String lastName;

    @Column(name = "Phone", nullable = false)
    private String phone;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Address", nullable = false)
    private String address;

    @Column(name = "Password", nullable = false)
    private String password;

    // Getters & Setters
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

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
}