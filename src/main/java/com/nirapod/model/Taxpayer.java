package com.nirapod.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "taxpayer")
@Table(name = "taxpayers")
public class Taxpayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
   
    @Column(unique = true)
    private String tinNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taxpayer_type_id")
    private TaxpayerType taxpayerType;

    // ─── Individual Fields ───
    private String fullName;
    private String nid;
    private String fathersName;
    private String mothersName;
    private LocalDate dateOfBirth;
    private String profession;

    // ─── Company Fields ───
    private String companyName;
    private LocalDate incorporationDate;
    private String tradeLicenseNo;
    private String rjscNo;
    private String natureOfBusiness;
    private String authorizedPersonName;
    private String authorizedPersonNid;
    private String authorizedPersonDesignation;

    // ─── Contact Info ───
    private String email;
    private String phone;

    // ─── Address Info (Embedded) ───
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "division", column = @Column(name = "present_division")),
        @AttributeOverride(name = "district", column = @Column(name = "present_district")),
        @AttributeOverride(name = "thana", column = @Column(name = "present_thana")),
        @AttributeOverride(name = "roadVillage", column = @Column(name = "present_road_village"))
    })
    private Address presentAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "division", column = @Column(name = "permanent_division")),
        @AttributeOverride(name = "district", column = @Column(name = "permanent_district")),
        @AttributeOverride(name = "thana", column = @Column(name = "permanent_thana")),
        @AttributeOverride(name = "roadVillage", column = @Column(name = "permanent_road_village"))
    })
    private Address permanentAddress;

    private Boolean sameAsPermanent;

    // ─── Meta Info ───
    private String status; 
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "taxpayer", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("taxpayer")
    private List<Business> businesses;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // ─── Getters and Setters ───
   
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public TaxpayerType getTaxpayerType() { return taxpayerType; }
    public void setTaxpayerType(TaxpayerType taxpayerType) { this.taxpayerType = taxpayerType; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNid() { return nid; }
    public void setNid(String nid) { this.nid = nid; }

    public String getFathersName() { return fathersName; }
    public void setFathersName(String fathersName) { this.fathersName = fathersName; }

    public String getMothersName() { return mothersName; }
    public void setMothersName(String mothersName) { this.mothersName = mothersName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDate getIncorporationDate() { return incorporationDate; }
    public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }

    public String getTradeLicenseNo() { return tradeLicenseNo; }
    public void setTradeLicenseNo(String tradeLicenseNo) { this.tradeLicenseNo = tradeLicenseNo; }

    public String getRjscNo() { return rjscNo; }
    public void setRjscNo(String rjscNo) { this.rjscNo = rjscNo; }

    public String getNatureOfBusiness() { return natureOfBusiness; }
    public void setNatureOfBusiness(String natureOfBusiness) { this.natureOfBusiness = natureOfBusiness; }

    public String getAuthorizedPersonName() { return authorizedPersonName; }
    public void setAuthorizedPersonName(String authorizedPersonName) { this.authorizedPersonName = authorizedPersonName; }

    public String getAuthorizedPersonNid() { return authorizedPersonNid; }
    public void setAuthorizedPersonNid(String authorizedPersonNid) { this.authorizedPersonNid = authorizedPersonNid; }

    public String getAuthorizedPersonDesignation() { return authorizedPersonDesignation; }
    public void setAuthorizedPersonDesignation(String authorizedPersonDesignation) { this.authorizedPersonDesignation = authorizedPersonDesignation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Address getPresentAddress() { return presentAddress; }
    public void setPresentAddress(Address presentAddress) { this.presentAddress = presentAddress; }

    public Address getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(Address permanentAddress) { this.permanentAddress = permanentAddress; }

    public Boolean getSameAsPermanent() { return sameAsPermanent; }
    public void setSameAsPermanent(Boolean sameAsPermanent) { this.sameAsPermanent = sameAsPermanent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public List<Business> getBusinesses() { return businesses; }
    public void setBusinesses(List<Business> businesses) { this.businesses = businesses; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}