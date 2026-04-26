package com.nirapod.services;

import com.nirapod.dao.BusinessDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.VatRegistrationDAO;
import com.nirapod.model.Business;
import com.nirapod.model.Taxpayer;
import com.nirapod.model.VatRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VatRegistrationService {

    @Autowired
    private VatRegistrationDAO vatRegistrationDAO;

    // TaxpayerDAO is a custom EntityManager DAO — getById() returns object directly (NOT Optional)
    @Autowired
    private TaxpayerDAO taxpayerDAO;
    
    @Autowired
    private BusinessDAO businessDAO;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
	public VatRegistration createRegistration(VatRegistration vatReg) {
	
	    if (vatReg.getTaxpayerId() == null) {
	        throw new IllegalArgumentException("taxpayerId is required.");
	    }
	    if (vatReg.getBusinessId() == null) {
	        throw new IllegalArgumentException("businessId is required to link a VAT registration to a specific business.");
	    }
	    
	    if (vatReg.getBusinessId() != null) {
	        Business business = businessDAO.getById(vatReg.getBusinessId());
	        if (business == null) {
	            throw new IllegalArgumentException("Business not found: " + vatReg.getBusinessId());
	        }
	        vatReg.setBusiness(business);

	        // Switch duplicate check from TIN-level to business-level
	        if (vatRegistrationDAO.existsByBusiness_IdAndIsDeletedFalse(business.getId())) {
	            throw new IllegalStateException(
	                "VAT already registered for: " + business.getBusinessName());
	        }
	    }
	    
	    Taxpayer taxpayer = taxpayerDAO.getById(vatReg.getTaxpayerId());
	    if (taxpayer == null) {
	        throw new IllegalArgumentException("Taxpayer not found: " + vatReg.getTaxpayerId());
	    }
	
	    // Resolve business FK
	    Business business = businessDAO.getById(vatReg.getBusinessId());
	    if (business == null || business.isDeleted()) {
	        throw new IllegalArgumentException("Business not found: " + vatReg.getBusinessId());
	    }
	
	    // Guard: business must belong to this taxpayer
	    if (!business.getTaxpayer().getId().equals(taxpayer.getId())) {
	        throw new IllegalStateException("Business does not belong to this taxpayer.");
	    }
	
	    // Duplicate guard: one BIN per business
	    if (vatRegistrationDAO.existsByBusiness_IdAndIsDeletedFalse(business.getId())) {
	        throw new IllegalStateException(
	            "Business \"" + business.getBusinessName() + "\" already has a VAT registration (BIN).");
	    }
	
	    vatReg.setTaxpayer(taxpayer);
	    vatReg.setBusiness(business);
	    vatReg.setTinNumber(taxpayer.getTinNumber());
	    vatReg.setBinNo(generateBinNo());
	    if (vatReg.getRegistrationDate() == null) vatReg.setRegistrationDate(LocalDate.now());
	    vatReg.setStatus("Pending");
	
	    return vatRegistrationDAO.save(vatReg);
	}

    // ── Read All ──────────────────────────────────────────────────────────────

    public List<VatRegistration> getAllRegistrations() {
        return vatRegistrationDAO.findByIsDeletedFalse();
    }

    // ── Read by ID ────────────────────────────────────────────────────────────

    public VatRegistration getRegistrationById(Long id) {
        return vatRegistrationDAO.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "VAT registration not found with ID: " + id));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public VatRegistration updateRegistration(Long id, VatRegistration updatedData) {
        VatRegistration existing = getRegistrationById(id);

        // Updateable fields — binNo, tinNumber, taxpayer FK are NOT updatable
        existing.setBusinessName(updatedData.getBusinessName());
        existing.setOwnerName(updatedData.getOwnerName());
        existing.setVatCategory(updatedData.getVatCategory());
        existing.setBusinessType(updatedData.getBusinessType());
        existing.setBusinessCategory(updatedData.getBusinessCategory());
        existing.setTradeLicenseNo(updatedData.getTradeLicenseNo());
        existing.setVatZone(updatedData.getVatZone());
        existing.setVatCircle(updatedData.getVatCircle());
        existing.setRegistrationDate(updatedData.getRegistrationDate());
        existing.setEffectiveDate(updatedData.getEffectiveDate());
        existing.setExpiryDate(updatedData.getExpiryDate());
        existing.setAnnualTurnover(updatedData.getAnnualTurnover());
        existing.setEmail(updatedData.getEmail());
        existing.setPhone(updatedData.getPhone());
        existing.setAddress(updatedData.getAddress());
        existing.setDistrict(updatedData.getDistrict());
        existing.setDivision(updatedData.getDivision());
        existing.setStatus(updatedData.getStatus());
        existing.setRemarks(updatedData.getRemarks());

        return vatRegistrationDAO.save(existing);
    }

    // ── Soft Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteRegistration(Long id) {
        VatRegistration existing = getRegistrationById(id);
        existing.setDeleted(true);
        vatRegistrationDAO.save(existing);
    }

    // ── BIN Generation ────────────────────────────────────────────────────────
    // Format: BIN-{YEAR}-{6-digit UUID substring}
    // Uses UUID to avoid race conditions on concurrent inserts

    private String generateBinNo() {
        String year = String.valueOf(LocalDate.now().getYear());
        String unique = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        String candidate = "BIN-" + year + "-" + unique;

        // Extremely unlikely collision, but guard anyway
        if (vatRegistrationDAO.existsByBinNoAndIsDeletedFalse(candidate)) {
            candidate = "BIN-" + year + "-" + UUID.randomUUID()
                    .toString().replace("-", "").substring(0, 6).toUpperCase();
        }
        return candidate;
    }
}
