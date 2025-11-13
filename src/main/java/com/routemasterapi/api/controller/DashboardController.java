package com.routemasterapi.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.routemasterapi.api.repositories.CustomerRepository;
import com.routemasterapi.api.repositories.ParcelRepository;

@RestController
public class DashboardController {
	@Autowired
    private CustomerRepository customerRepository;

	@RequestMapping(value = "/dashboard/customercount", method = RequestMethod.GET)
    public ResponseEntity<Long> getNumberOfCustomers() {
        long numberOfCustomers = customerRepository.count();
        return ResponseEntity.ok(numberOfCustomers);
    }
    

    @Autowired
    private ParcelRepository parcelRepository;

    @RequestMapping(value = "/dashboard/parcelscount", method = RequestMethod.GET)
    public ResponseEntity<Long> getNumberOfParcelsSent() {
        long numberOfParcels = parcelRepository.count();
        return ResponseEntity.ok(numberOfParcels);
    }
    
    

    @RequestMapping(value = "/dashboard/parcelpayment", method = RequestMethod.GET)
    public ResponseEntity<Double> getTotalPaymentCollected() {
        double totalPayment = parcelRepository.sumTotalPayment();
        return ResponseEntity.ok(totalPayment);
    }
   

    @RequestMapping(value = "/dashboard/parcelroutecount", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Long>> getParcelCountPerRoute() {
        List<Object[]> result = parcelRepository.countParcelsByRoute();
        Map<String, Long> routeCountMap = new HashMap<>();
        for (Object[] row : result) {
            String routeName = (String) row[0];
            Long parcelCount = (Long) row[1];
            routeCountMap.put(routeName, parcelCount);
        }
        return ResponseEntity.ok(routeCountMap);
    }

}
