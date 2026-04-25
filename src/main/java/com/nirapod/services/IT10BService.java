package com.nirapod.services;

import com.nirapod.dao.IT10BDAO;
import com.nirapod.dao.IncomeTaxReturnDAO;
import com.nirapod.model.IT10B;
import com.nirapod.model.IncomeTaxReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IT10BService {

    @Autowired
    private IT10BDAO it10bDAO;

    @Autowired
    private IncomeTaxReturnDAO incomeTaxReturnDAO;  

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public IT10B createStatement(IT10B it10b) {

        if (it10b.getReturnId() == null) {
            throw new IllegalArgumentException("returnId is required to link an IT-10B statement.");
        }

        IncomeTaxReturn itr = incomeTaxReturnDAO.findByIdAndIsDeletedFalse(it10b.getReturnId())
        	    .orElseThrow(() -> new IllegalArgumentException("ITR not found or deleted."));

        if (it10bDAO.existsByIncomeTaxReturn_IdAndIsDeletedFalse(itr.getId())) {
            throw new IllegalStateException(
                    "An IT-10B statement already exists for return ID: " + itr.getId()
                    + " (Return No: " + itr.getReturnNo() + ").");
        }

        it10b.setIncomeTaxReturn(itr);

        it10b.setNetWealth(calculateNetWealth(it10b));

        return it10bDAO.save(it10b);
    }

    // ── Read by returnId ──────────────────────────────────────────────────────

    public IT10B getStatementByReturnId(Long returnId) {
        return it10bDAO.findByIncomeTaxReturn_IdAndIsDeletedFalse(returnId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No IT-10B statement found for Income Tax Return ID: " + returnId));
    }

    // ── Read by primary key ───────────────────────────────────────────────────

    public IT10B getStatementById(Long id) {
        return it10bDAO.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "IT-10B statement not found with ID: " + id));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public IT10B updateStatement(Long id, IT10B updatedData) {
        IT10B existing = getStatementById(id);

        existing.setNonAgriculturalProperty(updatedData.getNonAgriculturalProperty());
        existing.setAgriculturalProperty(updatedData.getAgriculturalProperty());
        existing.setInvestments(updatedData.getInvestments());
        existing.setMotorVehicles(updatedData.getMotorVehicles());
        existing.setBankBalances(updatedData.getBankBalances());
        existing.setPersonalLiabilities(updatedData.getPersonalLiabilities());

        existing.setNetWealth(calculateNetWealth(existing));

        return it10bDAO.save(existing);
    }

    // ── Soft Delete ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteStatement(Long id) {
        IT10B existing = getStatementById(id);
        existing.setDeleted(true);
        it10bDAO.save(existing);
    }

    // ── netWealth Calculation ─────────────────────────────────────────────────
    // Formula: Total Assets − Total Liabilities
    // Total Assets = nonAgriculturalProperty + agriculturalProperty
    //              + investments + motorVehicles + bankBalances

    private Double calculateNetWealth(IT10B it10b) {
        double totalAssets =
                nullSafe(it10b.getNonAgriculturalProperty()) +
                nullSafe(it10b.getAgriculturalProperty())    +
                nullSafe(it10b.getInvestments())             +
                nullSafe(it10b.getMotorVehicles())           +
                nullSafe(it10b.getBankBalances());

        double totalLiabilities = nullSafe(it10b.getPersonalLiabilities());

        return totalAssets - totalLiabilities;
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0.0;
    }
}
