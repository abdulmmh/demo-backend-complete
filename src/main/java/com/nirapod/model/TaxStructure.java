package com.nirapod.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity(name ="taxStructure")
@Table(name = "tax_structures")
public class TaxStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String taxCode;

    @Column(nullable = false, unique = true)
    private String taxName;

    @Column(nullable = false)
    private String taxType;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private String applicableTo;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String status;

    private String createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "taxStructure")
    @JsonIgnore
    private List<Product> products;

    public TaxStructure() {
        super();
    }

    public TaxStructure(Integer id, String taxCode, String taxName, String taxType, Double rate, String applicableTo,
                        LocalDate effectiveDate, LocalDate expiryDate, String description, String status,
                        String createdBy, LocalDateTime createdAt) {
        super();
        this.id = id;
        this.taxCode = taxCode;
        this.taxName = taxName;
        this.taxType = taxType;
        this.rate = rate;
        this.applicableTo = applicableTo;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.description = description;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(String applicableTo) {
        this.applicableTo = applicableTo;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}