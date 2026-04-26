package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "vat_registrations")
public class VatRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── BIN (Business Identification Number) — auto-generated in Service ──
    @Column(name = "bin_no", nullable = false, unique = true, length = 20)
    private String binNo;

    // ── Business Info ──────────────────────────────────────────────────────
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "vat_category", nullable = false, length = 20)
    private String vatCategory;          // Standard | Zero Rated | Exempt | Special

    @Column(name = "business_type", length = 50)
    private String businessType;

    @Column(name = "business_category", length = 50)
    private String businessCategory;

    @Column(name = "trade_license_no", length = 30)
    private String tradeLicenseNo;

    // ── VAT Authority ──────────────────────────────────────────────────────
    @Column(name = "vat_zone", nullable = false, length = 30)
    private String vatZone;

    @Column(name = "vat_circle", nullable = false, length = 30)
    private String vatCircle;

    // ── Dates ──────────────────────────────────────────────────────────────
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // ── Financial ─────────────────────────────────────────────────────────
    @Column(name = "annual_turnover", nullable = false)
    private Double annualTurnover = 0.0;

    // ── Contact ───────────────────────────────────────────────────────────
    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String district;

    @Column(length = 50)
    private String division;

    // ── Status ────────────────────────────────────────────────────────────
    @Column(nullable = false, length = 20)
    private String status = "Pending";   // Active | Inactive | Pending | Suspended | Cancelled

    @Column(length = 500)
    private String remarks;

    // ── Soft Delete ───────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── Relationship: Taxpayer ─────────────────────────────────────────────
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @Transient
    private Long taxpayerId;

    
    @Column(name = "tin_number", nullable = false, length = 30)
    private String tinNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "taxpayer"})
    private Business business;

    @Transient
    private Long businessId;

    // ── Getters & Setters ─────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBinNo() { return binNo; }
    public void setBinNo(String binNo) { this.binNo = binNo; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getVatCategory() { return vatCategory; }
    public void setVatCategory(String vatCategory) { this.vatCategory = vatCategory; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(String businessCategory) { this.businessCategory = businessCategory; }

    public String getTradeLicenseNo() { return tradeLicenseNo; }
    public void setTradeLicenseNo(String tradeLicenseNo) { this.tradeLicenseNo = tradeLicenseNo; }

    public String getVatZone() { return vatZone; }
    public void setVatZone(String vatZone) { this.vatZone = vatZone; }

    public String getVatCircle() { return vatCircle; }
    public void setVatCircle(String vatCircle) { this.vatCircle = vatCircle; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Double getAnnualTurnover() { return annualTurnover; }
    public void setAnnualTurnover(Double annualTurnover) { this.annualTurnover = annualTurnover; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Taxpayer getTaxpayer() { return taxpayer; }
    public void setTaxpayer(Taxpayer taxpayer) { this.taxpayer = taxpayer; }

    public Long getTaxpayerId() { return taxpayerId; }
    public void setTaxpayerId(Long taxpayerId) { this.taxpayerId = taxpayerId; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
	
    public Business getBusiness() {
		return business;
	}
	public void setBusiness(Business business) {
		this.business = business;
	}
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
    
}
