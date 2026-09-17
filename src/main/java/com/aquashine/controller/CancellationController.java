package com.aquashine.controller;

import com.aquashine.dto.CancellationRequest;
import com.aquashine.model.Booking;
import com.aquashine.service.CancellationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id == null ? null : ((Number) id).longValue();
    }

    @GetMapping("/{id}/refund-preview")
    public ResponseEntity<?> refundPreview(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        var opt = cancellationService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Booking not found"));

        Booking b = opt.get();
        if (!b.getUserId().equals(userId)) return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));

        LocalDateTime slot = LocalDateTime.of(b.getSlotDate(), b.getSlotTime());
        try {
            int percent = cancellationService.calculateRefundPercent(slot, LocalDateTime.now());
            int refund = b.getTotalPrice() * percent / 100;
            return ResponseEntity.ok(Map.of(
                "totalPrice", b.getTotalPrice(),
                "refundPercent", percent,
                "refundAmount", refund
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, @Valid @RequestBody CancellationRequest req,
                                    HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            cancellationService.cancel(userId, id, req.getReason());
            return ResponseEntity.ok(Map.of("message", "Booking cancelled"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<?> reschedule(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            java.time.LocalDate newDate = java.time.LocalDate.parse(body.get("slotDate"));
            java.time.LocalTime newTime = java.time.LocalTime.parse(body.get("slotTime"));
            cancellationService.reschedule(userId, id, newDate, newTime);
            return ResponseEntity.ok(Map.of("message", "Booking rescheduled"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}