package com.nirapod.controller;

import com.nirapod.dto.RegistrationResponse;
import com.nirapod.dto.UserRegistrationRequest;
import com.nirapod.services.PublicRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * PublicRegistrationController
 *
 * Exposed at /api/public/** — intentionally separate from /api/auth/**
 * so that security config can whitelist this path without touching the
 * authenticated auth endpoints.
 *
 * No JWT / Bearer token required on any endpoint here.
 */
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicRegistrationController {

    @Autowired
    private PublicRegistrationService registrationService;

    /**
     * POST /api/public/register
     *
     * Creates a portal User account + Taxpayer record + TIN in one atomic
     * transaction. Always assigns TAXPAYER role — role cannot be supplied
     * by the client payload.
     *
     * Responses:
     *   200 OK       → RegistrationResponse with tinNumber
     *   400 Bad Request → missing / invalid fields (IllegalArgumentException)
     *   409 Conflict    → duplicate email, NID, or RJSC (IllegalStateException)
     *
     * Both 400 and 409 are handled globally by GlobalExceptionHandler.
     */
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody UserRegistrationRequest request) {

        RegistrationResponse response = registrationService.register(request);
        return ResponseEntity.ok(response);
    }
}
