package com.nirapod.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nirapod.model.TaxStructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository(value = "taxStructureDAO")
public class TaxStructureDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(TaxStructure taxStructure) {
		entityManager.persist(taxStructure);
	}
   
	public List<TaxStructure> getAll(){
		String sql = "from taxStructure";
		List<TaxStructure> taxStructures = entityManager.createQuery(sql).getResultList();
		return taxStructures;
	}
	
	public TaxStructure getById(int id) {
        return entityManager.find(TaxStructure.class, id);
	}
	
	public void update (TaxStructure taxStructure) {
		entityManager.merge(taxStructure);
	}
	
	public void delete (int id) {
		String sql = "delete from taxStructure where id = :id";
		entityManager.createQuery(sql).setParameter("id", id).executeUpdate();
	}
}