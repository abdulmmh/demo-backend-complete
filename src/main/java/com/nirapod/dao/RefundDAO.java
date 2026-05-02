package com.nirapod.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Refund;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RefundDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Refund save(Refund entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * JOIN FETCH taxpayer so the response DTO can resolve name/tinNumber
     * without a second query (avoids N+1).
     */
    public List<Refund> getAll() {
        return entityManager.createQuery(
            "SELECT e FROM refund e JOIN FETCH e.taxpayer", Refund.class
        ).getResultList();
    }

    public Optional<Refund> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Refund.class, id));
    }

    public List<Refund> getByTaxpayerId(Long taxpayerId) {
        return entityManager.createQuery(
            "SELECT e FROM refund e JOIN FETCH e.taxpayer t WHERE t.id = :tid",
            Refund.class
        ).setParameter("tid", taxpayerId).getResultList();
    }

    @Transactional
    public Refund update(Refund entity) {
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
