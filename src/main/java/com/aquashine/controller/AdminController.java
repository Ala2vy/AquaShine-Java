package com.aquashine.controller;

import com.aquashine.model.Booking;
import com.aquashine.model.Payment;
import com.aquashine.model.User;
import com.aquashine.model.Vehicle;
import com.aquashine.model.WashService;
import com.aquashine.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;
    private final VehicleRepository vehicleRepo;
    private final WashServiceRepository serviceRepo;

    public AdminController(UserRepository userRepo, BookingRepository bookingRepo,
                           PaymentRepository paymentRepo, VehicleRepository vehicleRepo,
                           WashServiceRepository serviceRepo) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        this.paymentRepo = paymentRepo;
        this.vehicleRepo = vehicleRepo;
        this.serviceRepo = serviceRepo;
    }

    // ==================== STATS ====================
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        LocalDate today = LocalDate.now();
        List<Booking> allBookings = bookingRepo.findAll();
        List<Payment> allPayments = paymentRepo.findAll();

        // Today's revenue
        int todayRevenue = allPayments.stream()
            .filter(p -> p.getCreatedAt().toLocalDate().equals(today))
            .mapToInt(Payment::getFinalAmount)
            .sum();

        // Total revenue
        int totalRevenue = allPayments.stream().mapToInt(Payment::getFinalAmount).sum();

        long activeBookings = allBookings.stream()
            .filter(b -> "CONFIRMED".equals(b.getStatus()) &&
                        (b.getSlotDate().isAfter(today) ||
                         (b.getSlotDate().equals(today) && b.getSlotTime().isAfter(LocalTime.now()))))
            .count();

        long todayBookings = allBookings.stream()
            .filter(b -> b.getSlotDate().equals(today))
            .count();

        return ResponseEntity.ok(Map.of(
            "todayRevenue", todayRevenue,
            "totalRevenue", totalRevenue,
            "totalBookings", allBookings.size(),
            "activeBookings", activeBookings,
            "todayBookings", todayBookings,
            "totalUsers", userRepo.count(),
            "totalServices", serviceRepo.count(),
            "totalVehicles", vehicleRepo.count()
        ));
    }

    // ==================== USERS ====================
    @GetMapping("/users")
    public ResponseEntity<?> users() {
        List<Map<String, Object>> result = userRepo.findAll().stream()
            .map(u -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", u.getId());
                m.put("email", u.getEmail());
                m.put("fullName", u.getFullName());
                m.put("phone", u.getPhone());
                m.put("role", u.getRole());
                m.put("createdAt", u.getCreatedAt());
                return m;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        if (role == null || (!role.equals("USER") && !role.equals("ADMIN"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
        }
        var user = userRepo.findById(id);
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        user.get().setRole(role);
        userRepo.save(user.get());
        return ResponseEntity.ok(Map.of("message", "Role updated"));
    }

    // ==================== BOOKINGS ====================
    @GetMapping("/bookings")
    public ResponseEntity<?> bookings(@RequestParam(required = false) String status) {
        List<Booking> bookings = bookingRepo.findAll();

        if (status != null && !status.isBlank()) {
            bookings = bookings.stream()
                .filter(b -> b.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }

        // Sort newest first
        bookings.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // Enrich with user email
        List<Map<String, Object>> result = bookings.stream().map(b -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", b.getId());
            m.put("userId", b.getUserId());
            m.put("userEmail", userRepo.findById(b.getUserId()).map(User::getEmail).orElse("—"));
            m.put("vehicleId", b.getVehicleId());
            m.put("vehiclePlate", vehicleRepo.findById(b.getVehicleId()).map(Vehicle::getPlateNumber).orElse("—"));
            m.put("serviceId", b.getServiceId());
            m.put("serviceName", serviceRepo.findById(b.getServiceId()).map(WashService::getName).orElse("—"));
            m.put("slotDate", b.getSlotDate());
            m.put("slotTime", b.getSlotTime());
            m.put("status", b.getStatus());
            m.put("totalPrice", b.getTotalPrice());
            m.put("createdAt", b.getCreatedAt());
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/bookings/{id}/complete")
    public ResponseEntity<?> markComplete(@PathVariable Long id) {
        var booking = bookingRepo.findById(id);
        if (booking.isEmpty()) return ResponseEntity.notFound().build();
        booking.get().setStatus("COMPLETED");
        bookingRepo.save(booking.get());
        return ResponseEntity.ok(Map.of("message", "Marked complete"));
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> adminCancel(@PathVariable Long id) {
        var booking = bookingRepo.findById(id);
        if (booking.isEmpty()) return ResponseEntity.notFound().build();
        booking.get().setStatus("CANCELLED");
        bookingRepo.save(booking.get());
        return ResponseEntity.ok(Map.of("message", "Cancelled"));
    }

    // ==================== SERVICES ====================
    @GetMapping("/services")
    public ResponseEntity<?> services() {
        return ResponseEntity.ok(serviceRepo.findAll());
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        var svc = serviceRepo.findById(id);
        if (svc.isEmpty()) return ResponseEntity.notFound().build();

        WashService s = svc.get();
        if (body.containsKey("name")) s.setName((String) body.get("name"));
        if (body.containsKey("basePrice")) {
            int price = ((Number) body.get("basePrice")).intValue();
            if (price < 100 || price > 10000)
                return ResponseEntity.badRequest().body(Map.of("error", "Price must be 100-10000"));
            s.setBasePrice(price);
        }
        if (body.containsKey("active")) s.setActive((Boolean) body.get("active"));
        serviceRepo.save(s);
        return ResponseEntity.ok(Map.of("message", "Service updated"));
    }

    // ==================== PAYMENTS ====================
    @GetMapping("/payments")
    public ResponseEntity<?> payments() {
        List<Payment> payments = paymentRepo.findAll();
        payments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        List<Map<String, Object>> result = payments.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("bookingId", p.getBookingId());
            m.put("userEmail", userRepo.findById(p.getUserId()).map(User::getEmail).orElse("—"));
            m.put("originalAmount", p.getOriginalAmount());
            m.put("discountAmount", p.getDiscountAmount());
            m.put("finalAmount", p.getFinalAmount());
            m.put("promoCode", p.getPromoCode());
            m.put("transactionId", p.getTransactionId());
            m.put("status", p.getStatus());
            m.put("createdAt", p.getCreatedAt());
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ==================== VEHICLES ====================
    @GetMapping("/vehicles")
    public ResponseEntity<?> vehicles() {
        List<Map<String, Object>> result = vehicleRepo.findAll().stream().map(v -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", v.getId());
            m.put("userId", v.getUserId());
            m.put("userEmail", userRepo.findById(v.getUserId()).map(User::getEmail).orElse("—"));
            m.put("plateNumber", v.getPlateNumber());
            m.put("type", v.getType());
            m.put("make", v.getMake());
            m.put("model", v.getModel());
            m.put("year", v.getYear());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}