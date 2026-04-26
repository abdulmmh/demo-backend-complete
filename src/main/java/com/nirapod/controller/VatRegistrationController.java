package com.nirapod.controller;

import com.nirapod.model.VatRegistration;
import com.nirapod.services.VatRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vat-registrations")
@CrossOrigin(origins = "http://localhost:4200")
public class VatRegistrationController {

    @Autowired
    private VatRegistrationService vatRegistrationService;

    // POST /api/vat-registrations
    @PostMapping
    public ResponseEntity<?> createRegistration(@RequestBody VatRegistration vatReg) {
        // FIX: was returning raw 500 on IllegalArgumentException / IllegalStateException.
        // Now catches and returns proper 400 Bad Request with a readable message.
        try {
            VatRegistration created = vatRegistrationService.createRegistration(vatReg);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // GET /api/vat-registrations
    @GetMapping
    public ResponseEntity<List<VatRegistration>> getAllRegistrations() {
        return ResponseEntity.ok(vatRegistrationService.getAllRegistrations());
    }

    // GET /api/vat-registrations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vatRegistrationService.getRegistrationById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/vat-registrations/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegistration(
            @PathVariable Long id,
            @RequestBody VatRegistration updatedData) {
        try {
            return ResponseEntity.ok(vatRegistrationService.updateRegistration(id, updatedData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE /api/vat-registrations/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Long id) {
        try {
            vatRegistrationService.deleteRegistration(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}