package com.nirapod.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class NoticeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Notice save(Notice entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * JOIN FETCH taxpayer so the response DTO can resolve name/tinNumber
     * without a second query (avoids N+1).
     */
    public List<Notice> getAll() {
        return entityManager.createQuery(
            "SELECT e FROM notice e JOIN FETCH e.taxpayer", Notice.class
        ).getResultList();
    }

    public Optional<Notice> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Notice.class, id));
    }

    public List<Notice> getByTaxpayerId(Long taxpayerId) {
        return entityManager.createQuery(
            "SELECT e FROM notice e JOIN FETCH e.taxpayer t WHERE t.id = :tid",
            Notice.class
        ).setParameter("tid", taxpayerId).getResultList();
    }

    @Transactional
    public Notice update(Notice entity) {
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
