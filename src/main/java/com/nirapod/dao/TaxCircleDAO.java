package com.nirapod.dao;

import com.nirapod.model.TaxCircle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaxCircleDAO extends JpaRepository<TaxCircle, Long> {
    List<TaxCircle> findByTaxZoneId(Long taxZoneId);
}