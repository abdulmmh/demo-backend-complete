package com.nirapod.services;

import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.VatRegistrationDAO;
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

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public VatRegistration createRegistration(VatRegistration vatReg) {

        // 1. Validate taxpayerId is present
        if (vatReg.getTaxpayerId() == null) {
            throw new IllegalArgumentException("taxpayerId is required to link a VAT registration.");
        }

        // 2. Resolve @Transient taxpayerId → Taxpayer entity
        //    TaxpayerDAO uses custom EntityManager — getById() returns object directly, NOT Optional
        Taxpayer taxpayer = taxpayerDAO.getById(vatReg.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException(
                    "Taxpayer not found with ID: " + vatReg.getTaxpayerId());
        }
        vatReg.setTaxpayer(taxpayer);

        // 3. Copy tinNumber from resolved taxpayer for direct column storage
        vatReg.setTinNumber(taxpayer.getTinNumber());

        // 4. Duplicate guard — one active VAT registration per TIN
        if (vatRegistrationDAO.existsByTinNumberAndIsDeletedFalse(taxpayer.getTinNumber())) {
            throw new IllegalStateException(
                    "An active VAT registration already exists for TIN: " + taxpayer.getTinNumber());
        }

        // 5. Auto-generate BIN (Business Identification Number) — race-condition safe
        vatReg.setBinNo(generateBinNo());

        // 6. Set defaults
        if (vatReg.getRegistrationDate() == null) {
            vatReg.setRegistrationDate(LocalDate.now());
        }
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
