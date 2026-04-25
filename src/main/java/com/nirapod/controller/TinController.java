package com.nirapod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nirapod.model.Taxpayer;
import com.nirapod.model.Tin;
import com.nirapod.services.TaxpayerService;
import com.nirapod.services.TinCertificateService;
import com.nirapod.services.TinService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/tins")
@CrossOrigin(origins = "http://localhost:4200") 
public class TinController {

    @Autowired
    private TinService tinService;
    
    @Autowired
    private TaxpayerService taxpayerService;

    @Autowired
    private TinCertificateService tinCertificateService;

    @PostMapping
    public ResponseEntity<Tin> createTin(@RequestBody Tin tin) {
        Tin createdTin = tinService.createTin(tin);
        return ResponseEntity.ok(createdTin);
    }

    @GetMapping
    public ResponseEntity<List<Tin>> getAllTins() {
        return ResponseEntity.ok(tinService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tin> getTinById(@PathVariable Long id) {
        Tin tin = tinService.getById(id);
        return tin != null ? ResponseEntity.ok(tin) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tin> updateTin(@PathVariable Long id, @RequestBody Tin tin) {
        Tin updatedTin = tinService.updateTin(id, tin);
        return updatedTin != null ? ResponseEntity.ok(updatedTin) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTin(@PathVariable Long id) {
        tinService.deleteTin(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/certificate")
    public void downloadTinCertificate(@PathVariable Long id, HttpServletResponse response) throws Exception {

        Tin tin = tinService.getById(id);
        Taxpayer taxpayer = taxpayerService.getById(tin.getTaxpayerId());


        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=TIN_Certificate_" + tin.getTinNumber() + ".pdf";
        response.setHeader(headerKey, headerValue);


        tinCertificateService.generateCertificate(tin, taxpayer, response);
    }
    
 // Export TINs
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTins() {
        byte[] csvData = tinService.exportTinsToCsv();
        
        HttpHeaders headers = new HttpHeaders();
        // ফাইলের নাম সেট করে দেওয়া হচ্ছে
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tin_list.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}