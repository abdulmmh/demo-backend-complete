package com.nirapod.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nirapod.model.Taxpayer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository(value = "taxpayerDAO")
public class TaxpayerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Taxpayer taxpayer) {
        entityManager.persist(taxpayer);
    }

    public List<Taxpayer> getAll() {
        return entityManager.createQuery("from taxpayer", Taxpayer.class)
                .getResultList();
    }

    public List<Taxpayer> getByStatus(String status) {
        return entityManager
                .createQuery("from taxpayer where status = :status", Taxpayer.class)
                .setParameter("status", status)
                .getResultList();
    }

    // server-side search across name, NID, company name, TIN, email, phone
    public List<Taxpayer> search(String query) {
        String like = "%" + query.toLowerCase() + "%";
        return entityManager.createQuery(
                "from taxpayer t where " +
                "lower(t.fullName)     like :q or " +
                "lower(t.companyName)  like :q or " +
                "lower(t.nid)          like :q or " +
                "lower(t.tinNumber)    like :q or " +
                "lower(t.email)        like :q or " +
                "lower(t.phone)        like :q or " +
                "lower(t.tradeLicenseNo) like :q or " +
                "lower(t.rjscNo)       like :q",
                Taxpayer.class)
                .setParameter("q", like)
                .getResultList();
    }

    public Taxpayer getById(Long id) {
        return entityManager.find(Taxpayer.class, id);
    }

    public void update(Taxpayer taxpayer) {
        entityManager.merge(taxpayer);
    }


    public void delete(Long id) {
        entityManager.createQuery("delete from taxpayer where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}