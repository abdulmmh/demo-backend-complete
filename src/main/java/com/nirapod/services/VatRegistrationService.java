package com.nirapod.services;

import com.nirapod.dao.BusinessDAO;
import com.nirapod.dao.DistrictDAO;
import com.nirapod.dao.DivisionDAO;
import com.nirapod.dao.TaxCircleDAO;
import com.nirapod.dao.TaxZoneDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.VatRegistrationDAO;
import com.nirapod.model.Business;
import com.nirapod.model.District;
import com.nirapod.model.Division;
import com.nirapod.model.TaxCircle;
import com.nirapod.model.TaxZone;
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

    @Autowired
    private TaxpayerDAO taxpayerDAO;

    @Autowired
    private BusinessDAO businessDAO;

    @Autowired
    private TaxZoneDAO taxZoneDAO;

    @Autowired
    private TaxCircleDAO taxCircleDAO;

    @Autowired
    private DistrictDAO districtDAO;

    @Autowired
    private DivisionDAO divisionDAO;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public VatRegistration createRegistration(VatRegistration vatReg) {

        if (vatReg.getTaxpayerId() == null) {
            throw new IllegalArgumentException("taxpayerId is required.");
        }

        // Fetch taxpayer first — needed to determine company vs individual
        Taxpayer taxpayer = taxpayerDAO.getById(vatReg.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + vatReg.getTaxpayerId());
        }

        // Resolve vatZone name from vatZoneId
        if (vatReg.getVatZoneId() != null) {
            TaxZone zone = taxZoneDAO.findById(vatReg.getVatZoneId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "VAT Zone not found with ID: " + vatReg.getVatZoneId()));
            vatReg.setVatZone(zone.getName());
        } else {
            throw new IllegalArgumentException("vatZoneId is required.");
        }

        // Resolve vatCircle name from vatCircleId
        if (vatReg.getVatCircleId() != null) {
            TaxCircle circle = taxCircleDAO.findById(vatReg.getVatCircleId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "VAT Circle not found with ID: " + vatReg.getVatCircleId()));
            vatReg.setVatCircle(circle.getName());
        } else {
            throw new IllegalArgumentException("vatCircleId is required.");
        }

        // Resolve district and division names from districtId
        if (vatReg.getDistrictId() != null) {
            District district = districtDAO.findById(vatReg.getDistrictId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "District not found with ID: " + vatReg.getDistrictId()));
            vatReg.setDistrict(district.getName());
            // Division comes from district relationship
            if (district.getDivision() != null) {
                vatReg.setDivision(district.getDivision().getName());
            }
        }

        // Check if company taxpayer
        boolean isCompany = taxpayer.getTaxpayerType() != null &&
            taxpayer.getTaxpayerType().getTypeName().toLowerCase().contains("company");

        if (isCompany) {
            // Company taxpayers register VAT directly — no business record needed
            vatReg.setBusinessName(
                vatReg.getBusinessName() != null && !vatReg.getBusinessName().isBlank()
                    ? vatReg.getBusinessName()
                    : taxpayer.getCompanyName()
            );
            vatReg.setOwnerName(
                vatReg.getOwnerName() != null && !vatReg.getOwnerName().isBlank()
                    ? vatReg.getOwnerName()
                    : taxpayer.getAuthorizedPersonName()
            );
            vatReg.setTinNumber(taxpayer.getTinNumber());
            vatReg.setBinNo(generateBinNo());
            vatReg.setTaxpayer(taxpayer);
            if (vatReg.getRegistrationDate() == null)
                vatReg.setRegistrationDate(LocalDate.now());
            vatReg.setStatus("Pending");
            return vatRegistrationDAO.save(vatReg);
        }

        // Non-company: business is required
        if (vatReg.getBusinessId() == null) {
            throw new IllegalArgumentException("businessId is required for non-company taxpayers.");
        }

        Business business = businessDAO.getById(vatReg.getBusinessId());
        if (business == null || business.isDeleted()) {
            throw new IllegalArgumentException("Business not found: " + vatReg.getBusinessId());
        }

        // Guard: business must belong to this taxpayer
        Taxpayer businessOwner = business.getTaxpayer();
        if (businessOwner == null || !businessOwner.getId().equals(taxpayer.getId())) {
            throw new IllegalStateException(
                "Business \"" + business.getBusinessName() +
                "\" does not belong to taxpayer ID " + vatReg.getTaxpayerId() + ".");
        }

        // Duplicate guard: one BIN per business
        if (vatRegistrationDAO.existsByBusiness_IdAndIsDeletedFalse(business.getId())) {
            throw new IllegalStateException(
                "Business \"" + business.getBusinessName() +
                "\" already has an active VAT registration (BIN).");
        }

        vatReg.setTaxpayer(taxpayer);
        vatReg.setBusiness(business);
        vatReg.setTinNumber(taxpayer.getTinNumber());
        vatReg.setBinNo(generateBinNo());
        if (vatReg.getRegistrationDate() == null) {
            vatReg.setRegistrationDate(LocalDate.now());
        }
        vatReg.setStatus("Pending");

        return vatRegistrationDAO.save(vatReg);
    }

    // ── Read All ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<VatRegistration> getAllRegistrations() {
        return vatRegistrationDAO.findByIsDeletedFalse();
    }

    // ── Read by ID ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public VatRegistration getRegistrationById(Long id) {
        return vatRegistrationDAO.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "VAT registration not found with ID: " + id));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public VatRegistration updateRegistration(Long id, VatRegistration updatedData) {
        VatRegistration existing = getRegistrationById(id);

        // Resolve zone name if ID provided
        if (updatedData.getVatZoneId() != null) {
            TaxZone zone = taxZoneDAO.findById(updatedData.getVatZoneId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "VAT Zone not found: " + updatedData.getVatZoneId()));
            existing.setVatZone(zone.getName());
        } else if (updatedData.getVatZone() != null) {
            existing.setVatZone(updatedData.getVatZone());
        }

        // Resolve circle name if ID provided
        if (updatedData.getVatCircleId() != null) {
            TaxCircle circle = taxCircleDAO.findById(updatedData.getVatCircleId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "VAT Circle not found: " + updatedData.getVatCircleId()));
            existing.setVatCircle(circle.getName());
        } else if (updatedData.getVatCircle() != null) {
            existing.setVatCircle(updatedData.getVatCircle());
        }

        // Resolve district/division names if ID provided
        if (updatedData.getDistrictId() != null) {
            District district = districtDAO.findById(updatedData.getDistrictId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "District not found: " + updatedData.getDistrictId()));
            existing.setDistrict(district.getName());
            if (district.getDivision() != null) {
                existing.setDivision(district.getDivision().getName());
            }
        }

        // Immutable fields: binNo, tinNumber, taxpayer FK, business FK — NOT updated
        existing.setBusinessName(updatedData.getBusinessName());
        existing.setOwnerName(updatedData.getOwnerName());
        existing.setVatCategory(updatedData.getVatCategory());
        existing.setBusinessType(updatedData.getBusinessType());
        existing.setBusinessCategory(updatedData.getBusinessCategory());
        existing.setTradeLicenseNo(updatedData.getTradeLicenseNo());
        existing.setRegistrationDate(updatedData.getRegistrationDate());
        existing.setEffectiveDate(updatedData.getEffectiveDate());
        existing.setExpiryDate(updatedData.getExpiryDate());
        existing.setAnnualTurnover(updatedData.getAnnualTurnover());
        existing.setEmail(updatedData.getEmail());
        existing.setPhone(updatedData.getPhone());
        existing.setAddress(updatedData.getAddress());
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

    private String generateBinNo() {
        String year = String.valueOf(LocalDate.now().getYear());
        String candidate;
        do {
            String unique = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();
            candidate = "BIN-" + year + "-" + unique;
        } while (vatRegistrationDAO.existsByBinNoAndIsDeletedFalse(candidate));
        return candidate;
    }
}