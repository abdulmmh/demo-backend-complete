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

    /** Used in generateBinNo() to detect collisions on the candidate BIN string. */
    boolean existsByBinNoAndIsDeletedFalse(String binNo);

    /** Business-level duplicate guard — one active VAT registration per business. */
    boolean existsByBusiness_IdAndIsDeletedFalse(Long businessId);

    /** Lookup by business — used for VAT status checks in BusinessVatStatusDTO. */
    Optional<VatRegistration> findByBusiness_IdAndIsDeletedFalse(Long businessId);

    /** Cascade soft-delete: find all VAT registrations for a given taxpayer. */
    List<VatRegistration> findByTaxpayer_IdAndIsDeletedFalse(Long taxpayerId);

    /**
     * Counts all non-deleted registrations in a given VAT zone.
     * Used by generateBinNo() to build a zone-scoped sequential BIN number.
     *
     * Example: for zoneId=5, count returns 3 → next BIN sequence = 4.
     */
    long countByZoneIdAndIsDeletedFalse(Long zoneId);
}
