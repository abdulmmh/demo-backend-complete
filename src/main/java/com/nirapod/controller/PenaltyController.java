package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.nirapod.dto.request.PenaltyRequest;
import com.nirapod.dto.response.PenaltyResponse;
import com.nirapod.service.PenaltyService;

@RestController
@RequestMapping("/api/penalties")
// CrossOrigin removed — handled globally in SecurityConfig
public class PenaltyController {

    @Autowired private PenaltyService penaltyService;

    @PostMapping
    public ResponseEntity<PenaltyResponse> create(@Valid @RequestBody PenaltyRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(penaltyService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<PenaltyResponse>> getAll() {
        return ResponseEntity.ok(penaltyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(penaltyService.getById(id));
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<PenaltyResponse>> getByTaxpayer(@PathVariable Long taxpayerId) {
        return ResponseEntity.ok(penaltyService.getByTaxpayerId(taxpayerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PenaltyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PenaltyRequest req) {
        return ResponseEntity.ok(penaltyService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        penaltyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
