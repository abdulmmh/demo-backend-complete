package com.nirapod.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.RefundDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dto.request.RefundRequest;
import com.nirapod.dto.response.RefundResponse;
import com.nirapod.model.Refund;
import com.nirapod.model.Taxpayer;

@Service
@Transactional
public class RefundService {

    @Autowired private RefundDAO  refundDAO;
    @Autowired private TaxpayerDAO taxpayerDAO;

    public RefundResponse create(RefundRequest req) {
        Taxpayer taxpayer = taxpayerDAO.getById(req.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + req.getTaxpayerId());
        }
        Refund entity = new Refund();
        entity.setTaxpayer(taxpayer);   // FK — name never copied
        applyFields(req, entity);
        return RefundResponse.from(refundDAO.save(entity));
    }

    public List<RefundResponse> getAll() {
        // DAO uses JOIN FETCH — taxpayerName resolved from FK, zero extra queries
        return refundDAO.getAll()
            .stream()
            .map(RefundResponse::from)
            .collect(Collectors.toList());
    }

    public RefundResponse getById(Long id) {
        return refundDAO.getById(id)
            .map(RefundResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found: " + id));
    }

    public List<RefundResponse> getByTaxpayerId(Long taxpayerId) {
        return refundDAO.getByTaxpayerId(taxpayerId)
            .stream()
            .map(RefundResponse::from)
            .collect(Collectors.toList());
    }

    public RefundResponse update(Long id, RefundRequest req) {
        Refund existing = refundDAO.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found: " + id));
        // taxpayer FK intentionally NOT changed on update
        applyFields(req, existing);
        return RefundResponse.from(refundDAO.update(existing));
    }

    public void delete(Long id) {
        refundDAO.softDelete(id);
    }

    private void applyFields(RefundRequest req, Refund entity) {
        entity.setRefundType(req.getRefundType());
        entity.setRefundMethod(req.getRefundMethod());
        entity.setClaimAmount(req.getClaimAmount());
        entity.setReturnNo(req.getReturnNo());
        entity.setPaymentRef(req.getPaymentRef());
        entity.setBankName(req.getBankName());
        entity.setBankBranch(req.getBankBranch());
        entity.setAccountNo(req.getAccountNo());
        entity.setClaimDate(req.getClaimDate());
        entity.setRemarks(req.getRemarks());
    }
}
