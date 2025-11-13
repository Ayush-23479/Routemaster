package com.routemasterapi.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.routemasterapi.api.repositories.CustomerRepository;
import com.routemasterapi.api.repositories.ParcelRepository;
import com.routemasterapi.api.repositories.RouterRepository;

@Service
public class DashboardService {
	 @Autowired
	    private CustomerRepository customerRepository;

	    @Autowired
	    private ParcelRepository parcelRepository;

	    @Autowired
	    private RouterRepository routerRepository;

	    public long getNumberOfCustomers() {
	        return customerRepository.count();
	    }

	    public long getNumberOfParcelsSent() {
	        return parcelRepository.count();
	    }

	    public double getTotalPaymentCollected() {
	        return parcelRepository.sumTotalPayment();
	    }

	    public List<Object[]> getParcelCountPerRoute() {
	        return parcelRepository.countParcelsByRoute();
	    }

}
