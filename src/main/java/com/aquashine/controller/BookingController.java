package com.aquashine.controller;

import com.aquashine.dto.BookingRequest;
import com.aquashine.model.Booking;
import com.aquashine.service.BookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id == null ? null : ((Number) id).longValue();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BookingRequest req, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        try {
            Booking b = bookingService.createBooking(
                userId, req.getVehicleId(), req.getServiceId(),
                req.getSlotDate(), req.getSlotTime());
            return ResponseEntity.ok(Map.of(
                "message", "Booking confirmed",
                "bookingId", b.getId(),
                "totalPrice", b.getTotalPrice()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        List<Booking> bookings = bookingService.listUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
}