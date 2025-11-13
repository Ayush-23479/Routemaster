package com.routemasterapi.api.model;

public class TrackParcelRequestBody {
    
    private int trackId;
    private int parcelId;
    private int empId;
    private String location;
    private String status;
    private String approvalStatus;
    private String notes;

    // Getters and Setters
    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getParcelId() {
        return parcelId;
    }

    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
