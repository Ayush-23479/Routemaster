package com.routemasterapi.api.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.routemasterapi.api.entity.EmployeeEntity;
import com.routemasterapi.api.entity.ParcelEntity;
import com.routemasterapi.api.entity.TrackParcelEntity;
import com.routemasterapi.api.model.TrackParcelIdRequest;
import com.routemasterapi.api.model.TrackParcelRequestBody;
import com.routemasterapi.api.repositories.EmployeeRepositories;
import com.routemasterapi.api.repositories.ParcelRepository;
import com.routemasterapi.api.repositories.TrackParcelRepository;

@Service
public class TrackParcelService {
    
    @Autowired
    private TrackParcelRepository trackParcelRepository;
    
    @Autowired
    private ParcelRepository parcelRepository;
    
    @Autowired
    private EmployeeRepositories employeeRepository;

    // ✅ CREATE - Create tracking entry
    public TrackParcelEntity createParcel(TrackParcelRequestBody trackParcelReqBody) {
        // Validate parcel exists
        Optional<ParcelEntity> optionalParcel = parcelRepository.findById(trackParcelReqBody.getParcelId());
        ParcelEntity parcel = optionalParcel.orElseThrow(() -> new Error("No parcel with this id"));
        
        // Validate employee exists
        Optional<EmployeeEntity> optionalEmployee = employeeRepository.findById(trackParcelReqBody.getEmpId());
        EmployeeEntity employee = optionalEmployee.orElseThrow(() -> new Error("No employee with this id"));
        
        // Create new tracking entry
        TrackParcelEntity newTrackParcel = new TrackParcelEntity();
        newTrackParcel.setParcel(parcel);
        newTrackParcel.setEmployee(employee);
        newTrackParcel.setLocation(trackParcelReqBody.getLocation());
        newTrackParcel.setStatus(trackParcelReqBody.getStatus());
        newTrackParcel.setApprovalStatus(trackParcelReqBody.getApprovalStatus());
        newTrackParcel.setNotes(trackParcelReqBody.getNotes());
        
        return trackParcelRepository.save(newTrackParcel);
    }

    // ✅ UPDATE - Update tracking entry
    public TrackParcelEntity updateParcel(TrackParcelRequestBody trackParcelReqBody) {
        // Validate parcel exists
        Optional<ParcelEntity> optionalParcel = parcelRepository.findById(trackParcelReqBody.getParcelId());
        ParcelEntity parcel = optionalParcel.orElseThrow(() -> new Error("No parcel with this id"));
        
        // Validate employee exists
        Optional<EmployeeEntity> optionalEmployee = employeeRepository.findById(trackParcelReqBody.getEmpId());
        EmployeeEntity employee = optionalEmployee.orElseThrow(() -> new Error("No employee with this id"));
        
        // Update tracking entry
        TrackParcelEntity updatedTrackParcel = new TrackParcelEntity();
        updatedTrackParcel.setTrackId(trackParcelReqBody.getTrackId());
        updatedTrackParcel.setParcel(parcel);
        updatedTrackParcel.setEmployee(employee);
        updatedTrackParcel.setLocation(trackParcelReqBody.getLocation());
        updatedTrackParcel.setStatus(trackParcelReqBody.getStatus());
        updatedTrackParcel.setApprovalStatus(trackParcelReqBody.getApprovalStatus());
        updatedTrackParcel.setNotes(trackParcelReqBody.getNotes());
        
        return trackParcelRepository.save(updatedTrackParcel);
    }

    // ✅ READ - Get all tracking records
    public Page<TrackParcelEntity> listAllparcelfromdb(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        return trackParcelRepository.listalltrackparcelsfromdb(pageable);
    }

    // ✅ DELETE - Delete tracking entry
    public String deleteparcel(TrackParcelIdRequest trackIdReq) {
        int trackId = trackIdReq.getTrackId();
        trackParcelRepository.deleteById(trackId);
        return "Record Deleted";
    }
}


