package com.nirapod.controller;

import com.nirapod.model.IT10B;
import com.nirapod.service.IT10BService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/it10b")
@CrossOrigin(origins = "http://localhost:4200")
public class IT10BController {

    @Autowired
    private IT10BService it10bService;

    @PostMapping
    public ResponseEntity<IT10B> createStatement(@RequestBody IT10B it10b) {
        IT10B created = it10bService.createStatement(it10b);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @GetMapping("/by-return/{returnId}")
    public ResponseEntity<IT10B> getByReturnId(@PathVariable Long returnId) {
        IT10B statement = it10bService.getStatementByReturnId(returnId);
        return ResponseEntity.ok(statement);
    }


    @GetMapping("/{id}")
    public ResponseEntity<IT10B> getById(@PathVariable Long id) {
        IT10B statement = it10bService.getStatementById(id);
        return ResponseEntity.ok(statement);
    }


    @PutMapping("/{id}")
    public ResponseEntity<IT10B> updateStatement(@PathVariable Long id,
                                                 @RequestBody IT10B updatedData) {
        IT10B updated = it10bService.updateStatement(id, updatedData);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatement(@PathVariable Long id) {
        it10bService.deleteStatement(id);
        return ResponseEntity.noContent().build();
    }
}
