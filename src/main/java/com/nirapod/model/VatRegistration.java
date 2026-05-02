package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "vatRegistration")
@Table(name = "vat_registrations")
public class VatRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bin_no", nullable = false, unique = true, length = 30)
    private String binNo;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "vat_category", nullable = false, length = 20)
    private String vatCategory;

    @Column(name = "business_type", length = 50)
    private String businessType;

    @Column(name = "business_category", length = 50)
    private String businessCategory;

    @Column(name = "trade_license_no", length = 30)
    private String tradeLicenseNo;

    @Column(name = "vat_zone", nullable = false, length = 50)
    private String vatZone;

    @Column(name = "vat_circle", nullable = false, length = 50)
    private String vatCircle;

  
    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "annual_turnover", nullable = false)
    private Double annualTurnover = 0.0;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String district;

    @Column(length = 50)
    private String division;

    @Column(nullable = false, length = 20)
    private String status = "Pending";

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── Relationships ──────────────────────────────────────────────────────

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "taxpayer"})
    private Business business;

    @Column(name = "tin_number", nullable = false, length = 30)
    private String tinNumber;

    // ── @Transient fields — received from Angular, resolved in Service ─────

    @Transient private Long taxpayerId;
    @Transient private Long businessId;
    @Transient private Long vatZoneId;
    @Transient private Long vatCircleId;
    @Transient private Long districtId;
    @Transient private Long divisionId;

    // ── Getters & Setters ──────────────────────────────────────────────────

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getBinNo()                     { return binNo; }
    public void setBinNo(String binNo)           { this.binNo = binNo; }

    public String getBusinessName()              { return businessName; }
    public void setBusinessName(String v)        { this.businessName = v; }

    public String getOwnerName()                 { return ownerName; }
    public void setOwnerName(String v)           { this.ownerName = v; }

    public String getVatCategory()               { return vatCategory; }
    public void setVatCategory(String v)         { this.vatCategory = v; }

    public String getBusinessType()              { return businessType; }
    public void setBusinessType(String v)        { this.businessType = v; }

    public String getBusinessCategory()          { return businessCategory; }
    public void setBusinessCategory(String v)    { this.businessCategory = v; }

    public String getTradeLicenseNo()            { return tradeLicenseNo; }
    public void setTradeLicenseNo(String v)      { this.tradeLicenseNo = v; }

    public String getVatZone()                   { return vatZone; }
    public void setVatZone(String v)             { this.vatZone = v; }

    public String getVatCircle()                 { return vatCircle; }
    public void setVatCircle(String v)           { this.vatCircle = v; }

    public Long getZoneId()                      { return zoneId; }
    public void setZoneId(Long v)                { this.zoneId = v; }

    public LocalDate getRegistrationDate()       { return registrationDate; }
    public void setRegistrationDate(LocalDate v) { this.registrationDate = v; }

    public LocalDate getEffectiveDate()          { return effectiveDate; }
    public void setEffectiveDate(LocalDate v)    { this.effectiveDate = v; }

    public LocalDate getExpiryDate()             { return expiryDate; }
    public void setExpiryDate(LocalDate v)       { this.expiryDate = v; }

    public Double getAnnualTurnover()            { return annualTurnover; }
    public void setAnnualTurnover(Double v)      { this.annualTurnover = v; }

    public String getEmail()                     { return email; }
    public void setEmail(String v)               { this.email = v; }

    public String getPhone()                     { return phone; }
    public void setPhone(String v)               { this.phone = v; }

    public String getAddress()                   { return address; }
    public void setAddress(String v)             { this.address = v; }

    public String getDistrict()                  { return district; }
    public void setDistrict(String v)            { this.district = v; }

    public String getDivision()                  { return division; }
    public void setDivision(String v)            { this.division = v; }

    public String getStatus()                    { return status; }
    public void setStatus(String v)              { this.status = v; }

    public String getRemarks()                   { return remarks; }
    public void setRemarks(String v)             { this.remarks = v; }

    public boolean isDeleted()                   { return isDeleted; }
    public void setDeleted(boolean v)            { isDeleted = v; }

    public Taxpayer getTaxpayer()                { return taxpayer; }
    public void setTaxpayer(Taxpayer v)          { this.taxpayer = v; }

    public Business getBusiness()                { return business; }
    public void setBusiness(Business v)          { this.business = v; }

    public String getTinNumber()                 { return tinNumber; }
    public void setTinNumber(String v)           { this.tinNumber = v; }

    // Transient getters/setters
    public Long getTaxpayerId()                  { return taxpayerId; }
    public void setTaxpayerId(Long v)            { this.taxpayerId = v; }

    public Long getBusinessId()                  { return businessId; }
    public void setBusinessId(Long v)            { this.businessId = v; }

    public Long getVatZoneId()                   { return vatZoneId; }
    public void setVatZoneId(Long v)             { this.vatZoneId = v; }

    public Long getVatCircleId()                 { return vatCircleId; }
    public void setVatCircleId(Long v)           { this.vatCircleId = v; }

    public Long getDistrictId()                  { return districtId; }
    public void setDistrictId(Long v)            { this.districtId = v; }

    public Long getDivisionId()                  { return divisionId; }
    public void setDivisionId(Long v)            { this.divisionId = v; }
}
