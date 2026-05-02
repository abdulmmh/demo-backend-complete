package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.nirapod.dto.request.AuditRequest;
import com.nirapod.dto.response.AuditResponse;
import com.nirapod.service.AuditService;

@RestController
@RequestMapping("/api/audits")
// CrossOrigin removed — handled globally in SecurityConfig
public class AuditController {

    @Autowired private AuditService auditService;

    @PostMapping
    public ResponseEntity<AuditResponse> create(@Valid @RequestBody AuditRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<AuditResponse>> getAll() {
        return ResponseEntity.ok(auditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getById(id));
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<AuditResponse>> getByTaxpayer(@PathVariable Long taxpayerId) {
        return ResponseEntity.ok(auditService.getByTaxpayerId(taxpayerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest req) {
        return ResponseEntity.ok(auditService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auditService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
