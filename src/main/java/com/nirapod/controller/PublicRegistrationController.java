package com.nirapod.controller;

import com.nirapod.dto.request.UserRegistrationRequest;
import com.nirapod.dto.response.RegistrationResponse;
import com.nirapod.service.PublicRegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicRegistrationController {

    @Autowired
    private PublicRegistrationService registrationService;
    
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody UserRegistrationRequest request) {

        RegistrationResponse response = registrationService.register(request);
        return ResponseEntity.ok(response);
    }
}
