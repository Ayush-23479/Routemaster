package com.routemasterapi.api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.routemasterapi.api.entity.RouterEntity;

@Repository
public interface RouterRepository extends JpaRepository<RouterEntity, Integer> {
  
    @Query(value = "SELECT * FROM ayush_router", nativeQuery = true) 
    Page<RouterEntity> findAll(Pageable pageable);
}