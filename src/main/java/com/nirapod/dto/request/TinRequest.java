package com.nirapod.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request DTO for creating/updating a TIN.
 * taxpayerName is NOT accepted here — it is always sourced from the Taxpayer record.
 */
public class TinRequest {

    @NotNull(message = "taxpayerId is required")
    private Long taxpayerId;

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
    private String status;
    private String remarks;

    public Long getTaxpayerId() { return taxpayerId; }
    public void setTaxpayerId(Long taxpayerId) { this.taxpayerId = taxpayerId; }
    public String getTinCategory() { return tinCategory; }
    public void setTinCategory(String tinCategory) { this.tinCategory = tinCategory; }
    public String getNid() { return nid; }
    public void setNid(String nid) { this.nid = nid; }
    public String getPassportNo() { return passportNo; }
    public void setPassportNo(String passportNo) { this.passportNo = passportNo; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getIncorporationDate() { return incorporationDate; }
    public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }
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
    public String getTaxZone() { return taxZone; }
    public void setTaxZone(String taxZone) { this.taxZone = taxZone; }
    public String getTaxCircle() { return taxCircle; }
    public void setTaxCircle(String taxCircle) { this.taxCircle = taxCircle; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
