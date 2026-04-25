package com.nirapod.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.Payment;
import com.nirapod.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody Payment payment) {
        Payment saved = paymentService.create(payment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    // Only status and remarks are patchable — amount, TIN, bank details are immutable
    @PatchMapping("/{id}/status")
    public ResponseEntity<Payment> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String newStatus = payload.get("status");
        String remarks   = payload.get("remarks");
        String processedBy = payload.get("processedBy");
        return ResponseEntity.ok(paymentService.updateStatus(id, newStatus, remarks, processedBy));
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}