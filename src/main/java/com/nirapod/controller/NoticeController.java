package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.Notice;
import com.nirapod.services.NoticeService;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "http://localhost:4200")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping
    public ResponseEntity<Notice> create(@RequestBody Notice notice) {
        noticeService.create(notice);
        return ResponseEntity.ok(notice);
    }

    @GetMapping
    public ResponseEntity<List<Notice>> getAll() {
        return ResponseEntity.ok(noticeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getById(@PathVariable int id) {
        Notice result = noticeService.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notice> update(@PathVariable int id, @RequestBody Notice notice) {
        noticeService.update(notice);
        return ResponseEntity.ok(notice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
