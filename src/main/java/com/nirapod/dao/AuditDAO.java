package com.nirapod.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Audit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AuditDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Audit save(Audit entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * JOIN FETCH taxpayer so the response DTO can resolve name/tinNumber
     * without a second query (avoids N+1).
     */
    public List<Audit> getAll() {
        return entityManager.createQuery(
            "SELECT e FROM audit e JOIN FETCH e.taxpayer", Audit.class
        ).getResultList();
    }

    public Optional<Audit> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Audit.class, id));
    }

    public List<Audit> getByTaxpayerId(Long taxpayerId) {
        return entityManager.createQuery(
            "SELECT e FROM audit e JOIN FETCH e.taxpayer t WHERE t.id = :tid",
            Audit.class
        ).setParameter("tid", taxpayerId).getResultList();
    }

    @Transactional
    public Audit update(Audit entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    public void softDelete(Long id) {
        getById(id).ifPresent(e -> {
            e.setStatus("Inactive");
            entityManager.merge(e);
        });
    }
}
