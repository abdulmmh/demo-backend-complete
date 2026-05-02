package com.nirapod.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.NoticeDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dto.request.NoticeRequest;
import com.nirapod.dto.response.NoticeResponse;
import com.nirapod.model.Notice;
import com.nirapod.model.Taxpayer;

@Service
@Transactional
public class NoticeService {

    @Autowired private NoticeDAO  noticeDAO;
    @Autowired private TaxpayerDAO taxpayerDAO;

    public NoticeResponse create(NoticeRequest req) {
        Taxpayer taxpayer = taxpayerDAO.getById(req.getTaxpayerId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found: " + req.getTaxpayerId());
        }
        Notice entity = new Notice();
        entity.setTaxpayer(taxpayer);   // FK — name never copied
        applyFields(req, entity);
        return NoticeResponse.from(noticeDAO.save(entity));
    }

    public List<NoticeResponse> getAll() {
        // DAO uses JOIN FETCH — taxpayerName resolved from FK, zero extra queries
        return noticeDAO.getAll()
            .stream()
            .map(NoticeResponse::from)
            .collect(Collectors.toList());
    }

    public NoticeResponse getById(Long id) {
        return noticeDAO.getById(id)
            .map(NoticeResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));
    }

    public List<NoticeResponse> getByTaxpayerId(Long taxpayerId) {
        return noticeDAO.getByTaxpayerId(taxpayerId)
            .stream()
            .map(NoticeResponse::from)
            .collect(Collectors.toList());
    }

    public NoticeResponse update(Long id, NoticeRequest req) {
        Notice existing = noticeDAO.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));
        // taxpayer FK intentionally NOT changed on update
        applyFields(req, existing);
        return NoticeResponse.from(noticeDAO.update(existing));
    }

    public void delete(Long id) {
        noticeDAO.softDelete(id);
    }

    private void applyFields(NoticeRequest req, Notice entity) {
        entity.setSubject(req.getSubject());
        entity.setBody(req.getBody());
        entity.setNoticeType(req.getNoticeType());
        entity.setPriority(req.getPriority());
        entity.setTargetType(req.getTargetType());
        entity.setIssuedDate(req.getIssuedDate());
        entity.setDueDate(req.getDueDate());
        entity.setIssuedBy(req.getIssuedBy());
        entity.setAttachmentName(req.getAttachmentName());
    }
}
