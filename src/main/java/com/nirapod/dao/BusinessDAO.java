package com.nirapod.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.model.Business;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
public class BusinessDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Business save(Business entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<Business> getAll() {
        return entityManager
            .createQuery("from business where isDeleted = false", Business.class)
            .getResultList();
    }

    public Business getById(Long id) {
        return entityManager.find(Business.class, id);
    }

    @Transactional
    public Business update(Business entity) {

        return entityManager.merge(entity);
    }
    
    public List<Business> getByTaxpayerId(Long taxpayerId) {
        return entityManager
            .createQuery(
                "from business b where b.taxpayer.id = :tid and b.isDeleted = false",
                Business.class)
            .setParameter("tid", taxpayerId)
            .getResultList();
    }
}