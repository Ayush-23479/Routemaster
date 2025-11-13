package com.routemasterapi.api.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ayush_parcel")
public class ParcelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcelid")
    private int parcelId;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "weight")
    private double weight;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_address")
    private String senderAddress;

    @Column(name = "sender_phone")
    private String senderPhone;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_address")
    private String receiverAddress;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "destination_pincode")
    private String destinationPincode;

    @Column(name = "parcel_status")
    private String parcelStatus;

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "total_payment")
    private double totalPayment;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    // ✅ FIXED - Add @JsonIgnore to prevent serialization issues
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderid")
    @JsonIgnore
    private customerentity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverid")
    @JsonIgnore
    private customerentity receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid")
    @JsonIgnore
    private customerentity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routeid")
    @JsonIgnore
    private RouterEntity route;

    // Getters and Setters
    public int getParcelId() {
        return parcelId;
    }

    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
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

    public String getParcelStatus() {
        return parcelStatus;
    }

    public void setParcelStatus(String parcelStatus) {
        this.parcelStatus = parcelStatus;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public customerentity getSender() {
        return sender;
    }

    public void setSender(customerentity sender) {
        this.sender = sender;
    }

    public customerentity getReceiver() {
        return receiver;
    }

    public void setReceiver(customerentity receiver) {
        this.receiver = receiver;
    }

    public customerentity getCustomer() {
        return customer;
    }

    public void setCustomer(customerentity customer) {
        this.customer = customer;
    }

    public RouterEntity getRoute() {
        return route;
    }

    public void setRoute(RouterEntity route) {
        this.route = route;
    }
}