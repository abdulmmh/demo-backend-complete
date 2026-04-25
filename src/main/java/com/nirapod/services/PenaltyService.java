package com.nirapod.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.PenaltyDAO;
import com.nirapod.model.Penalty;
import jakarta.transaction.Transactional;

@Service
public class PenaltyService {

    @Autowired
    private PenaltyDAO penaltyDAO;

    @Transactional
    public void create(Penalty entity) {
        penaltyDAO.save(entity);
    }

    public List<Penalty> getAll() {
        return penaltyDAO.getAll();
    }

    public Penalty getById(int id) {
        return penaltyDAO.getById(id);
    }

    @Transactional
    public void update(Penalty entity) {
        penaltyDAO.update(entity);
    }

    @Transactional
    public void delete(int id) {
        penaltyDAO.delete(id);
    }
}
