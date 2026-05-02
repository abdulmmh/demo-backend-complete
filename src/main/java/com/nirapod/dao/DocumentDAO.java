package com.nirapod.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Document;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DocumentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Document save(Document entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * JOIN FETCH taxpayer so the response DTO can resolve name/tinNumber
     * without a second query (avoids N+1).
     */
    public List<Document> getAll() {
        return entityManager.createQuery(
            "SELECT e FROM document e JOIN FETCH e.taxpayer", Document.class
        ).getResultList();
    }

    public Optional<Document> getById(Long id) {
        return Optional.ofNullable(entityManager.find(Document.class, id));
    }

    public List<Document> getByTaxpayerId(Long taxpayerId) {
        return entityManager.createQuery(
            "SELECT e FROM document e JOIN FETCH e.taxpayer t WHERE t.id = :tid",
            Document.class
        ).setParameter("tid", taxpayerId).getResultList();
    }

    @Transactional
    public Document update(Document entity) {
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
