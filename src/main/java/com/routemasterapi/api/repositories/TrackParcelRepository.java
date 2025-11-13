package com.routemasterapi.api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.routemasterapi.api.entity.TrackParcelEntity;

@Repository
public interface TrackParcelRepository extends CrudRepository<TrackParcelEntity, Integer> {
    
    // ✅ FIXED - Correct table name and column names (lowercase)
    @Query(value = "SELECT * FROM ayush_track_parcel", nativeQuery = true)
    Page<TrackParcelEntity> listalltrackparcelsfromdb(Pageable pageable);
}
