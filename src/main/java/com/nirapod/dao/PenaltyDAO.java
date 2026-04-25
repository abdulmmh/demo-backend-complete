package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Penalty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class PenaltyDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Penalty entity) {
        entityManager.persist(entity);
    }

    public List<Penalty> getAll() {
        return entityManager.createQuery("from penalty", Penalty.class).getResultList();
    }

    public Penalty getById(int id) {
        return entityManager.find(Penalty.class, id);
    }

    @Transactional
    public void update(Penalty entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void delete(int id) {
        Penalty entity = getById(id);
        if (entity != null) entityManager.remove(entity);
    }
}
