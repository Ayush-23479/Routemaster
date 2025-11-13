package com.routemasterapi.api.model;
 
import java.util.Date;

public class ParcelRequestBody { 
		 
		private int parcelId;
		
 		private String  senderName;
		
 		private String  destinationAddress;
		
 		private String  destinationPincode;
		
 		private String  receiverName;
		
 		private String  parcelStatus;
 		 private String createdDate;
 		private int customerId;
 		private double totalPayment;

		

		public double getTotalPayment() {
			return totalPayment;
		}

		public void setTotalPayment(double totalPayment) {
			this.totalPayment = totalPayment;
		}

		public int getCustomerId() {
			return customerId;
		}

		public void setCustomerId(int customerId) {
			this.customerId = customerId;
		}

		

		private int routeId;
		public int getRouteId() {
			return routeId;
		}

		public void setRouteId(int routeId) {
			this.routeId = routeId;
		}

		public String getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}

		public int getParcelId() {
			return parcelId;
		}

		public void setParcelId(int parcelId) {
			this.parcelId = parcelId;
		}

		public String getSenderName() {
			return senderName;
		}

		public void setSenderName(String senderName) {
			this.senderName = senderName;
		}

		public String getDestinationAddress() {
			return destinationAddress;
		}

		public void setDestinationAddress(String destinationAddress) {
			this.destinationAddress = destinationAddress;
		}

		public String getDestinationPincode() {
			return destinationPincode;
		}

		public void setDestinationPincode(String destinationPincode) {
			this.destinationPincode = destinationPincode;
		}

		public String getReceiverName() {
			return receiverName;
		}

		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}

		public String getParcelStatus() {
			return parcelStatus;
		}

		public void setParcelStatus(String parcelStatus) {
			this.parcelStatus = parcelStatus;
		}
		 
	}