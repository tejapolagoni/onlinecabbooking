package com.cab.booking.controller;

import com.cab.booking.entity.User;
import com.cab.booking.repository.UserRepository;
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
        this.adminSetupKey = adminSetupKey;
    }

    @PostMapping("/register")
    public User register(@RequestBody User u,
                         @RequestHeader(value = "X-Admin-Setup-Key", required = false) String providedSetupKey) {
        if (repo.existsByEmail(u.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
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
        u.setRole(role);
        u.setPassword(encoder.encode(u.getPassword()));
        return repo.save(u);
    }

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> body) {
        User u = repo.findByEmail(body.get("email"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(body.get("password"), u.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return u;
    }
}
