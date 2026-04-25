package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.Audit;
import com.nirapod.services.AuditService;

@RestController
@RequestMapping("/api/audits")
@CrossOrigin(origins = "http://localhost:4200")
public class AuditController {

    @Autowired
    private AuditService auditService;

      @PostMapping
    public ResponseEntity<Audit> create(@RequestBody Audit audit) {
        auditService.create(audit);
        return ResponseEntity.ok(audit);
    }

    @GetMapping
    public ResponseEntity<List<Audit>> getAll() {
        return ResponseEntity.ok(auditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Audit> getById(@PathVariable int id) {
        Audit result = auditService.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Audit> update(@PathVariable int id, @RequestBody Audit audit) {
        auditService.update(audit);
        return ResponseEntity.ok(audit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        auditService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
