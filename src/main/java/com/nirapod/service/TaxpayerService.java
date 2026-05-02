package com.nirapod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.model.Taxpayer;

/**
 * Enterprise refactor — TaxpayerService no longer syncs denormalized names
 * to other entities. All modules (Tin, Business, Payment, etc.) now resolve
 * taxpayer name at query time via a FK JOIN. There is nothing to sync.
 */
@Service(value = "taxpayerService")
@Transactional
public class TaxpayerService {

    @Autowired TaxpayerDAO taxpayerDAO;

    public void create(Taxpayer taxpayer) {
        taxpayerDAO.save(taxpayer);
    }

    public List<Taxpayer> getAll() {
        return taxpayerDAO.getAll();
    }

    public List<Taxpayer> getByStatus(String status) {
        return taxpayerDAO.getByStatus(status);
    }

    public List<Taxpayer> search(String query) {
        return taxpayerDAO.search(query);
    }

    public Taxpayer getById(Long id) {
        return taxpayerDAO.getById(id);
    }

    /**
     * Load the existing record, apply only editable fields, save.
     * Never merges raw request body — prevents null-wiping of tinNumber, createdAt etc.
     * No name sync needed — all other entities resolve name via JOIN to this table.
     */
    public Taxpayer update(Long id, Taxpayer incoming) {

        Taxpayer existing = taxpayerDAO.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + id);
        }

        existing.setTaxpayerType(incoming.getTaxpayerType());
        existing.setFullName(incoming.getFullName());
        existing.setNid(incoming.getNid());
        existing.setFathersName(incoming.getFathersName());
        existing.setMothersName(incoming.getMothersName());
        existing.setDateOfBirth(incoming.getDateOfBirth());
        existing.setGender(incoming.getGender());
        existing.setProfession(incoming.getProfession());
        existing.setCompanyName(incoming.getCompanyName());
        existing.setIncorporationDate(incoming.getIncorporationDate());
        existing.setTradeLicenseNo(incoming.getTradeLicenseNo());
        existing.setRjscNo(incoming.getRjscNo());
        existing.setNatureOfBusiness(incoming.getNatureOfBusiness());
        existing.setAuthorizedPersonName(incoming.getAuthorizedPersonName());
        existing.setAuthorizedPersonNid(incoming.getAuthorizedPersonNid());
        existing.setAuthorizedPersonDesignation(incoming.getAuthorizedPersonDesignation());
        existing.setEmail(incoming.getEmail());
        existing.setPhone(incoming.getPhone());
        existing.setPresentAddress(incoming.getPresentAddress());
        existing.setPermanentAddress(incoming.getPermanentAddress());
        existing.setSameAsPermanent(incoming.getSameAsPermanent());
        existing.setStatus(incoming.getStatus());
        // tinNumber, registrationDate, createdAt intentionally NOT touched

        return taxpayerDAO.update(existing);
    }

    public void delete(Long id) {
        Taxpayer taxpayer = taxpayerDAO.getById(id);
        if (taxpayer != null) {
            taxpayer.setStatus("Inactive");
            taxpayerDAO.update(taxpayer);
        }
    }

    public byte[] exportTaxpayersToCsv() {
        List<Taxpayer> taxpayers = taxpayerDAO.getAll();
        StringBuilder csv = new StringBuilder();
        csv.append("Taxpayer Name,Taxpayer Type,NID / Trade License,Phone,Email\n");
        for (Taxpayer t : taxpayers) {
            String name = t.getFullName() != null ? t.getFullName()
                    : (t.getCompanyName() != null ? t.getCompanyName() : "N/A");
            String identifier = t.getNid() != null ? t.getNid()
                    : (t.getTradeLicenseNo() != null ? t.getTradeLicenseNo() : "N/A");
            csv.append("\"").append(name).append("\",")
               .append(t.getTaxpayerType() != null ? t.getTaxpayerType() : "N/A").append(",")
               .append(identifier).append(",")
               .append(t.getPhone() != null ? t.getPhone() : "N/A").append(",")
               .append(t.getEmail() != null ? t.getEmail() : "N/A").append("\n");
        }
        return csv.toString().getBytes();
    }
}
