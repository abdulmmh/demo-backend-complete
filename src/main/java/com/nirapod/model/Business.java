package com.nirapod.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "business")
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    @JsonIgnoreProperties("businesses")
    private Taxpayer taxpayer;

    @Column(unique = true)
    private String businessRegNo;

    @Column(nullable = false)
    private String businessName;


    @Column(nullable = true)
    private String tinNumber;



    @Column(nullable = false)
    private String ownerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    private Division division;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_type_id")
    private BusinessType businessType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_category_id")
    private BusinessCategory businessCategory;

    private String tradeLicenseNo;
    private String email;
    private String phone;
    @Column(nullable = false)
    private String status;

    @Column(length = 1000)
    private String address;

    private LocalDate incorporationDate;
    private LocalDate registrationDate;
    private LocalDate expiryDate;


    @Column
    private Double annualTurnover;

    private Integer numberOfEmployees;

    @Column(length = 1000)
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;
    
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = "Active";
        if (this.businessRegNo == null || this.businessRegNo.isEmpty())
            this.businessRegNo = "BUS-" + UUID.randomUUID()
                                              .toString()
                                              .substring(0, 8)
                                              .toUpperCase();
    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Taxpayer getTaxpayer() { return taxpayer; }
    public void setTaxpayer(Taxpayer taxpayer) { this.taxpayer = taxpayer; }

    public String getBusinessRegNo() { return businessRegNo; }
    public void setBusinessRegNo(String businessRegNo) { this.businessRegNo = businessRegNo; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }

    public BusinessType getBusinessType() { return businessType; }
    public void setBusinessType(BusinessType businessType) { this.businessType = businessType; }

    public BusinessCategory getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(BusinessCategory businessCategory) { this.businessCategory = businessCategory; }

    public String getTradeLicenseNo() { return tradeLicenseNo; }
    public void setTradeLicenseNo(String tradeLicenseNo) { this.tradeLicenseNo = tradeLicenseNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getIncorporationDate() { return incorporationDate; }
    public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Double getAnnualTurnover() { return annualTurnover; }
    public void setAnnualTurnover(Double annualTurnover) { this.annualTurnover = annualTurnover; }

    public Integer getNumberOfEmployees() { return numberOfEmployees; }
    public void setNumberOfEmployees(Integer numberOfEmployees) { this.numberOfEmployees = numberOfEmployees; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}