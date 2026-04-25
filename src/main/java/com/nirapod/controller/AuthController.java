package com.nirapod.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.User;
import com.nirapod.services.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email    = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password required"));
        }

        List<User> users = userService.getAll();
        User matched = users.stream()
                .filter(u -> email.equals(u.getEmail()) && password.equals(u.getPassword()))
                .findFirst()
                .orElse(null);

        if (matched == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        // Update lastLogin
        matched.setLastLogin(LocalDateTime.now());
        userService.update(matched);

        // Build response (no real JWT yet — simple token placeholder)
        Map<String, Object> response = new HashMap<>();
        response.put("id",       matched.getId());
        response.put("fullName", matched.getFullName());
        response.put("email",    matched.getEmail());
        response.put("role",     matched.getRole());
        response.put("token",    "token-" + matched.getId() + "-" + System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Simplified: return a placeholder. Add JWT parsing later.
        return ResponseEntity.ok(Map.of("message", "Profile endpoint ready"));
    }
}
