package com.nirapod.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirapod.model.BusinessType;

@Repository
public interface BusinessTypeDAO extends JpaRepository<BusinessType, Long> {
	
	
}
