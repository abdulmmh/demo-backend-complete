package com.nirapod.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.TaxStructureDAO;
import com.nirapod.model.TaxStructure;

@Service(value = "taxStructureService")
@Transactional
public class TaxStructureService {

    @Autowired
    TaxStructureDAO taxStructureDAO;
    
    public void create(TaxStructure taxStructure) {
    	taxStructureDAO.save(taxStructure);
    }
    
    public List<TaxStructure> getAll() {
    	return taxStructureDAO.getAll();
    }
    
    public TaxStructure getById(int id) {
        return taxStructureDAO.getById(id);
    }
    
    public void update(TaxStructure taxStructure) {
    	taxStructureDAO.update(taxStructure);
    }
    
    public void delete(int id) {
    	taxStructureDAO.delete(id);
    }
}