package com.nirapod.controller;

import com.nirapod.model.VatReturn;
import com.nirapod.services.VatReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vat-returns")
@CrossOrigin(origins = "http://localhost:4200")
public class VatReturnController {

    @Autowired
    private VatReturnService vatReturnService;

    // POST /api/vat-returns
    @PostMapping
    public ResponseEntity<?> createReturn(@RequestBody VatReturn vatReturn) {
        // FIX: was returning raw 500 on business rule violations.
        // Now returns 400 with a readable message for both Angular toast and debugging.
        try {
            VatReturn created = vatReturnService.createReturn(vatReturn);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // GET /api/vat-returns
    @GetMapping
    public ResponseEntity<List<VatReturn>> getAllReturns() {
        return ResponseEntity.ok(vatReturnService.getAllReturns());
    }

    // GET /api/vat-returns/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vatReturnService.getReturnById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/vat-returns/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReturn(@PathVariable Long id,
                                          @RequestBody VatReturn updatedData) {
        try {
            return ResponseEntity.ok(vatReturnService.updateReturn(id, updatedData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // PATCH /api/vat-returns/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> payload) {
        try {
            return ResponseEntity.ok(vatReturnService.updateStatus(id, payload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE /api/vat-returns/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReturn(@PathVariable Long id) {
        try {
            vatReturnService.deleteReturn(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}