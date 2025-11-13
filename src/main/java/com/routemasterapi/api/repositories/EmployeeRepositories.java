package com.routemasterapi.api.repositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.routemasterapi.api.entity.EmployeeEntity;

@Repository
public interface EmployeeRepositories extends CrudRepository<EmployeeEntity, Integer>{
	
	// ✅ SIMPLE FIX - Use JPQL (Object-Oriented Query Language)
	// No nativeQuery = true, no resultSetMapping
	@Query("SELECT e FROM EmployeeEntity e")
	Page<EmployeeEntity> findAllEmployees(Pageable pageable);
}