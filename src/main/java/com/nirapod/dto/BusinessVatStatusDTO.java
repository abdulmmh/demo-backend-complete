package com.nirapod.dto;

import com.nirapod.model.Business;
import com.nirapod.model.VatRegistration;

public class BusinessVatStatusDTO {

    private Long   id;
    private String businessRegNo;
    private String businessName;
    private String ownerName;
    private String tradeLicenseNo;

    // IDs — needed for dropdown pre-selection in Angular
    private Long   businessTypeId;
    private String businessTypeName;
    private Long   businessCategoryId;
    private String businessCategoryName;
    private Long   divisionId;
    private String divisionName;
    private Long   districtId;
    private String districtName;

    private String email;
    private String phone;
    private String address;
    private String status;
    private Double annualTurnover;

    // VAT status — null when not yet registered
    private boolean vatRegistered;
    private String  binNo;
    private String  vatStatus;

    // ── Constructor — takes full entity objects, extracts everything needed ──
    public BusinessVatStatusDTO(Business b, VatRegistration vat) {
        this.id             = b.getId();
        this.businessRegNo  = b.getBusinessRegNo();
        this.businessName   = b.getBusinessName();
        this.ownerName      = b.getOwnerName();
        this.tradeLicenseNo = b.getTradeLicenseNo();
        this.email          = b.getEmail();
        this.phone          = b.getPhone();
        this.address        = b.getAddress();
        this.status         = b.getStatus();
        this.annualTurnover = b.getAnnualTurnover() != null
                                ? b.getAnnualTurnover().doubleValue()
                                : 0.0;

        if (b.getBusinessType() != null) {
            this.businessTypeId   = b.getBusinessType().getId();
            this.businessTypeName = b.getBusinessType().getTypeName();
        }

        if (b.getBusinessCategory() != null) {
            this.businessCategoryId   = b.getBusinessCategory().getId();
            this.businessCategoryName = b.getBusinessCategory().getCategoryName();
        }

        if (b.getDivision() != null) {
            this.divisionId   = b.getDivision().getId();
            this.divisionName = b.getDivision().getName();
        }

        if (b.getDistrict() != null) {
            this.districtId   = b.getDistrict().getId();
            this.districtName = b.getDistrict().getName();
        }

        this.vatRegistered = vat != null;
        this.binNo         = vat != null ? vat.getBinNo()  : null;
        this.vatStatus     = vat != null ? vat.getStatus() : null;
    }

    // ── Getters ──

    public Long    getId()                   { return id; }
    public String  getBusinessRegNo()        { return businessRegNo; }
    public String  getBusinessName()         { return businessName; }
    public String  getOwnerName()            { return ownerName; }
    public String  getTradeLicenseNo()       { return tradeLicenseNo; }
    public Long    getBusinessTypeId()       { return businessTypeId; }
    public String  getBusinessTypeName()     { return businessTypeName; }
    public Long    getBusinessCategoryId()   { return businessCategoryId; }
    public String  getBusinessCategoryName() { return businessCategoryName; }
    public Long    getDivisionId()           { return divisionId; }
    public String  getDivisionName()         { return divisionName; }
    public Long    getDistrictId()           { return districtId; }
    public String  getDistrictName()         { return districtName; }
    public String  getEmail()                { return email; }
    public String  getPhone()                { return phone; }
    public String  getAddress()              { return address; }
    public String  getStatus()               { return status; }
    public Double  getAnnualTurnover()       { return annualTurnover; }
    public boolean isVatRegistered()         { return vatRegistered; }
    public String  getBinNo()                { return binNo; }
    public String  getVatStatus()            { return vatStatus; }
}