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
import java.util.Set;
import java.util.UUID;

@Service
public class VatReturnService {

    @Autowired
    private VatReturnDAO vatReturnDAO;

    @Autowired
    private VatRegistrationDAO vatRegistrationDAO;

    // Allowed workflow statuses — any other value rejected at the API layer
    private static final Set<String> ALLOWED_STATUSES = Set.of(
        "Draft", "Submitted", "Under Review", "Accepted", "Rejected", "Send Back", "Overdue"
    );

    // Statuses that cannot be edited — terminal states
    private static final Set<String> NON_EDITABLE_STATUSES = Set.of("Accepted", "Rejected");

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public VatReturn createReturn(VatReturn vatReturn) {

        if (vatReturn.getVatRegistrationId() == null) {
            throw new IllegalArgumentException("vatRegistrationId is required.");
        }

        VatRegistration reg = vatRegistrationDAO
                .findByIdAndIsDeletedFalse(vatReturn.getVatRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "VAT Registration not found with ID: " + vatReturn.getVatRegistrationId()));

        if (!"Active".equals(reg.getStatus())) {
            throw new IllegalStateException(
                    "Cannot file a return for a " + reg.getStatus() + " VAT registration.");
        }

        vatReturn.setVatRegistration(reg);
        vatReturn.setBinNo(reg.getBinNo());
        vatReturn.setTinNumber(reg.getTinNumber());
        vatReturn.setBusinessName(reg.getBusinessName());

        if (vatReturnDAO.existsByBinNoAndPeriodMonthAndPeriodYearAndIsDeletedFalse(
                reg.getBinNo(), vatReturn.getPeriodMonth(), vatReturn.getPeriodYear())) {
            throw new IllegalStateException(
                    "A VAT return for " + reg.getBusinessName() +
                    " — " + vatReturn.getPeriodMonth() + " " + vatReturn.getPeriodYear() +
                    " already exists.");
        }

        recalculate(vatReturn);
        vatReturn.setReturnNo(generateReturnNo(vatReturn.getPeriodYear()));

        if (vatReturn.getSubmissionDate() == null) {
            vatReturn.setSubmissionDate(LocalDate.now());
        }
        vatReturn.setStatus("Draft");

        return vatReturnDAO.save(vatReturn);
    }

    // ── Read All ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<VatReturn> getAllReturns() {
        return vatReturnDAO.findByIsDeletedFalse();
    }

    // ── Read by ID ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public VatReturn getReturnById(Long id) {
        return vatReturnDAO.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "VAT Return not found with ID: " + id));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public VatReturn updateReturn(Long id, VatReturn updatedData) {
        VatReturn existing = getReturnById(id);

        // FIX #1: status guard on update.
        // Old code allowed editing Accepted/Rejected returns — which are terminal states.
        // Angular edit component already redirects away from these, but the backend
        // must enforce it independently.
        if (NON_EDITABLE_STATUSES.contains(existing.getStatus())) {
            throw new IllegalStateException(
                "Cannot edit a VAT return with status: " + existing.getStatus() +
                ". Accepted and Rejected returns are immutable.");
        }

        // Immutable fields: returnNo, binNo, tinNumber, businessName, vatRegistration FK
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

        // FIX #2: validate newStatus against the allowed set.
        // Old code accepted any arbitrary string — e.g. "Hacked", "foo" would persist.
        if (!ALLOWED_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException(
                "Invalid status: \"" + newStatus + "\". Allowed values: " + ALLOWED_STATUSES);
        }

        // FIX #3: prevent status changes on already-terminal returns.
        // Once Accepted or Rejected, no further workflow actions are allowed.
        if (NON_EDITABLE_STATUSES.contains(existing.getStatus())) {
            throw new IllegalStateException(
                "Status cannot be changed for a return that is already " + existing.getStatus() + ".");
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
                nullSafe(r.getExemptSupplies())  +
                nullSafe(r.getZeroRatedSupplies());

        double netTaxPayable = Math.max(0,
                nullSafe(r.getOutputTax()) - nullSafe(r.getInputTax()));

        r.setTotalSupplies(totalSupplies);
        r.setNetTaxPayable(netTaxPayable);
    }

    private double nullSafe(Double v) { return v != null ? v : 0.0; }

    // ── returnNo Generation ────────────────────────────────────────────────────

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
        // FIX: uses existsByReturnNoAndIsDeletedFalse — soft-deleted returnNos are reusable
        } while (vatReturnDAO.existsByReturnNoAndIsDeletedFalse(candidate));

        return candidate;
    }
}