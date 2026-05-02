package com.nirapod.dto.response;

import com.nirapod.model.Tin;
import java.time.LocalDate;

/**
 * Response DTO for TIN.
 * taxpayerName is resolved fresh from the Taxpayer FK at query time — never stored.
 */
public class TinResponse {
    private Long id;
    private String tinNumber;
    private Long taxpayerId;
    private String taxpayerName;
    private String tinCategory;
    private String nid;
    private String passportNo;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDate incorporationDate;
    private String email;
    private String phone;
    private String address;
    private String district;
    private String division;
    private String taxZone;
    private String taxCircle;
    private LocalDate issuedDate;
    private LocalDate lastUpdated;
    private String status;
    private String remarks;

    public static TinResponse from(Tin tin) {
        TinResponse r = new TinResponse();
        r.id        = tin.getId();
        r.tinNumber = tin.getTinNumber();
        if (tin.getTaxpayer() != null) {
            r.taxpayerId   = tin.getTaxpayer().getId();
            r.taxpayerName = tin.getTaxpayer().getFullName() != null
                ? tin.getTaxpayer().getFullName()
                : tin.getTaxpayer().getCompanyName();
        }
        r.tinCategory       = tin.getTinCategory();
        r.nid               = tin.getNid();
        r.passportNo        = tin.getPassportNo();
        r.dateOfBirth       = tin.getDateOfBirth();
        r.gender            = tin.getGender();
        r.incorporationDate = tin.getIncorporationDate();
        r.email             = tin.getEmail();
        r.phone             = tin.getPhone();
        r.address           = tin.getAddress();
        r.district          = tin.getDistrict();
        r.division          = tin.getDivision();
        r.taxZone           = tin.getTaxZone();
        r.taxCircle         = tin.getTaxCircle();
        r.issuedDate        = tin.getIssuedDate();
        r.lastUpdated       = tin.getLastUpdated();
        r.status            = tin.getStatus();
        r.remarks           = tin.getRemarks();
        return r;
    }

    public Long getId() { return id; }
    public String getTinNumber() { return tinNumber; }
    public Long getTaxpayerId() { return taxpayerId; }
    public String getTaxpayerName() { return taxpayerName; }
    public String getTinCategory() { return tinCategory; }
    public String getNid() { return nid; }
    public String getPassportNo() { return passportNo; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public LocalDate getIncorporationDate() { return incorporationDate; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDistrict() { return district; }
    public String getDivision() { return division; }
    public String getTaxZone() { return taxZone; }
    public String getTaxCircle() { return taxCircle; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public LocalDate getLastUpdated() { return lastUpdated; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
}
