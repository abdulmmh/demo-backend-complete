package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "taxZone")
@Table(name = "tax_zones")
public class TaxZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "division"})
    private District district;

    @OneToMany(mappedBy = "taxZone", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("taxZone")
    private List<TaxCircle> taxCircles;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }

    public List<TaxCircle> getTaxCircles() { return taxCircles; }
    public void setTaxCircles(List<TaxCircle> taxCircles) { this.taxCircles = taxCircles; }
}