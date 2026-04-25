package com.nirapod.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.DocumentDAO;
import com.nirapod.model.Document;
import jakarta.transaction.Transactional;

@Service
public class DocumentService {

    @Autowired
    private DocumentDAO documentDAO;

    @Transactional
    public void create(Document entity) {
        documentDAO.save(entity);
    }

    public List<Document> getAll() {
        return documentDAO.getAll();
    }

    public Document getById(int id) {
        return documentDAO.getById(id);
    }

    @Transactional
    public void update(Document entity) {
        documentDAO.update(entity);
    }

    @Transactional
    public void delete(int id) {
        documentDAO.delete(id);
    }
}
