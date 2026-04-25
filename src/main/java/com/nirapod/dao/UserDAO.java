package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.nirapod.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(User entity) {
        entityManager.persist(entity);
    }

    public List<User> getAll() {
        return entityManager.createQuery("from user", User.class).getResultList();
    }

    public User getById(int id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public void update(User entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void delete(int id) {
        User entity = getById(id);
        if (entity != null) entityManager.remove(entity);
    }
}
