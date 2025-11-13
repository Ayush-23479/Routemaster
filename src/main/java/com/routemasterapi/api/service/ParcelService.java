package com.routemasterapi.api.service;

import com.routemasterapi.api.entity.ParcelEntity;
import com.routemasterapi.api.entity.RouterEntity;
import com.routemasterapi.api.entity.customerentity;
import com.routemasterapi.api.model.ParcelIdRequest;
import com.routemasterapi.api.model.ParcelRequestBody;
import com.routemasterapi.api.repositories.CustomerRepository;
import com.routemasterapi.api.repositories.ParcelRepository;
import com.routemasterapi.api.repositories.RouterRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ParcelService  {

	@Autowired
	private ParcelRepository parcelRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private RouterRepository routeRepository;
	

	public ParcelEntity createParcel(ParcelRequestBody parcelReqBody) {
		Optional<customerentity> optionalCustomer = customerRepository.findById(parcelReqBody.getCustomerId());
		customerentity customer = optionalCustomer.orElseThrow(() -> new Error("No customer with this id") );
		
		Optional<RouterEntity> optionalRoute = routeRepository.findById(parcelReqBody.getRouteId());
		RouterEntity route = optionalRoute.orElseThrow(() -> new Error("No route with this id") );
		ParcelEntity newParcel = new ParcelEntity();
		newParcel.setSenderName(parcelReqBody.getSenderName());
		newParcel.setReceiverName(parcelReqBody.getReceiverName());
		newParcel.setDestinationAddress(parcelReqBody.getDestinationAddress());
		newParcel.setDestinationPincode(parcelReqBody.getDestinationPincode());
		newParcel.setParcelStatus(parcelReqBody.getParcelStatus());
		newParcel.setCreatedDate(parcelReqBody.getCreatedDate());
		newParcel.setTotalPayment(parcelReqBody.getTotalPayment());
		newParcel.setCustomer(customer);
		newParcel.setRoute(route);
		return parcelRepository.save(newParcel);		 
	}

	public ParcelEntity updateParcel(ParcelRequestBody parcelReqBody) {
		Optional<customerentity> optionalCustomer = customerRepository.findById(parcelReqBody.getCustomerId());
		customerentity customer = optionalCustomer.orElseThrow(() -> new Error("No customer with this id") );
		
		Optional<RouterEntity> optionalRoute = routeRepository.findById(parcelReqBody.getRouteId());
		RouterEntity route = optionalRoute.orElseThrow(() -> new Error("No route with this id") );
		ParcelEntity newParcel = new ParcelEntity();
		newParcel.setParcelId(parcelReqBody.getParcelId());
		newParcel.setSenderName(parcelReqBody.getSenderName());
		newParcel.setReceiverName(parcelReqBody.getReceiverName());
		newParcel.setDestinationAddress(parcelReqBody.getDestinationAddress());
		newParcel.setDestinationPincode(parcelReqBody.getDestinationPincode());
		newParcel.setParcelStatus(parcelReqBody.getParcelStatus());
		newParcel.setCreatedDate(parcelReqBody.getCreatedDate());
		newParcel.setTotalPayment(parcelReqBody.getTotalPayment());
		newParcel.setCustomer(customer);
		newParcel.setRoute(route);
		
		return parcelRepository.save(newParcel);		 
	}

	public Page<ParcelEntity> listallparcelsfromdb(int pageNumber, int size) {
		Pageable pageable = PageRequest.of(pageNumber, size);
		return parcelRepository.listallparcelsfromdb(pageable);
	}
 
	public String deleteParcel(ParcelIdRequest parcelIdReq) {
		int ParcelId= parcelIdReq.getParcelId();
		parcelRepository.deleteById(ParcelId);
		return "Record Deleted";
	}
	
	public Page<ParcelEntity> listcustomerparcelstatusfromdb(int pageNumber, int size, int customerId) {
		Pageable pageable = PageRequest.of(pageNumber, size);
		return parcelRepository.listcustomerparcelstatusfromdb(customerId, pageable);
	}
	
	public Page<ParcelEntity> listonemonthparcelsfromdb(int pageNumber, int size) {
		Pageable pageable = PageRequest.of(pageNumber, size);
		LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
		return parcelRepository.listonemonthparcelsfromdb(oneMonthAgo, pageable);
	}
	
	public Page<ParcelEntity> listonemonthdelayedparcelsfromdb(int pageNumber, int size) {
		Pageable pageable = PageRequest.of(pageNumber, size);
		LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
		return parcelRepository.listonemonthdelayedparcelsfromdb(oneMonthAgo, pageable);
	}

}