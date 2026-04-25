package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "tax_circles")
public class TaxCircle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_zone_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "taxCircles", "district"})
    private TaxZone taxZone;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TaxZone getTaxZone() { return taxZone; }
    public void setTaxZone(TaxZone taxZone) { this.taxZone = taxZone; }
}