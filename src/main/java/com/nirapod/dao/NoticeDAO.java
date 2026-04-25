package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class NoticeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Notice entity) {
        entityManager.persist(entity);
    }

    public List<Notice> getAll() {
        return entityManager.createQuery("from notice", Notice.class).getResultList();
    }

    public Notice getById(int id) {
        return entityManager.find(Notice.class, id);
    }

    @Transactional
    public void update(Notice entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void delete(int id) {
        Notice entity = getById(id);
        if (entity != null) entityManager.remove(entity);
    }
}
