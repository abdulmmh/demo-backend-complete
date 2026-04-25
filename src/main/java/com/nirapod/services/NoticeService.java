package com.nirapod.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.NoticeDAO;
import com.nirapod.model.Notice;
import jakarta.transaction.Transactional;

@Service
public class NoticeService {

    @Autowired
    private NoticeDAO noticeDAO;

    @Transactional
    public void create(Notice entity) {
        noticeDAO.save(entity);
    }

    public List<Notice> getAll() {
        return noticeDAO.getAll();
    }

    public Notice getById(int id) {
        return noticeDAO.getById(id);
    }

    @Transactional
    public void update(Notice entity) {
        noticeDAO.update(entity);
    }

    @Transactional
    public void delete(int id) {
        noticeDAO.delete(id);
    }
}
