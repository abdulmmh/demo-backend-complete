package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.Refund;
import com.nirapod.services.RefundService;

@RestController
@RequestMapping("/api/refunds")
@CrossOrigin(origins = "http://localhost:4200")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @PostMapping
    public ResponseEntity<Refund> create(@RequestBody Refund refund) {
        refundService.create(refund);
        return ResponseEntity.ok(refund);
    }

    @GetMapping
    public ResponseEntity<List<Refund>> getAll() {
        return ResponseEntity.ok(refundService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Refund> getById(@PathVariable int id) {
        Refund result = refundService.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Refund> update(@PathVariable int id, @RequestBody Refund refund) {
        refundService.update(refund);
        return ResponseEntity.ok(refund);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        refundService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
