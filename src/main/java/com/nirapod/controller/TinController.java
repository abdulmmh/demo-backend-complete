package com.nirapod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nirapod.dto.request.TinRequest;
import com.nirapod.dto.response.TinResponse;
import com.nirapod.service.TinCertificateService;
import com.nirapod.service.TinService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tins")
// CrossOrigin removed — handled globally in SecurityConfig
public class TinController {

    @Autowired private TinService           tinService;
    @Autowired private TinCertificateService tinCertificateService;

    @PostMapping
    public ResponseEntity<TinResponse> create(@Valid @RequestBody TinRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tinService.createTin(req));
    }

    @GetMapping
    public ResponseEntity<List<TinResponse>> getAll() {
        return ResponseEntity.ok(tinService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TinResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tinService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TinResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TinRequest req) {
        return ResponseEntity.ok(tinService.updateTin(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tinService.deleteTin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/certificate")
    public void downloadCertificate(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {
        TinResponse tin = tinService.getById(id);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=TIN_Certificate_" + tin.getTinNumber() + ".pdf");
        tinCertificateService.generateCertificate(id, response);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tin_list.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        return new ResponseEntity<>(tinService.exportToCsv(), headers, HttpStatus.OK);
    }
}
