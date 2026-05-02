package com.nirapod.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Penalty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PenaltyDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Penalty save(Penalty entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * JOIN FETCH taxpayer so the response DTO can resolve name/tinNumber
     * without a second query (avoids N+1).
     */
    public List<Penalty> getAll() {
        return entityManager.createQuery(
            "SELECT e FROM penalty e JOIN FETCH e.taxpayer", Penalty.class
        ).getResultList();
    }

    public Optional<Penalty> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Penalty.class, id));
    }

    public List<Penalty> getByTaxpayerId(Long taxpayerId) {
        return entityManager.createQuery(
            "SELECT e FROM penalty e JOIN FETCH e.taxpayer t WHERE t.id = :tid",
            Penalty.class
        ).setParameter("tid", taxpayerId).getResultList();
    }

    @Transactional
    public Penalty update(Penalty entity) {
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
