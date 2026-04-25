package com.nirapod.dao;

import com.nirapod.model.TaxZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaxZoneDAO extends JpaRepository<TaxZone, Long> {
    List<TaxZone> findByDistrictId(Long districtId);
    List<TaxZone> findByDistrictName(String districtName);
}