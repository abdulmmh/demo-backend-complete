package com.nirapod.services;

import com.nirapod.dao.VatRegistrationDAO;
import com.nirapod.dao.VatReturnDAO;
import com.nirapod.model.VatRegistration;
import com.nirapod.model.VatReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VatReturnService {

    @Autowired
    private VatReturnDAO vatReturnDAO;

    // VatRegistrationDAO extends JpaDAO — use findById().orElseThrow()
    @Autowired
    private VatRegistrationDAO vatRegistrationDAO;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public VatReturn createReturn(VatReturn vatReturn) {

        // 1. Validate vatRegistrationId is present
        if (vatReturn.getVatRegistrationId() == null) {
            throw new IllegalArgumentException("vatRegistrationId is required.");
        }

        // 2. Resolve @Transient vatRegistrationId → VatRegistration entity
        VatRegistration reg = vatRegistrationDAO
                .findByIdAndIsDeletedFalse(vatReturn.getVatRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "VAT Registration not found with ID: " + vatReturn.getVatRegistrationId()));

        // 3. Only Active registrations can file returns
        if (!"Active".equals(reg.getStatus())) {
            throw new IllegalStateException(
                    "Cannot file a return for a " + reg.getStatus() + " VAT registration.");
        }

        vatReturn.setVatRegistration(reg);

        // 4. Copy denormalized fields from registration for fast query
        vatReturn.setBinNo(reg.getBinNo());
        vatReturn.setTinNumber(reg.getTinNumber());
        vatReturn.setBusinessName(reg.getBusinessName());

        // 5. Duplicate guard — one return per BIN + period + year
        if (vatReturnDAO.existsByBinNoAndPeriodMonthAndPeriodYearAndIsDeletedFalse(
                reg.getBinNo(), vatReturn.getPeriodMonth(), vatReturn.getPeriodYear())) {
            throw new IllegalStateException(
                    "A VAT return for " + reg.getBusinessName() +
                    " — " + vatReturn.getPeriodMonth() + " " + vatReturn.getPeriodYear() +
                    " already exists.");
        }

        // 6. Auto-calculate supplies and tax
        recalculate(vatReturn);

        // 7. Auto-generate returnNo — race-condition safe
        vatReturn.setReturnNo(generateReturnNo(vatReturn.getPeriodYear()));

        // 8. Set defaults
        if (vatReturn.getSubmissionDate() == null) {
            vatReturn.setSubmissionDate(LocalDate.now());
        }
        vatReturn.setStatus("Draft");

        return vatReturnDAO.save(vatReturn);
    }

    // ── Read All ──────────────────────────────────────────────────────────────

    public List<VatReturn> getAllReturns() {
        return vatReturnDAO.findByIsDeletedFalse();
    }

    // ── Read by ID ────────────────────────────────────────────────────────────

    public VatReturn getReturnById(Long id) {
        return vatReturnDAO.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "VAT Return not found with ID: " + id));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public VatReturn updateReturn(Long id, VatReturn updatedData) {
        VatReturn existing = getReturnById(id);

        // returnNo, binNo, tinNumber, businessName, vatRegistration FK — NOT updatable
        existing.setReturnPeriod(updatedData.getReturnPeriod());
        existing.setPeriodMonth(updatedData.getPeriodMonth());
        existing.setPeriodYear(updatedData.getPeriodYear());
        existing.setAssessmentYear(updatedData.getAssessmentYear());
        existing.setSubmissionDate(updatedData.getSubmissionDate());
        existing.setDueDate(updatedData.getDueDate());
        existing.setTaxableSupplies(updatedData.getTaxableSupplies());
        existing.setExemptSupplies(updatedData.getExemptSupplies());
        existing.setZeroRatedSupplies(updatedData.getZeroRatedSupplies());
        existing.setOutputTax(updatedData.getOutputTax());
        existing.setInputTax(updatedData.getInputTax());
        existing.setTaxPaid(updatedData.getTaxPaid());
        existing.setSubmittedBy(updatedData.getSubmittedBy());
        existing.setRemarks(updatedData.getRemarks());

        // Re-calculate after update
        recalculate(existing);

        return vatReturnDAO.save(existing);
    }

    // ── Status Update (Workflow) ───────────────────────────────────────────────

    @Transactional
    public VatReturn updateStatus(Long id, Map<String, String> payload) {
        VatReturn existing = getReturnById(id);

        String newStatus = payload.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("status field is required.");
        }

        existing.setStatus(newStatus);
        if (payload.containsKey("remarks") && payload.get("remarks") != null) {
            existing.setRemarks(payload.get("remarks"));
        }

        return vatReturnDAO.save(existing);
    }

    // ── Soft Delete ────────────────────────────────────────────────────────────

    @Transactional
    public void deleteReturn(Long id) {
        VatReturn existing = getReturnById(id);
        existing.setDeleted(true);
        vatReturnDAO.save(existing);
    }

    // ── Auto-Calculation ───────────────────────────────────────────────────────

    private void recalculate(VatReturn r) {
        double totalSupplies =
                nullSafe(r.getTaxableSupplies()) +
                nullSafe(r.getExemptSupplies()) +
                nullSafe(r.getZeroRatedSupplies());

        double netTaxPayable = Math.max(0,
                nullSafe(r.getOutputTax()) - nullSafe(r.getInputTax()));

        r.setTotalSupplies(totalSupplies);
        r.setNetTaxPayable(netTaxPayable);
    }

    private double nullSafe(Double v) { return v != null ? v : 0.0; }

    // ── returnNo Generation ────────────────────────────────────────────────────
    // Format: VRT-{YEAR}-{5-char UUID substring}
    // do-while loop guarantees uniqueness even on collision

    private String generateReturnNo(String periodYear) {
        String year = (periodYear != null && periodYear.length() >= 4)
                ? periodYear.substring(0, 4)
                : String.valueOf(LocalDate.now().getYear());

        String candidate;
        do {
            candidate = "VRT-" + year + "-" +
                    UUID.randomUUID().toString()
                            .replace("-", "")
                            .substring(0, 5)
                            .toUpperCase();
        } while (vatReturnDAO.existsByReturnNo(candidate));

        return candidate;
    }
}