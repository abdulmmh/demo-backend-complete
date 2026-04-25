package com.nirapod.dao;

import com.nirapod.model.VatReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VatReturnDAO extends JpaRepository<VatReturn, Long> {


    List<VatReturn> findByIsDeletedFalse();

    Optional<VatReturn> findByIdAndIsDeletedFalse(Long id);

    boolean existsByBinNoAndPeriodMonthAndPeriodYearAndIsDeletedFalse(
            String binNo, String periodMonth, String periodYear);
    
    boolean existsByReturnNo(String returnNo);
    
    List<VatReturn> findByVatRegistration_IdAndIsDeletedFalse(Long vatRegistrationId);
}
