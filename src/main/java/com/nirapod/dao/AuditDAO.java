package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Audit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class AuditDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Audit entity) {
        entityManager.persist(entity);
    }

    public List<Audit> getAll() {
        return entityManager.createQuery("from audit", Audit.class).getResultList();
    }

    public Audit getById(int id) {
        return entityManager.find(Audit.class, id);
    }

    @Transactional
    public void update(Audit entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void delete(int id) {
        Audit entity = getById(id);
        if (entity != null) entityManager.remove(entity);
    }
}
