package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.Penalty;
import com.nirapod.services.PenaltyService;

@RestController
@RequestMapping("/api/penalties")
@CrossOrigin(origins = "http://localhost:4200")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @PostMapping
    public ResponseEntity<Penalty> create(@RequestBody Penalty penalty) {
        penaltyService.create(penalty);
        return ResponseEntity.ok(penalty);
    }

    @GetMapping
    public ResponseEntity<List<Penalty>> getAll() {
        return ResponseEntity.ok(penaltyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Penalty> getById(@PathVariable int id) {
        Penalty result = penaltyService.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Penalty> update(@PathVariable int id, @RequestBody Penalty penalty) {
        penaltyService.update(penalty);
        return ResponseEntity.ok(penalty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        penaltyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
