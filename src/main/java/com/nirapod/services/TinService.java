package com.nirapod.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.TinDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.model.Tin;
import com.nirapod.model.Taxpayer;

@Service
@Transactional
public class TinService {

    @Autowired
    private TinDAO tinDAO;

    @Autowired
    private TaxpayerDAO taxpayerDAO;


    public Tin createTin(Tin tin) {

        if (tin.getTaxpayerId() == null) {
            throw new IllegalArgumentException("taxpayerId is required to issue a TIN.");
        }

        // Duplicate check:
        boolean alreadyExists = tinDAO.findByTaxpayerId(tin.getTaxpayerId()).isPresent();
        if (alreadyExists) {
            throw new IllegalStateException(
                "A TIN has already been issued for taxpayer ID: " + tin.getTaxpayerId()
            );
        }


        tin.setTinNumber("PENDING");
        Tin savedTin = tinDAO.saveAndFlush(tin);


        String generatedTin = generateTIN(savedTin.getId());
        savedTin.setTinNumber(generatedTin);
        tinDAO.save(savedTin);


        Taxpayer taxpayer = taxpayerDAO.getById(tin.getTaxpayerId());
        if (taxpayer != null) {
            taxpayer.setTinNumber(generatedTin);
            taxpayerDAO.update(taxpayer);
        }

        return savedTin;
    }

    private String generateTIN(long id) {
        return "TIN-" + String.format("%09d", id);
    }

    public List<Tin> getAll() {
        return tinDAO.findAll();
    }

    public Tin getById(Long id) {
        return tinDAO.findById(id).orElse(null);
    }

    public Tin updateTin(Long id, Tin updatedTin) {
        return tinDAO.findById(id).map(existing -> {
            existing.setTaxpayerName(updatedTin.getTaxpayerName());
            existing.setTinCategory(updatedTin.getTinCategory());
            existing.setEmail(updatedTin.getEmail());
            existing.setPhone(updatedTin.getPhone());
            existing.setAddress(updatedTin.getAddress());
            existing.setDistrict(updatedTin.getDistrict());
            existing.setDivision(updatedTin.getDivision());
            existing.setTaxZone(updatedTin.getTaxZone());
            existing.setTaxCircle(updatedTin.getTaxCircle());
            existing.setStatus(updatedTin.getStatus());
            existing.setRemarks(updatedTin.getRemarks());
            return tinDAO.save(existing);
        }).orElse(null);
    }

    // Soft delete
    public void deleteTin(Long id) {
        tinDAO.findById(id).ifPresent(tin -> {
            tin.setStatus("Inactive");
            tinDAO.save(tin);
        });
    }
    
    public byte[] exportTinsToCsv() {
        
        List<Tin> tins = tinDAO.findAll(); 
        StringBuilder csvBuilder = new StringBuilder();
        
        // CSV Header
        csvBuilder.append("TIN Number,Taxpayer Name,Category,Tax Zone,Tax Circle,Issue Date,Status\n");
        
        // Data Rows
        for (Tin tin : tins) {
            csvBuilder.append(tin.getTinNumber() != null ? tin.getTinNumber() : "N/A").append(",")
                      
            		  .append("\"").append(tin.getTaxpayerName() != null ? tin.getTaxpayerName() : "N/A").append("\",")                      .append(tin.getTinCategory() != null ? tin.getTinCategory() : "N/A").append(",")
                      .append(tin.getTaxZone() != null ? tin.getTaxZone() : "N/A").append(",")
                      .append(tin.getTaxCircle() != null ? tin.getTaxCircle() : "N/A").append(",")
                      .append(tin.getIssuedDate() != null ? tin.getIssuedDate() : "N/A").append(",")
                      .append(tin.getStatus() != null ? tin.getStatus() : "N/A").append("\n");
        }
        
        return csvBuilder.toString().getBytes();
    }
    
    
}