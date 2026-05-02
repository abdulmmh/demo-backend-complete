package com.nirapod.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.nirapod.dto.request.NoticeRequest;
import com.nirapod.dto.response.NoticeResponse;
import com.nirapod.service.NoticeService;

@RestController
@RequestMapping("/api/notices")
// CrossOrigin removed — handled globally in SecurityConfig
public class NoticeController {

    @Autowired private NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResponse> create(@Valid @RequestBody NoticeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAll() {
        return ResponseEntity.ok(noticeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getById(id));
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<NoticeResponse>> getByTaxpayer(@PathVariable Long taxpayerId) {
        return ResponseEntity.ok(noticeService.getByTaxpayerId(taxpayerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequest req) {
        return ResponseEntity.ok(noticeService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
