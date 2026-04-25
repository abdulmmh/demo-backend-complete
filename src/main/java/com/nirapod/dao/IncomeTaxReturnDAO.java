package com.nirapod.dao;

import com.nirapod.model.IncomeTaxReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeTaxReturnDAO extends JpaRepository<IncomeTaxReturn, Long> {
    
    boolean existsByTinNumberAndAssessmentYearAndIsDeletedFalse(String tinNumber, String assessmentYear);
    
    List<IncomeTaxReturn> findByIsDeletedFalse();
    
    Optional<IncomeTaxReturn> findByIdAndIsDeletedFalse(Long id);
}