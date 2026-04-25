package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Refund;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class RefundDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Refund entity) {
        entityManager.persist(entity);
    }

    public List<Refund> getAll() {
        return entityManager.createQuery("from refund", Refund.class).getResultList();
    }

    public Refund getById(int id) {
        return entityManager.find(Refund.class, id);
    }

    @Transactional
    public void update(Refund entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void delete(int id) {
        Refund entity = getById(id);
        if (entity != null) entityManager.remove(entity);
    }
}
