package com.nirapod.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.model.Taxpayer;

@Service(value = "taxpayerService")
@Transactional
public class TaxpayerService {

    @Autowired
    TaxpayerDAO taxpayerDAO;

    public void create(Taxpayer taxpayer) {
        taxpayerDAO.save(taxpayer);
    }

    public List<Taxpayer> getAll() {
        return taxpayerDAO.getAll();
    }

    public List<Taxpayer> getByStatus(String status) {
        return taxpayerDAO.getByStatus(status);
    }

	//    DAO search query
    public List<Taxpayer> search(String query) {
        return taxpayerDAO.search(query);
    }

    public Taxpayer getById(Long id) {
        return taxpayerDAO.getById(id);
    }

    public void update(Taxpayer taxpayer) {
        taxpayerDAO.update(taxpayer);
    }

    // Soft delete 
    public void delete(Long id) {
        Taxpayer taxpayer = taxpayerDAO.getById(id);
        if (taxpayer != null) {
            taxpayer.setStatus("Inactive");
            taxpayerDAO.update(taxpayer);
        }
    }
    
	 // --- Export Taxpayers Data to CSV ---
    public byte[] exportTaxpayersToCsv() {
        List<Taxpayer> taxpayers = taxpayerDAO.getAll(); 
        StringBuilder csvBuilder = new StringBuilder();
        
        // CSV Header
        csvBuilder.append("Taxpayer Name,Taxpayer Type,NID / Trade License,Phone,Email\n");
        
        // Data Rows
        for (Taxpayer t : taxpayers) {
            String name = t.getFullName() != null ? t.getFullName() : (t.getCompanyName() != null ? t.getCompanyName() : "N/A");
            
            String identifier = t.getNid() != null ? t.getNid() : (t.getTradeLicenseNo() != null ? t.getTradeLicenseNo() : "N/A");

            csvBuilder.append("\"").append(name).append("\",")
                      .append(t.getTaxpayerType() != null ? t.getTaxpayerType() : "N/A").append(",")
                      .append(identifier).append(",")
                      .append(t.getPhone() != null ? t.getPhone() : "N/A").append(",")
                      .append(t.getEmail() != null ? t.getEmail() : "N/A").append("\n");
        }
        
        return csvBuilder.toString().getBytes();
    }
}