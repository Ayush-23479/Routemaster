package com.routemasterapi.api.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.routemasterapi.api.entity.ParcelEntity;

@Repository
public interface ParcelRepository extends CrudRepository<ParcelEntity, Integer> {
    
    // ✅ Get all parcels
    @Query(value = "SELECT * FROM ayush_parcel", nativeQuery = true)
    Page<ParcelEntity> listallparcelsfromdb(Pageable pageable);
    
    // ✅ Get parcels by customer
    @Query(value = "SELECT * FROM ayush_parcel WHERE customerid = :customerId", nativeQuery = true)
    Page<ParcelEntity> listcustomerparcelstatusfromdb(int customerId, Pageable pageable);
    
    // ✅ Get parcels from last month
    @Query(value = "SELECT * FROM ayush_parcel WHERE created_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)", nativeQuery = true)
    Page<ParcelEntity> listonemonthparcelsfromdb(LocalDate oneMonthAgo, Pageable pageable);
    
    // ✅ Get delayed parcels from last month
    @Query(value = "SELECT * FROM ayush_parcel WHERE created_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND parcel_status = 'Delayed'", nativeQuery = true)
    Page<ParcelEntity> listonemonthdelayedparcelsfromdb(LocalDate oneMonthAgo, Pageable pageable);
    
    // ✅ Sum total payment
    @Query(value = "SELECT SUM(total_payment) FROM ayush_parcel", nativeQuery = true)
    double sumTotalPayment();
    
    // ✅ FIXED - Count parcels by route (using ayush_router instead of ayush_routes)
    @Query(value = "SELECT r.name, COUNT(p.parcelid) FROM ayush_parcel p LEFT JOIN ayush_router r ON p.routeid = r.routeId WHERE r.routeid IS NOT NULL GROUP BY p.routeid, r.name", nativeQuery = true)
    List<Object[]> countParcelsByRoute();
}