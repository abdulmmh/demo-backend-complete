package com.nirapod.dao;

import com.nirapod.model.VatRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VatRegistrationDAO extends JpaRepository<VatRegistration, Long> {

    List<VatRegistration> findByIsDeletedFalse();

    Optional<VatRegistration> findByIdAndIsDeletedFalse(Long id);

    // BIN uniqueness guard — used in generateBinNo() collision check
    boolean existsByBinNoAndIsDeletedFalse(String binNo);

    // Business-level duplicate guard — one active VAT registration per business
    boolean existsByBusiness_IdAndIsDeletedFalse(Long businessId);

    // Lookup by business — used for VAT status checks in BusinessVatStatusDTO
    Optional<VatRegistration> findByBusiness_IdAndIsDeletedFalse(Long businessId);

    // Cascade soft-delete: find all VAT registrations for a given taxpayer
    List<VatRegistration> findByTaxpayer_IdAndIsDeletedFalse(Long taxpayerId);

    // FIX: removed existsByTinNumberAndIsDeletedFalse(String tinNumber)
    // That method was declared but never called after the duplicate check was
    // switched from TIN-level to business-level. Dead code removed to avoid confusion.
}