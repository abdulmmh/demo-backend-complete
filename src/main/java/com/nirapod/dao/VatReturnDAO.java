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

    // FIX: old method existsByReturnNo() had no isDeleted filter.
    // A soft-deleted return with the same returnNo would block generation of a new one
    // since the do-while loop in generateReturnNo() would never exit.
    // Now only counts non-deleted records as true duplicates.
    boolean existsByReturnNoAndIsDeletedFalse(String returnNo);

    List<VatReturn> findByVatRegistration_IdAndIsDeletedFalse(Long vatRegistrationId);
}