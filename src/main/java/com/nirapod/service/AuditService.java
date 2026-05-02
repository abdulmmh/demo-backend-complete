package com.nirapod.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.AuditDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dto.request.AuditRequest;
import com.nirapod.dto.response.AuditResponse;
import com.nirapod.model.Audit;
import com.nirapod.model.Taxpayer;

@Service
@Transactional
public class AuditService {

    @Autowired private AuditDAO  auditDAO;
    @Autowired private TaxpayerDAO taxpayerDAO;

    public AuditResponse create(AuditRequest req) {
        Taxpayer taxpayer = taxpayerDAO.getById(req.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + req.getTaxpayerId());
        }
        Audit entity = new Audit();
        entity.setTaxpayer(taxpayer);   // FK — name never copied
        applyFields(req, entity);
        return AuditResponse.from(auditDAO.save(entity));
    }

    public List<AuditResponse> getAll() {
        // DAO uses JOIN FETCH — taxpayerName resolved from FK, zero extra queries
        return auditDAO.getAll()
            .stream()
            .map(AuditResponse::from)
            .collect(Collectors.toList());
    }

    public AuditResponse getById(Long id) {
        return auditDAO.getById(id)
            .map(AuditResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Audit not found: " + id));
    }

    public List<AuditResponse> getByTaxpayerId(Long taxpayerId) {
        return auditDAO.getByTaxpayerId(taxpayerId)
            .stream()
            .map(AuditResponse::from)
            .collect(Collectors.toList());
    }

    public AuditResponse update(Long id, AuditRequest req) {
        Audit existing = auditDAO.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Audit not found: " + id));
        // taxpayer FK intentionally NOT changed on update
        applyFields(req, existing);
        return AuditResponse.from(auditDAO.update(existing));
    }

    public void delete(Long id) {
        auditDAO.softDelete(id);
    }

    private void applyFields(AuditRequest req, Audit entity) {
        entity.setAuditType(req.getAuditType());
        entity.setPriority(req.getPriority());
        entity.setAssessmentYear(req.getAssessmentYear());
        entity.setReturnNo(req.getReturnNo());
        entity.setScheduledDate(req.getScheduledDate());
        entity.setAssignedTo(req.getAssignedTo());
        entity.setSupervisedBy(req.getSupervisedBy());
        entity.setRemarks(req.getRemarks());
    }
}
