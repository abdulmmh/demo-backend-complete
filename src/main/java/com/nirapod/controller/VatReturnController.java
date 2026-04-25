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
    // Angular sends { vatRegistrationId, returnPeriod, periodMonth, periodYear, ... }
    // binNo, tinNumber, businessName, returnNo, totalSupplies, netTaxPayable
    // are all auto-set in Service — Angular does NOT send these
    @PostMapping
    public ResponseEntity<VatReturn> createReturn(@RequestBody VatReturn vatReturn) {
        VatReturn created = vatReturnService.createReturn(vatReturn);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET /api/vat-returns
    @GetMapping
    public ResponseEntity<List<VatReturn>> getAllReturns() {
        return ResponseEntity.ok(vatReturnService.getAllReturns());
    }

    // GET /api/vat-returns/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VatReturn> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vatReturnService.getReturnById(id));
    }

    // PUT /api/vat-returns/{id}
    // returnNo, binNo, businessName and vatRegistration FK preserved in Service
    @PutMapping("/{id}")
    public ResponseEntity<VatReturn> updateReturn(@PathVariable Long id,
                                                  @RequestBody VatReturn updatedData) {
        return ResponseEntity.ok(vatReturnService.updateReturn(id, updatedData));
    }

    // PATCH /api/vat-returns/{id}/status
    // Angular view component calls this for workflow actions (Submit, Accept, Reject, etc.)
    @PatchMapping("/{id}/status")
    public ResponseEntity<VatReturn> updateStatus(@PathVariable Long id,
                                                  @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(vatReturnService.updateStatus(id, payload));
    }

    // DELETE /api/vat-returns/{id} — soft delete only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturn(@PathVariable Long id) {
        vatReturnService.deleteReturn(id);
        return ResponseEntity.noContent().build();
    }
}
