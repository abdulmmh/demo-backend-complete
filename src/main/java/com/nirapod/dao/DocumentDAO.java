package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Document;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class DocumentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Document entity) {
        entityManager.persist(entity);
    }

    public List<Document> getAll() {
        return entityManager.createQuery("from document", Document.class).getResultList();
    }

    public Document getById(int id) {
        return entityManager.find(Document.class, id);
    }

    @Transactional
    public void update(Document entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void delete(int id) {
        Document entity = getById(id);
        if (entity != null) entityManager.remove(entity);
    }
}
