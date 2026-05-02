package com.nirapod.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.PenaltyDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dto.request.PenaltyRequest;
import com.nirapod.dto.response.PenaltyResponse;
import com.nirapod.model.Penalty;
import com.nirapod.model.Taxpayer;

@Service
@Transactional
public class PenaltyService {

    @Autowired private PenaltyDAO  penaltyDAO;
    @Autowired private TaxpayerDAO taxpayerDAO;

    public PenaltyResponse create(PenaltyRequest req) {
        Taxpayer taxpayer = taxpayerDAO.getById(req.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + req.getTaxpayerId());
        }
        Penalty entity = new Penalty();
        entity.setTaxpayer(taxpayer);   // FK — name never copied
        applyFields(req, entity);
        return PenaltyResponse.from(penaltyDAO.save(entity));
    }

    public List<PenaltyResponse> getAll() {
        // DAO uses JOIN FETCH — taxpayerName resolved from FK, zero extra queries
        return penaltyDAO.getAll()
            .stream()
            .map(PenaltyResponse::from)
            .collect(Collectors.toList());
    }

    public PenaltyResponse getById(Long id) {
        return penaltyDAO.getById(id)
            .map(PenaltyResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Penalty not found: " + id));
    }

    public List<PenaltyResponse> getByTaxpayerId(Long taxpayerId) {
        return penaltyDAO.getByTaxpayerId(taxpayerId)
            .stream()
            .map(PenaltyResponse::from)
            .collect(Collectors.toList());
    }

    public PenaltyResponse update(Long id, PenaltyRequest req) {
        Penalty existing = penaltyDAO.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Penalty not found: " + id));
        // taxpayer FK intentionally NOT changed on update
        applyFields(req, existing);
        return PenaltyResponse.from(penaltyDAO.update(existing));
    }

    public void delete(Long id) {
        penaltyDAO.softDelete(id);
    }

    private void applyFields(PenaltyRequest req, Penalty entity) {
        entity.setPenaltyType(req.getPenaltyType());
        entity.setSeverity(req.getSeverity());
        entity.setPenaltyAmount(req.getPenaltyAmount());
        entity.setInterestAmount(req.getInterestAmount());
        entity.setReturnNo(req.getReturnNo());
        entity.setAssessmentYear(req.getAssessmentYear());
        entity.setIssueDate(req.getIssueDate());
        entity.setDueDate(req.getDueDate());
        entity.setIssuedBy(req.getIssuedBy());
        entity.setDescription(req.getDescription());
        entity.setRemarks(req.getRemarks());
    }
}
