package com.nirapod.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nirapod.dao.PaymentDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.model.Payment;
import com.nirapod.model.Taxpayer;

@Service
public class PaymentService {

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private TaxpayerDAO taxpayerDAO;

    // ── Create ───────────────────────────────────────────────────────────────

    @Transactional
    public Payment create(Payment payment) {

        if (payment.getTaxpayerId() != null) {
            Taxpayer taxpayer = taxpayerDAO.getById(payment.getTaxpayerId());
            if (taxpayer == null) {
                throw new IllegalArgumentException(
                    "Taxpayer not found with ID: " + payment.getTaxpayerId());
            }
            payment.setTaxpayer(taxpayer);

            payment.setTinNumber(taxpayer.getTinNumber());
            String name = taxpayer.getFullName();
            if (name == null || name.isBlank()) name = taxpayer.getCompanyName();
            if (name == null || name.isBlank()) name = "Unknown";
            payment.setTaxpayerName(name);
        } else {
            if (payment.getTinNumber() == null || payment.getTinNumber().isBlank()) {
                throw new IllegalArgumentException("Either taxpayerId or tinNumber is required.");
            }
        }
        
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        return paymentDAO.save(payment);
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public List<Payment> getAll() {
        return paymentDAO.getAll();
    }

    public Payment getById(Long id) {
        Payment payment = paymentDAO.getById(id);
        if (payment == null || payment.isDeleted()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        return payment;
    }

    // ── Update — only status and remarks are editable ────────────────────────
    // Financial records are immutable after creation.
    // Amount, TIN, payment method, bank details cannot be changed.

    @Transactional
    public Payment updateStatus(Long id, String newStatus, String remarks, String processedBy) {
        Payment existing = getById(id);

        // Completed payments cannot be re-processed
        if ("Completed".equals(existing.getStatus())) {
            throw new IllegalStateException(
                "Cannot update a Completed payment. Create a new payment record if needed.");
        }

        List<String> validStatuses = List.of("Pending", "Completed", "Failed", "Cancelled");
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid payment status: " + newStatus);
        }

        existing.setStatus(newStatus);
        if (remarks != null && !remarks.isBlank()) {
            existing.setRemarks(remarks.trim());
        }
        
        if (processedBy != null && !processedBy.isBlank()) {
            existing.setProcessedBy(processedBy.trim());
        }
        
        return paymentDAO.update(existing);
    }

    // ── Soft delete — financial records must never be hard-deleted ───────────

    @Transactional
    public void delete(Long id) {
        Payment existing = getById(id);

        // Completed & Cancelled payments cannot be deleted
        if ("Completed".equals(existing.getStatus()) || "Cancelled".equals(existing.getStatus())) {
            throw new IllegalStateException(
                "Cannot update a " + existing.getStatus() + " payment.");
        }

        existing.setDeleted(true);
        paymentDAO.update(existing);
    }
}