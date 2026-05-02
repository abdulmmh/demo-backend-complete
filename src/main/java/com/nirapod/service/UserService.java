package com.nirapod.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.UserDAO;
import com.nirapod.model.User;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Transactional
    public void create(User entity) {
        userDAO.save(entity);
    }

    public List<User> getAll() {
        return userDAO.getAll();
    }

    public User getById(int id) {
        return userDAO.getById(id);
    }

    @Transactional
    public void update(User entity) {
        userDAO.update(entity);
    }

    @Transactional
    public void delete(int id) {
        userDAO.delete(id);
    }
}
