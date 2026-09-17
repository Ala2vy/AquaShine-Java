package com.aquashine.controller;

import com.aquashine.dto.ChangePasswordRequest;
import com.aquashine.dto.EditProfileRequest;
import com.aquashine.model.User;
import com.aquashine.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id == null ? null : ((Number) id).longValue();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        Optional<User> user = profileService.getById(userId);
        if (user.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        User u = user.get();
        return ResponseEntity.ok(Map.of(
            "id", u.getId(),
            "email", u.getEmail(),
            "fullName", u.getFullName(),
            "phone", u.getPhone()
        ));
    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(@Valid @RequestBody EditProfileRequest req, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        try {
            User updated = profileService.updateProfile(userId, req.getFullName(), req.getPhone());
            if (updated == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated",
                "fullName", updated.getFullName(),
                "phone", updated.getPhone()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        try {
            boolean ok = profileService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
            if (!ok) {
                return ResponseEntity.badRequest().body(Map.of("error", "Old password is incorrect"));
            }
            return ResponseEntity.ok(Map.of("message", "Password changed"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}