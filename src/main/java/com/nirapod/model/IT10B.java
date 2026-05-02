package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity(name = "it10bAssetsLiabilitie")
@Table(name = "it_10b_assets_liabilities")
public class IT10B {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Assets ────────────────────────────────────────────────

    @Column(nullable = false)
    private Double nonAgriculturalProperty = 0.0;   

    @Column(nullable = false)
    private Double agriculturalProperty = 0.0;

    @Column(nullable = false)
    private Double investments = 0.0;              

    @Column(nullable = false)
    private Double motorVehicles = 0.0;

    @Column(nullable = false)
    private Double bankBalances = 0.0;              

    // ── Liabilities ───────────────────────────────────────────

    @Column(nullable = false)
    private Double personalLiabilities = 0.0;       

    // ── Net Wealth 

    @Column(nullable = false)
    private Double netWealth = 0.0;             

    // ── Soft Delete ───────────────────────────────────────────

    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── Relationship ──────────────────────────────────────────

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false, unique = true)
    private IncomeTaxReturn incomeTaxReturn;

 
    @Transient
    private Long returnId;

    // ── Getters & Setters ─────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getNonAgriculturalProperty() { return nonAgriculturalProperty; }
    public void setNonAgriculturalProperty(Double v) { this.nonAgriculturalProperty = v; }

    public Double getAgriculturalProperty() { return agriculturalProperty; }
    public void setAgriculturalProperty(Double v) { this.agriculturalProperty = v; }

    public Double getInvestments() { return investments; }
    public void setInvestments(Double v) { this.investments = v; }

    public Double getMotorVehicles() { return motorVehicles; }
    public void setMotorVehicles(Double v) { this.motorVehicles = v; }

    public Double getBankBalances() { return bankBalances; }
    public void setBankBalances(Double v) { this.bankBalances = v; }

    public Double getPersonalLiabilities() { return personalLiabilities; }
    public void setPersonalLiabilities(Double v) { this.personalLiabilities = v; }

    public Double getNetWealth() { return netWealth; }
    public void setNetWealth(Double v) { this.netWealth = v; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }

    public IncomeTaxReturn getIncomeTaxReturn() { return incomeTaxReturn; }
    public void setIncomeTaxReturn(IncomeTaxReturn incomeTaxReturn) {
        this.incomeTaxReturn = incomeTaxReturn;
    }

    public Long getReturnId() { return returnId; }
    public void setReturnId(Long returnId) { this.returnId = returnId; }
}
