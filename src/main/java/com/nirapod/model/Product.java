package com.nirapod.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity(name = "product")
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, unique = true)
    private String hsCode;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String taxType;

    @ManyToOne
    @JoinColumn(name = "tax_structure_id", nullable = false)
    private TaxStructure taxStructure;

    @Column(nullable = false)
    private Double taxRate;

    @Column(nullable = false)
    private String unit;

    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Product() {
        super();
    }

    public Product(int id, String productCode, String productName, String hsCode, String category, String taxType,
                   TaxStructure taxStructure, Double taxRate, String unit, String description, String status,
                   LocalDateTime createdAt) {
        super();
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.hsCode = hsCode;
        this.category = category;
        this.taxType = taxType;
        this.taxStructure = taxStructure;
        this.taxRate = taxRate;
        this.unit = unit;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.productCode == null || this.productCode.isEmpty()) {
            this.productCode = "PRD-" + System.currentTimeMillis();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public TaxStructure getTaxStructure() {
        return taxStructure;
    }

    public void setTaxStructure(TaxStructure taxStructure) {
        this.taxStructure = taxStructure;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}