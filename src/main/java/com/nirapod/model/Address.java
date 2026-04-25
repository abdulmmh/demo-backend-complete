package com.nirapod.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    
    private String division;
    private String district;
    private String thana;
    private String roadVillage;

    // ----- Getters and Setters -----
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getThana() { return thana; }
    public void setThana(String thana) { this.thana = thana; }

    public String getRoadVillage() { return roadVillage; }
    public void setRoadVillage(String roadVillage) { this.roadVillage = roadVillage; }
}