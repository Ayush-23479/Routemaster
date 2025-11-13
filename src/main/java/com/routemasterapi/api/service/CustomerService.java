package com.routemasterapi.api.service;

import com.routemasterapi.api.entity.customerentity;
import com.routemasterapi.api.model.CustomerIdRequest;
import com.routemasterapi.api.model.CustomerRequestBody; 
import com.routemasterapi.api.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ ADDED - Password encoder

    public customerentity createCustomer(CustomerRequestBody customerReqBody) {
        customerentity newCustomer = new customerentity();
        newCustomer.setFirstName(customerReqBody.getFirstName());
        newCustomer.setLastName(customerReqBody.getLastName());
        newCustomer.setPhone(customerReqBody.getPhone());
        newCustomer.setEmail(customerReqBody.getEmail());
        newCustomer.setAddress(customerReqBody.getAddress());
        
        // ✅ FIXED - Encode password before saving
        if (customerReqBody.getPassword() != null && !customerReqBody.getPassword().isEmpty()) {
            newCustomer.setPassword(passwordEncoder.encode(customerReqBody.getPassword()));
        }
        
        return customerRepository.save(newCustomer);         
    }

    public customerentity updateCustomer(CustomerRequestBody customerReqBody) {
        customerentity updatedCustomer = customerRepository.findById(customerReqBody.getCustomerId()).orElse(null);
        if (updatedCustomer != null) {
            updatedCustomer.setFirstName(customerReqBody.getFirstName());
            updatedCustomer.setLastName(customerReqBody.getLastName());
            updatedCustomer.setPhone(customerReqBody.getPhone());
            updatedCustomer.setEmail(customerReqBody.getEmail());
            updatedCustomer.setAddress(customerReqBody.getAddress());
            
            // ✅ FIXED - Only update password if provided
            if (customerReqBody.getPassword() != null && !customerReqBody.getPassword().isEmpty()) {
                updatedCustomer.setPassword(passwordEncoder.encode(customerReqBody.getPassword()));
            }
            
            return customerRepository.save(updatedCustomer);
        }
        return null;
    }

    public Page<customerentity> listAllCustomersFromDb(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        return customerRepository.findAllCustomer(pageable);
    }

    public String deleteCustomer(CustomerIdRequest customerIdReq) {
        int customerId = customerIdReq.getCustomerId();
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return "Customer record deleted successfully";
        } else {
            return "Customer record not found";
        }
    }
}