package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.TaxStructure;
import com.nirapod.services.TaxStructureService;

@RestController
@RequestMapping("/api/tax-structures")
@CrossOrigin(origins = "http://localhost:4200")
public class TaxStructureController {

    @Autowired
    private TaxStructureService taxStructureService;

    @PostMapping
    public ResponseEntity<TaxStructure> create(@RequestBody TaxStructure taxStructure) {
        taxStructureService.create(taxStructure);
        return ResponseEntity.ok(taxStructure);
    }

    @GetMapping
    public ResponseEntity<List<TaxStructure>> getAll() {
        return ResponseEntity.ok(taxStructureService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxStructure> getById(@PathVariable int id) {
        TaxStructure result = taxStructureService.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxStructure> update(@PathVariable int id, @RequestBody TaxStructure taxStructure) {
        taxStructureService.update(taxStructure);
        return ResponseEntity.ok(taxStructure);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        taxStructureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}