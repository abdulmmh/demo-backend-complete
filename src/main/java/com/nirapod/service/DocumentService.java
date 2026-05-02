package com.nirapod.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.DocumentDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dto.request.DocumentRequest;
import com.nirapod.dto.response.DocumentResponse;
import com.nirapod.model.Document;
import com.nirapod.model.Taxpayer;

@Service
@Transactional
public class DocumentService {

    @Autowired private DocumentDAO  documentDAO;
    @Autowired private TaxpayerDAO taxpayerDAO;

    public DocumentResponse create(DocumentRequest req) {
        Taxpayer taxpayer = taxpayerDAO.getById(req.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + req.getTaxpayerId());
        }
        Document entity = new Document();
        entity.setTaxpayer(taxpayer);   // FK — name never copied
        applyFields(req, entity);
        return DocumentResponse.from(documentDAO.save(entity));
    }

    public List<DocumentResponse> getAll() {
        // DAO uses JOIN FETCH — taxpayerName resolved from FK, zero extra queries
        return documentDAO.getAll()
            .stream()
            .map(DocumentResponse::from)
            .collect(Collectors.toList());
    }

    public DocumentResponse getById(Long id) {
        return documentDAO.getById(id)
            .map(DocumentResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    }

    public List<DocumentResponse> getByTaxpayerId(Long taxpayerId) {
        return documentDAO.getByTaxpayerId(taxpayerId)
            .stream()
            .map(DocumentResponse::from)
            .collect(Collectors.toList());
    }

    public DocumentResponse update(Long id, DocumentRequest req) {
        Document existing = documentDAO.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
        // taxpayer FK intentionally NOT changed on update
        applyFields(req, existing);
        return DocumentResponse.from(documentDAO.update(existing));
    }

    public void delete(Long id) {
        documentDAO.softDelete(id);
    }

    private void applyFields(DocumentRequest req, Document entity) {
        entity.setDocumentType(req.getDocumentType());
        entity.setDocumentCategory(req.getDocumentCategory());
        entity.setDocumentTitle(req.getDocumentTitle());
        entity.setReferenceNo(req.getReferenceNo());
        entity.setIssueDate(req.getIssueDate());
        entity.setExpiryDate(req.getExpiryDate());
        entity.setSubmissionDate(req.getSubmissionDate());
        entity.setFileSize(req.getFileSize());
        entity.setRemarks(req.getRemarks());
    }
}
