package com.nirapod.service;

import com.nirapod.dao.IncomeTaxReturnDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.model.ITRAction;
import com.nirapod.model.IncomeTaxReturn;
import com.nirapod.model.Taxpayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class IncomeTaxReturnService {

    private static final List<String> VALID_STATUSES = List.of(
        "Draft", "Submitted", "Under Review",
        "Accepted", "Rejected", "Overdue", "Amended", "Send Back"
    );

    @Autowired private IncomeTaxReturnDAO    incomeTaxReturnDAO;
    @Autowired private TaxpayerDAO           taxpayerDAO;
    @Autowired private TaxCalculationService taxCalculationService; // injected

    // ── Create ──────────────────────────────────────────────────────────────

    @Transactional
    public IncomeTaxReturn createReturn(IncomeTaxReturn itr) {

        if (incomeTaxReturnDAO.existsByTinNumberAndAssessmentYearAndIsDeletedFalse(
                itr.getTinNumber(), itr.getAssessmentYear())) {
            throw new IllegalStateException(
                "A return for TIN " + itr.getTinNumber() +
                " and assessment year " + itr.getAssessmentYear() + " already exists.");
        }

        // Link taxpayer FK
        if (itr.getTaxpayerId() == null) {
            throw new IllegalArgumentException("taxpayerId is required.");
        }
        Taxpayer taxpayer = taxpayerDAO.getById(itr.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + itr.getTaxpayerId());
        }
        itr.setTaxpayer(taxpayer);

        // Set itrCategory from taxpayer type if not explicitly sent
        if (itr.getItrCategory() == null || itr.getItrCategory().isBlank()) {
            String typeName = taxpayer.getTaxpayerType() != null
                ? taxpayer.getTaxpayerType().getTypeName() : "Individual";
            itr.setItrCategory(typeName);
        }

        // Backend tax calculation — officer never sets taxRate or grossTax manually
        taxCalculationService.calculate(itr);

        // Generate returnNo
        String shortId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        itr.setReturnNo("ITR-" + itr.getAssessmentYear() + "-" + shortId);

        itr.setSubmissionDate(LocalDate.now());
        itr.setStatus("Submitted");

        return incomeTaxReturnDAO.save(itr);
    }

    // ── Read ────────────────────────────────────────────────────────────────

    public List<IncomeTaxReturn> getAllReturns() {
        return incomeTaxReturnDAO.findByIsDeletedFalse();
    }

    public IncomeTaxReturn getReturnById(Long id) {
        return incomeTaxReturnDAO.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("ITR not found: " + id));
    }

    // ── Update (income figures only — taxRate/grossTax recalculated) ────────

    @Transactional
    public IncomeTaxReturn updateReturn(Long id, IncomeTaxReturn updatedData) {
        IncomeTaxReturn existing = getReturnById(id);
        
     // Block editing accepted or rejected returns
        if ("Accepted".equals(existing.getStatus()) || "Rejected".equals(existing.getStatus())) {
            throw new IllegalStateException(
                "Cannot edit a return with status: " + existing.getStatus()
            );
        }

        // Update raw input fields
        existing.setGrossIncome(updatedData.getGrossIncome());
        existing.setExemptIncome(updatedData.getExemptIncome());
        existing.setTaxRebate(updatedData.getTaxRebate());
        existing.setAdvanceTaxPaid(updatedData.getAdvanceTaxPaid());
        existing.setWithholdingTax(updatedData.getWithholdingTax());
        existing.setTaxPaid(updatedData.getTaxPaid());
        existing.setRemarks(updatedData.getRemarks());

        // Recalculate — taxRate and grossTax always come from the engine, never from the form
        taxCalculationService.calculate(existing);

        return incomeTaxReturnDAO.save(existing);
    }

    // ── Status update (workflow) ─────────────────────────────────────────────

    @Transactional
    public IncomeTaxReturn updateStatus(
            Long id, String newStatus, String remarks,
            String action, String performedBy, String role) {

        if (newStatus == null || !VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        IncomeTaxReturn existing = getReturnById(id);
        String current = existing.getStatus();

        if (current.equals(newStatus)) {
            throw new IllegalStateException("Return is already in status: " + newStatus);
        }

        if (!isValidTransition(current, newStatus)) {
            throw new IllegalStateException(
                "Invalid transition: " + current + " → " + newStatus);
        }

        existing.setStatus(newStatus);

        if ("Accepted".equals(newStatus))    existing.setApprovalDate(LocalDate.now());
        if ("Under Review".equals(newStatus)) existing.setReviewStartDate(LocalDate.now());

        if (remarks != null && !remarks.isBlank()) {
            existing.setRemarks(remarks.trim());
        }

        // Audit entry
        ITRAction entry = new ITRAction();
        entry.setAction(action != null ? action : "STATUS_UPDATE");
        entry.setStatus(newStatus);
        entry.setRemarks(remarks != null ? remarks.trim() : null);
        entry.setPerformedBy(performedBy != null ? performedBy : "SYSTEM");
        entry.setRole(role != null ? role : "UNKNOWN");
        entry.setPerformedAt(LocalDateTime.now());
        entry.setIncomeTaxReturn(existing);
        existing.getActionHistory().add(entry);

        return incomeTaxReturnDAO.save(existing);
    }

    // ── Soft delete ──────────────────────────────────────────────────────────

    @Transactional
    public void softDeleteReturn(Long id) {
        IncomeTaxReturn existing = getReturnById(id);
        existing.setDeleted(true);
        incomeTaxReturnDAO.save(existing);
    }

    // ── CSV export ───────────────────────────────────────────────────────────

    public byte[] exportReturnsToCsv() {
        StringBuilder csv = new StringBuilder(
            "\uFEFFReturn No,TIN,Taxpayer,Category,Assessment Year,Gross Income,Net Tax Payable,Status\n");

        for (IncomeTaxReturn itr : getAllReturns()) {
            double grossTax  = itr.getGrossTax()   != null ? itr.getGrossTax()   : 0;
            double rebate    = itr.getTaxRebate()   != null ? itr.getTaxRebate()  : 0;
            double netPayable = Math.max(0, grossTax - rebate);

            csv.append(safe(itr.getReturnNo()))     .append(",")
               .append(safe(itr.getTinNumber()))    .append(",")
               .append('"').append(safe(itr.getTaxpayerName())).append('"').append(",")
               .append(safe(itr.getItrCategory())) .append(",")
               .append(safe(itr.getAssessmentYear())).append(",")
               .append(itr.getGrossIncome() != null ? itr.getGrossIncome() : 0).append(",")
               .append(netPayable)                 .append(",")
               .append(safe(itr.getStatus()))      .append("\n");
        }
        return csv.toString().getBytes();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String safe(String v) { return v != null ? v : "N/A"; }

    private boolean isValidTransition(String current, String next) {
        return switch (current) {
            case "Draft"        -> next.equals("Submitted");
            case "Submitted"    -> next.equals("Under Review") || next.equals("Rejected");
            case "Under Review" -> next.equals("Accepted") || next.equals("Rejected") || next.equals("Send Back");
            case "Send Back"    -> next.equals("Submitted");
            case "Rejected"     -> next.equals("Amended");
            case "Amended"      -> next.equals("Under Review");
            case "Overdue"      -> next.equals("Submitted");
            case "Accepted"     -> false; // terminal state
            default             -> false;
        };
    }
}
