package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.nirapod.dto.request.RefundRequest;
import com.nirapod.dto.response.RefundResponse;
import com.nirapod.service.RefundService;

@RestController
@RequestMapping("/api/refunds")
// CrossOrigin removed — handled globally in SecurityConfig
public class RefundController {

    @Autowired private RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundResponse> create(@Valid @RequestBody RefundRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refundService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<RefundResponse>> getAll() {
        return ResponseEntity.ok(refundService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.getById(id));
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<RefundResponse>> getByTaxpayer(@PathVariable Long taxpayerId) {
        return ResponseEntity.ok(refundService.getByTaxpayerId(taxpayerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefundResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RefundRequest req) {
        return ResponseEntity.ok(refundService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        refundService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
