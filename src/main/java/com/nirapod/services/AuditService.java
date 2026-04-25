package com.nirapod.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.AuditDAO;
import com.nirapod.model.Audit;
import jakarta.transaction.Transactional;

@Service
public class AuditService {

    @Autowired
    private AuditDAO auditDAO;

    @Transactional
    public void create(Audit entity) {
        auditDAO.save(entity);
    }

    public List<Audit> getAll() {
        return auditDAO.getAll();
    }

    public Audit getById(int id) {
        return auditDAO.getById(id);
    }

    @Transactional
    public void update(Audit entity) {
        auditDAO.update(entity);
    }

    @Transactional
    public void delete(int id) {
        auditDAO.delete(id);
    }
}
