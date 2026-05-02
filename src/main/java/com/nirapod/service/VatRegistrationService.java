package com.nirapod.service;

import com.nirapod.dao.BusinessDAO;
import com.nirapod.dao.DistrictDAO;
import com.nirapod.dao.DivisionDAO;
import com.nirapod.dao.TaxCircleDAO;
import com.nirapod.dao.TaxZoneDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.VatRegistrationDAO;
import com.nirapod.dto.request.VatRegistrationCreateRequest;
import com.nirapod.model.Address;
import com.nirapod.model.Business;
import com.nirapod.model.TaxCircle;
import com.nirapod.model.TaxZone;
import com.nirapod.model.Taxpayer;
import com.nirapod.model.VatRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VatRegistrationService {

    @Autowired private VatRegistrationDAO vatRegistrationDAO;
    @Autowired private TaxpayerDAO        taxpayerDAO;
    @Autowired private BusinessDAO        businessDAO;
    @Autowired private TaxZoneDAO         taxZoneDAO;
    @Autowired private TaxCircleDAO       taxCircleDAO;
    @Autowired private DistrictDAO        districtDAO;
    @Autowired private DivisionDAO        divisionDAO;

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Creates a new VAT registration from a validated DTO.
     *
     * Business rules enforced:
     *  1. taxpayerId is mandatory.
     *  2. Taxpayer must not be Blacklisted or Suspended (guard clause).
     *  3. vatZoneId and vatCircleId are always required.
     *  4. Company taxpayers register directly — no businessId needed.
     *     phone/email/address are sourced from the Taxpayer entity.
     *  5. Non-company taxpayers MUST supply a valid businessId.
     *  6. effectiveDate, if provided, must be >= registrationDate.
     */
    @Transactional
    public VatRegistration createRegistration(VatRegistrationCreateRequest req) {

        // ── Validation: taxpayerId ────────────────────────────────────────
        if (req.getTaxpayerId() == null) {
            throw new IllegalArgumentException("taxpayerId is required.");
        }

        Taxpayer taxpayer = taxpayerDAO.getById(req.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException(
                "Taxpayer not found with ID: " + req.getTaxpayerId());
        }

        // ── Guard Clause: Blacklisted / Suspended ─────────────────────────
        String taxpayerStatus = taxpayer.getStatus();
        if ("Blacklisted".equalsIgnoreCase(taxpayerStatus) ||
            "Suspended".equalsIgnoreCase(taxpayerStatus)) {
            throw new IllegalStateException(
                "Taxpayer " + taxpayer.getTinNumber() + " is " + taxpayerStatus +
                " and cannot be registered for VAT.");
        }

        // ── Validation: vatZoneId ─────────────────────────────────────────
        if (req.getVatZoneId() == null) {
            throw new IllegalArgumentException("vatZoneId is required.");
        }
        TaxZone zone = taxZoneDAO.findById(req.getVatZoneId())
            .orElseThrow(() -> new IllegalArgumentException(
                "VAT Zone not found with ID: " + req.getVatZoneId()));

        // ── Validation: vatCircleId ───────────────────────────────────────
        if (req.getVatCircleId() == null) {
            throw new IllegalArgumentException("vatCircleId is required.");
        }
        TaxCircle circle = taxCircleDAO.findById(req.getVatCircleId())
            .orElseThrow(() -> new IllegalArgumentException(
                "VAT Circle not found with ID: " + req.getVatCircleId()));

        // ── Validation: effectiveDate >= registrationDate ─────────────────
        LocalDate regDate = req.getRegistrationDate() != null
            ? req.getRegistrationDate()
            : LocalDate.now();

        if (req.getEffectiveDate() != null &&
            req.getEffectiveDate().isBefore(regDate)) {
            throw new IllegalArgumentException(
                "effectiveDate (" + req.getEffectiveDate() +
                ") cannot be earlier than registrationDate (" + regDate + ").");
        }

        // ── Build base record (shared by both paths) ──────────────────────
        VatRegistration vatReg = new VatRegistration();
        vatReg.setTaxpayer(taxpayer);
        vatReg.setTinNumber(taxpayer.getTinNumber());
        vatReg.setVatCategory(req.getVatCategory());
        vatReg.setVatZone(zone.getName());
        vatReg.setVatCircle(circle.getName());
        vatReg.setZoneId(zone.getId());
        vatReg.setRegistrationDate(regDate);
        vatReg.setEffectiveDate(req.getEffectiveDate());
        vatReg.setExpiryDate(req.getExpiryDate());
        vatReg.setRemarks(req.getRemarks());
        vatReg.setStatus("Pending");
        vatReg.setAnnualTurnover(0.0);

        // Resolve district/division names from any supplied districtId
        resolveDistrictDivision(req.getDistrictId(), vatReg);

        // ── Detect taxpayer type ──────────────────────────────────────────
        boolean isCompany = taxpayer.getTaxpayerType() != null &&
            taxpayer.getTaxpayerType().getTypeName().toLowerCase().contains("company");

        // ═════════════════════════════════════════════════════════════════
        // COMPANY PATH
        // No business record — every contact field is sourced from Taxpayer.
        // safe() converts null → "" so NOT NULL columns (phone) never receive
        // a literal null and throw SQLIntegrityConstraintViolationException.
        // ═════════════════════════════════════════════════════════════════
        if (isCompany) {
            vatReg.setBusinessName(
                notBlank(taxpayer.getCompanyName())
                    ? taxpayer.getCompanyName()
                    : "Unknown Company");

            vatReg.setOwnerName(safe(taxpayer.getAuthorizedPersonName()));

            // FIX: phone is NOT NULL in vat_registrations — populate from taxpayer
            vatReg.setPhone(safe(taxpayer.getPhone()));
            vatReg.setEmail(safe(taxpayer.getEmail()));

            // Composite address from taxpayer's present-address columns:
            // present_road_village, present_thana, present_district, present_division
            vatReg.setAddress(buildPresentAddress(taxpayer));

            // Geographic names — fall back to taxpayer's embedded present address
            // (Address is @Embedded: getPresentAddress().getDistrict() etc.)
            Address pa = taxpayer.getPresentAddress();
            if (!notBlank(vatReg.getDistrict()) && pa != null) {
                vatReg.setDistrict(safe(pa.getDistrict()));
            }
            if (!notBlank(vatReg.getDivision()) && pa != null) {
                vatReg.setDivision(safe(pa.getDivision()));
            }

            vatReg.setBinNo(generateBinNo(zone.getId()));
            return vatRegistrationDAO.save(vatReg);
        }

        // ═════════════════════════════════════════════════════════════════
        // NON-COMPANY PATH — businessId is mandatory
        // ═════════════════════════════════════════════════════════════════
        if (req.getBusinessId() == null) {
            throw new IllegalArgumentException(
                "businessId is required for Individual and Firm taxpayers.");
        }

        Business business = businessDAO.getById(req.getBusinessId());
        if (business == null || business.isDeleted()) {
            throw new IllegalArgumentException(
                "Business not found with ID: " + req.getBusinessId());
        }

        // Guard: business must belong to the supplied taxpayer
        Taxpayer businessOwner = business.getTaxpayer();
        if (businessOwner == null || !businessOwner.getId().equals(taxpayer.getId())) {
            throw new IllegalStateException(
                "Business \"" + business.getBusinessName() +
                "\" does not belong to taxpayer ID " + req.getTaxpayerId() + ".");
        }

        // Guard: one active VAT registration per business
        if (vatRegistrationDAO.existsByBusiness_IdAndIsDeletedFalse(business.getId())) {
            throw new IllegalStateException(
                "Business \"" + business.getBusinessName() +
                "\" already has an active VAT registration (BIN).");
        }

        // Populate all fields from the business record.
        // safe() also guards any business field that may be null in legacy data.
        vatReg.setBusiness(business);
        vatReg.setBusinessName(safe(business.getBusinessName()));
        vatReg.setOwnerName(safe(business.getOwnerName()));
        vatReg.setTradeLicenseNo(safe(business.getTradeLicenseNo()));
        vatReg.setEmail(safe(business.getEmail()));
        vatReg.setPhone(safe(business.getPhone()));       // NOT NULL in schema
        vatReg.setAddress(safe(business.getAddress()));
        vatReg.setAnnualTurnover(
            business.getAnnualTurnover() != null ? business.getAnnualTurnover() : 0.0);

        if (business.getBusinessType() != null) {
            vatReg.setBusinessType(business.getBusinessType().getTypeName());
        }
        if (business.getBusinessCategory() != null) {
            vatReg.setBusinessCategory(business.getBusinessCategory().getCategoryName());
        }

        // If no districtId in DTO, fall back to the business's district
        if (req.getDistrictId() == null && business.getDistrict() != null) {
            resolveDistrictDivision(business.getDistrict().getId(), vatReg);
        }

        vatReg.setBinNo(generateBinNo(zone.getId()));
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

        if (updatedData.getVatZoneId() != null) {
            TaxZone zone = taxZoneDAO.findById(updatedData.getVatZoneId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "VAT Zone not found: " + updatedData.getVatZoneId()));
            existing.setVatZone(zone.getName());
            existing.setZoneId(zone.getId());
        } else if (notBlank(updatedData.getVatZone())) {
            existing.setVatZone(updatedData.getVatZone());
        }

        if (updatedData.getVatCircleId() != null) {
            TaxCircle circle = taxCircleDAO.findById(updatedData.getVatCircleId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "VAT Circle not found: " + updatedData.getVatCircleId()));
            existing.setVatCircle(circle.getName());
        } else if (notBlank(updatedData.getVatCircle())) {
            existing.setVatCircle(updatedData.getVatCircle());
        }

        resolveDistrictDivision(updatedData.getDistrictId(), existing);

        LocalDate regDate = updatedData.getRegistrationDate() != null
            ? updatedData.getRegistrationDate()
            : existing.getRegistrationDate();

        if (updatedData.getEffectiveDate() != null &&
            updatedData.getEffectiveDate().isBefore(regDate)) {
            throw new IllegalArgumentException(
                "effectiveDate cannot be earlier than registrationDate.");
        }

        // Immutable: binNo, tinNumber, taxpayer, business
        existing.setBusinessName(safe(updatedData.getBusinessName()));
        existing.setOwnerName(safe(updatedData.getOwnerName()));
        existing.setVatCategory(updatedData.getVatCategory());
        existing.setBusinessType(updatedData.getBusinessType());
        existing.setBusinessCategory(updatedData.getBusinessCategory());
        existing.setTradeLicenseNo(updatedData.getTradeLicenseNo());
        existing.setRegistrationDate(updatedData.getRegistrationDate());
        existing.setEffectiveDate(updatedData.getEffectiveDate());
        existing.setExpiryDate(updatedData.getExpiryDate());
        existing.setAnnualTurnover(updatedData.getAnnualTurnover());
        existing.setEmail(safe(updatedData.getEmail()));
        existing.setPhone(safe(updatedData.getPhone()));  // safe() here too
        existing.setAddress(safe(updatedData.getAddress()));
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

    /**
     * Generates a structured BIN: {@code BIN-[YEAR]-[ZONE_ID]-[SEQUENCE]}.
     * SEQUENCE is zone-scoped, zero-padded to 4 digits.
     * Example: BIN-2026-3-0007
     */
    private String generateBinNo(Long zoneId) {
        String year = String.valueOf(LocalDate.now().getYear());
        long next = vatRegistrationDAO.countByZoneIdAndIsDeletedFalse(zoneId) + 1;
        String candidate;
        do {
            candidate = String.format("BIN-%s-%d-%04d", year, zoneId, next);
            next++;
        } while (vatRegistrationDAO.existsByBinNoAndIsDeletedFalse(candidate));
        return candidate;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Resolves a districtId to its name + parent division name and writes both
     * onto the VatRegistration. No-op when districtId is null.
     */
    private void resolveDistrictDivision(Long districtId, VatRegistration vatReg) {
        if (districtId == null) return;
        districtDAO.findById(districtId).ifPresent(district -> {
            vatReg.setDistrict(district.getName());
            if (district.getDivision() != null) {
                vatReg.setDivision(district.getDivision().getName());
            }
        });
    }

    /**
     * Builds a readable single-line address from a taxpayer's present-address
     * parts (present_road_village, present_thana, present_district, present_division).
     * Returns "" — never null — when all parts are absent.
     */
    private String buildPresentAddress(Taxpayer tp) {
        // Address is @Embedded — access via getPresentAddress(), never flat getters.
        Address pa = tp.getPresentAddress();
        if (pa == null) return "";
        StringBuilder sb = new StringBuilder();
        appendPart(sb, pa.getRoadVillage());
        appendPart(sb, pa.getThana());
        appendPart(sb, pa.getDistrict());
        appendPart(sb, pa.getDivision());
        return sb.toString().trim();
    }

    private void appendPart(StringBuilder sb, String part) {
        if (part != null && !part.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(part.trim());
        }
    }

    /**
     * Null-safe guard — converts null to "" so that columns marked NOT NULL
     * in the schema never receive a literal null from Java.
     */
    private String safe(String s) {
        return s != null ? s : "";
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
