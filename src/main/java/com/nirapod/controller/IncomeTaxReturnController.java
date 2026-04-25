package com.nirapod.controller;

import com.nirapod.model.IncomeTaxReturn;
import com.nirapod.services.IncomeTaxReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/income-tax-returns")
@CrossOrigin(origins = "http://localhost:4200")
public class IncomeTaxReturnController {
    
    @Autowired
    private IncomeTaxReturnService incomeTaxReturnService;

    // Create
    @PostMapping
    public ResponseEntity<IncomeTaxReturn> submitReturn(@RequestBody IncomeTaxReturn itr) {
        return new ResponseEntity<>(incomeTaxReturnService.createReturn(itr), HttpStatus.CREATED);
    }

    // Get All
    @GetMapping
    public ResponseEntity<List<IncomeTaxReturn>> getAllReturns() {
        return new ResponseEntity<>(incomeTaxReturnService.getAllReturns(), HttpStatus.OK);
    }

    // Get By ID
    @GetMapping("/{id}")
    public ResponseEntity<IncomeTaxReturn> getReturnById(@PathVariable Long id) {
        return new ResponseEntity<>(incomeTaxReturnService.getReturnById(id), HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<IncomeTaxReturn> updateReturn(@PathVariable Long id, @RequestBody IncomeTaxReturn updatedData) {
        return new ResponseEntity<>(incomeTaxReturnService.updateReturn(id, updatedData), HttpStatus.OK);
    }
    
    // Status Update (Workflow) - Now using Service
    @PatchMapping("/{id}/status")
    public ResponseEntity<IncomeTaxReturn> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        String remarks = payload.get("remarks"); 
        String action = payload.get("action");
        String performedBy = payload.get("performedBy");
        String role = payload.get("role");

        IncomeTaxReturn updatedItr = incomeTaxReturnService.updateStatus(id, newStatus, remarks, action, performedBy, role);
        return ResponseEntity.ok(updatedItr);
    }

    // Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturn(@PathVariable Long id) {
        incomeTaxReturnService.softDeleteReturn(id);
        return ResponseEntity.noContent().build(); 
    }
    
 // Export Returns
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReturns() {
        byte[] csvData = incomeTaxReturnService.exportReturnsToCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=income_tax_returns.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}