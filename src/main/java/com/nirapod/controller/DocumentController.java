package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.nirapod.dto.request.DocumentRequest;
import com.nirapod.dto.response.DocumentResponse;
import com.nirapod.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
// CrossOrigin removed — handled globally in SecurityConfig
public class DocumentController {

    @Autowired private DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAll() {
        return ResponseEntity.ok(documentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<DocumentResponse>> getByTaxpayer(@PathVariable Long taxpayerId) {
        return ResponseEntity.ok(documentService.getByTaxpayerId(taxpayerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest req) {
        return ResponseEntity.ok(documentService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
