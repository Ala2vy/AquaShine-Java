package com.aquashine.controller;

import com.aquashine.dto.LoginRequest;
import com.aquashine.dto.RegisterRequest;
import com.aquashine.model.User;
import com.aquashine.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req, HttpSession session) {
        try {
            User user = authService.register(req.getEmail(), req.getPassword(),
                                             req.getFullName(), req.getPhone());
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already registered"));
            }
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            return ResponseEntity.ok(Map.of("message", "Registered", "email", user.getEmail()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        User user = authService.login(req.getEmail(), req.getPassword());
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            return ResponseEntity.ok(Map.of(
                "message", "Logged in",
                "email", user.getEmail(),
                "role", user.getRole(),
                "redirect", "ADMIN".equals(user.getRole()) ? "/admin/index.html" : "/dashboard.html"
            ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        Map<String, Object> out = new HashMap<>();
        out.put("userId", userId);
        out.put("email", session.getAttribute("userEmail"));
        return ResponseEntity.ok(out);
    }
}