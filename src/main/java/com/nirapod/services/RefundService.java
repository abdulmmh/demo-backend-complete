package com.nirapod.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.RefundDAO;
import com.nirapod.model.Refund;
import jakarta.transaction.Transactional;

@Service
public class RefundService {

    @Autowired
    private RefundDAO refundDAO;

    @Transactional
    public void create(Refund entity) {
        refundDAO.save(entity);
    }

    public List<Refund> getAll() {
        return refundDAO.getAll();
    }

    public Refund getById(int id) {
        return refundDAO.getById(id);
    }

    @Transactional
    public void update(Refund entity) {
        refundDAO.update(entity);
    }

    @Transactional
    public void delete(int id) {
        refundDAO.delete(id);
    }
}
