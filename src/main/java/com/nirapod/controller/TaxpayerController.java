package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.Taxpayer;
import com.nirapod.services.TaxpayerService;

@RestController
@RequestMapping("/api/taxpayers")
@CrossOrigin(origins = "http://localhost:4200")
public class TaxpayerController {

    @Autowired
    private TaxpayerService taxpayerService;

    @PostMapping
    public ResponseEntity<Taxpayer> create(@RequestBody Taxpayer taxpayer) {
        taxpayerService.create(taxpayer);
        return ResponseEntity.ok(taxpayer);
    }


    
    @GetMapping
    public ResponseEntity<List<Taxpayer>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {

        List<Taxpayer> result;

        if (search != null && !search.isBlank()) {
            result = taxpayerService.search(search.trim());
        } else if (status != null && !status.isBlank()) {
            result = taxpayerService.getByStatus(status);
        } else {
            result = taxpayerService.getAll();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taxpayer> getById(@PathVariable Long id) {
        Taxpayer result = taxpayerService.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Taxpayer> update(@PathVariable Long id, @RequestBody Taxpayer taxpayer) {
        taxpayer.setId(id);
        taxpayerService.update(taxpayer);
        return ResponseEntity.ok(taxpayer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taxpayerService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
 // Export Taxpayers
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTaxpayers() {
        byte[] csvData = taxpayerService.exportTaxpayersToCsv(); 
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=taxpayer_list.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}