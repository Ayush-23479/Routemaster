package com.routemasterapi.api.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

@Entity
@Table(name = "ayush_track_parcel")
public class TrackParcelEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trackid")
    private int trackId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcelid")
    @JsonIgnore
    private ParcelEntity parcel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeid")
    @JsonIgnore
    private EmployeeEntity employee;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "approval_status")
    private String approvalStatus;
    
    @Column(name = "timestamp")
    private Date timestamp;
    
    @Column(name = "notes")
    private String notes;

    // Getters and Setters
    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public ParcelEntity getParcel() {
        return parcel;
    }

    public void setParcel(ParcelEntity parcel) {
        this.parcel = parcel;
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

