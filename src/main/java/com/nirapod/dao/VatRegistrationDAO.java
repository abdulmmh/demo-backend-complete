package com.nirapod.dao;

import com.nirapod.model.VatRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VatRegistrationDAO extends JpaRepository<VatRegistration, Long> {

    // List all active (non-deleted) registrations
    List<VatRegistration> findByIsDeletedFalse();

    // Safe fetch by primary key (excludes soft-deleted)
    Optional<VatRegistration> findByIdAndIsDeletedFalse(Long id);

    // Duplicate guard — one active VAT registration per TIN
    boolean existsByTinNumberAndIsDeletedFalse(String tinNumber);

    // Duplicate guard — unique BIN across all records
    boolean existsByBinNoAndIsDeletedFalse(String binNo);

    // Find by taxpayer (for cascading soft-delete use case)
    List<VatRegistration> findByTaxpayer_IdAndIsDeletedFalse(Long taxpayerId);
}
