package com.nirapod.dto.request;

import java.time.LocalDate;

/**
 * DTO for POST /api/vat-registrations.
 *
 * Decouples the HTTP contract from the JPA entity.
 * The *Id fields are resolved to their entity counterparts inside
 * VatRegistrationService.createRegistration().
 *
 * businessId is nullable: Company taxpayers register directly without
 * a business record. Non-company (Individual / Firm) taxpayers MUST supply one.
 */
public class VatRegistrationCreateRequest {

    // ── Foreign-key IDs resolved server-side ──────────────────────────────
    private Long taxpayerId;
    private Long businessId;    // null for Company taxpayers

    private Long vatZoneId;
    private Long vatCircleId;

    private Long districtId;    // optional — derived from business for non-company
    private Long divisionId;    // optional — derived from district

    // ── VAT classification ─────────────────────────────────────────────────
    private String vatCategory;

    // ── Dates ──────────────────────────────────────────────────────────────
    private LocalDate registrationDate;
    private LocalDate effectiveDate;  // must be >= registrationDate
    private LocalDate expiryDate;

    // ── Optional notes ─────────────────────────────────────────────────────
    private String remarks;

    // ── Getters & Setters ──────────────────────────────────────────────────

    public Long getTaxpayerId()              { return taxpayerId; }
    public void setTaxpayerId(Long v)        { this.taxpayerId = v; }

    public Long getBusinessId()              { return businessId; }
    public void setBusinessId(Long v)        { this.businessId = v; }

    public Long getVatZoneId()               { return vatZoneId; }
    public void setVatZoneId(Long v)         { this.vatZoneId = v; }

    public Long getVatCircleId()             { return vatCircleId; }
    public void setVatCircleId(Long v)       { this.vatCircleId = v; }

    public Long getDistrictId()              { return districtId; }
    public void setDistrictId(Long v)        { this.districtId = v; }

    public Long getDivisionId()              { return divisionId; }
    public void setDivisionId(Long v)        { this.divisionId = v; }

    public String getVatCategory()           { return vatCategory; }
    public void setVatCategory(String v)     { this.vatCategory = v; }

    public LocalDate getRegistrationDate()   { return registrationDate; }
    public void setRegistrationDate(LocalDate v) { this.registrationDate = v; }

    public LocalDate getEffectiveDate()      { return effectiveDate; }
    public void setEffectiveDate(LocalDate v) { this.effectiveDate = v; }

    public LocalDate getExpiryDate()         { return expiryDate; }
    public void setExpiryDate(LocalDate v)   { this.expiryDate = v; }

    public String getRemarks()               { return remarks; }
    public void setRemarks(String v)         { this.remarks = v; }
}
