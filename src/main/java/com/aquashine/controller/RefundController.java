package com.aquashine.controller;

import com.aquashine.model.Booking;
import com.aquashine.model.RefundRequest;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.RefundRequestRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundRequestRepository refundRepo;
    private final BookingRepository bookingRepo;

    public RefundController(RefundRequestRepository refundRepo, BookingRepository bookingRepo) {
        this.refundRepo = refundRepo;
        this.bookingRepo = bookingRepo;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id == null ? null : ((Number) id).longValue();
    }

    @PostMapping
    public ResponseEntity<?> requestRefund(@RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        Long bookingId = ((Number) body.get("bookingId")).longValue();
        String reason = body.get("reason") == null ? "" : body.get("reason").toString();

        var bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
        Booking booking = bookingOpt.get();
        if (!booking.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Not your booking"));
        }

        RefundRequest refund = new RefundRequest(bookingId, userId, booking.getTotalPrice(), reason);
        refundRepo.save(refund);
        return ResponseEntity.ok(Map.of("message", "Refund requested", "refundId", refund.getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<?> myRefunds(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        List<RefundRequest> list = refundRepo.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(list);
    }
}