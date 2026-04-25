package com.nirapod.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirapod.model.District;

@Repository
public interface DistrictDAO extends JpaRepository<District, Long> {
	List<District> findByDivisionId(Long divisionId);
}
