package com.cab.booking.controller;

import com.cab.booking.entity.User;
import com.cab.booking.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository repo;
    private final String adminSetupKey;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository repo, @Value("${app.admin-setup-key:}") String adminSetupKey) {
        this.repo = repo;
        this.adminSetupKey = adminSetupKey == null ? "" : adminSetupKey.trim();
    }

    @PostMapping("/register")
    public User register(@Valid @RequestBody User u,
                         @RequestHeader(value = "X-Admin-Setup-Key", required = false) String providedSetupKey) {
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        String email = normalizeEmail(u.getEmail());
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (repo.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        String role = u.getRole() == null ? "USER" : u.getRole().trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(role)) {
            if (adminSetupKey.isBlank()
                    || providedSetupKey == null
                    || !MessageDigest.isEqual(
                            adminSetupKey.getBytes(StandardCharsets.UTF_8),
                            providedSetupKey.getBytes(StandardCharsets.UTF_8))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Valid admin setup key required");
            }
        } else if (!"DRIVER".equals(role) && !"USER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be USER, DRIVER, or ADMIN");
        }
        if (u.getPassword() == null || u.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        u.setEmail(email);
        u.setRole(role);
        u.setPassword(encoder.encode(u.getPassword()));
        return repo.save(u);
    }

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> body) {
        if (body == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        String email = normalizeEmail(body.get("email"));
        String password = body.get("password");
        if (email == null || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        User u = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(password, u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return u;
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim();
    }
}
