package com.nirapod.controller;

import com.nirapod.model.VatRegistration;
import com.nirapod.services.VatRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vat-registrations")
@CrossOrigin(origins = "http://localhost:4200")
public class VatRegistrationController {

    // Only Service is injected — never DAO directly
    @Autowired
    private VatRegistrationService vatRegistrationService;

    // POST /api/vat-registrations
    // Angular sends { taxpayerId, businessName, vatCategory, vatZone, vatCircle, phone, ... }
    // binNo and tinNumber are auto-set in Service — Angular does NOT send them
    @PostMapping
    public ResponseEntity<VatRegistration> createRegistration(
            @RequestBody VatRegistration vatReg) {
        VatRegistration created = vatRegistrationService.createRegistration(vatReg);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET /api/vat-registrations
    @GetMapping
    public ResponseEntity<List<VatRegistration>> getAllRegistrations() {
        return ResponseEntity.ok(vatRegistrationService.getAllRegistrations());
    }

    // GET /api/vat-registrations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VatRegistration> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vatRegistrationService.getRegistrationById(id));
    }

    // PUT /api/vat-registrations/{id}
    // binNo and taxpayer FK are preserved in Service — not overwritten by update
    @PutMapping("/{id}")
    public ResponseEntity<VatRegistration> updateRegistration(
            @PathVariable Long id,
            @RequestBody VatRegistration updatedData) {
        return ResponseEntity.ok(vatRegistrationService.updateRegistration(id, updatedData));
    }

    // DELETE /api/vat-registrations/{id} — soft delete only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        vatRegistrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }
}
