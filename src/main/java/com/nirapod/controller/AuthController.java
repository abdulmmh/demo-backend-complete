package com.nirapod.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.nirapod.model.User;
import com.nirapod.services.UserService;

/**
 * AuthController — updated to handle both:
 *   - Legacy plain-text passwords (internal staff created before BCrypt was added)
 *   - BCrypt-hashed passwords (all accounts created via /public/register)
 *
 * The dual-check ensures zero disruption to existing demo/test accounts while
 * correctly verifying hashed passwords for self-registered taxpayers.
 *
 * Migration path: when all passwords are BCrypt-hashed (Phase 2), remove the
 * plain-text fallback check entirely.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    // Same strength (12) used in PublicRegistrationService
    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email    = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and password required."));
        }

        List<User> users = userService.getAll();
        User matched = users.stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst()
                .orElse(null);

        if (matched == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password."));
        }

        // Dual password check:
        // 1. BCrypt match (self-registered taxpayers)
        // 2. Plain-text match (existing internal staff accounts — legacy only)
        boolean passwordValid = isPasswordValid(password, matched.getPassword());

        if (!passwordValid) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password."));
        }

        // Block inactive accounts
        if ("Inactive".equals(matched.getStatus()) || "Suspended".equals(matched.getStatus())) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Your account has been " + matched.getStatus().toLowerCase() +
                            ". Please contact the NBR helpdesk."));
        }

        // Update lastLogin timestamp
        matched.setLastLogin(LocalDateTime.now());
        userService.update(matched);

        // Build response — no real JWT yet (placeholder token)
        Map<String, Object> response = new HashMap<>();
        response.put("id",       matched.getId());
        response.put("fullName", matched.getFullName());
        response.put("email",    matched.getEmail());
        response.put("role",     matched.getRole());
        response.put("token",    "token-" + matched.getId() + "-" + System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Checks the submitted password against the stored value.
     *
     * BCrypt hashes always start with "$2a$" or "$2b$".
     * Plain-text passwords don't — so we can detect which check to run
     * without needing a separate column flag on the User entity.
     */
    private boolean isPasswordValid(String submitted, String stored) {
        if (stored == null) return false;

        if (stored.startsWith("$2a$") || stored.startsWith("$2b$")) {
            // BCrypt hash — use BCrypt verify
            return bcrypt.matches(submitted, stored);
        } else {
            // Legacy plain-text (internal demo accounts)
            return stored.equals(submitted);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(Map.of("message", "Profile endpoint ready."));
    }
}
