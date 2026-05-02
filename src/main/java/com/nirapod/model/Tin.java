package com.nirapod.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tins")
public class Tin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String tinNumber;

    // ── Relationship — name is always read via taxpayer.getFullName(), never stored ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    // ── REMOVED: private String taxpayerName  (was denormalized copy) ──

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

    @PrePersist
    public void prePersist() {
        if (this.status == null)   this.status = "Active";
        if (this.issuedDate == null) this.issuedDate = LocalDate.now();
        this.lastUpdated = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() { this.lastUpdated = LocalDate.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
    public Taxpayer getTaxpayer() { return taxpayer; }
    public void setTaxpayer(Taxpayer taxpayer) { this.taxpayer = taxpayer; }
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
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
