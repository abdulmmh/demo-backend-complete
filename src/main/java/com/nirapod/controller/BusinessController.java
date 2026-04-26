package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nirapod.dto.BusinessVatStatusDTO;
import com.nirapod.model.Business;
import com.nirapod.services.BusinessService;

@RestController
@RequestMapping("/api/businesses")
@CrossOrigin(origins = "http://localhost:4200")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @PostMapping
    public ResponseEntity<Business> create(@RequestBody Business business) {
        Business saved = businessService.create(business);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Business>> getAll() {
        return ResponseEntity.ok(businessService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Business> getById(@PathVariable Long id) {
        Business b = businessService.getById(id);
        return b != null ? ResponseEntity.ok(b) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Business> update(@PathVariable Long id, @RequestBody Business business) {
        business.setId(id);
        Business updated = businessService.update(business);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        businessService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/by-taxpayer/{taxpayerId}/vat-status")
    public ResponseEntity<List<BusinessVatStatusDTO>> getByTaxpayerWithVatStatus(
            @PathVariable Long taxpayerId) {
        return ResponseEntity.ok(businessService.getByTaxpayerWithVatStatus(taxpayerId));
    }
}