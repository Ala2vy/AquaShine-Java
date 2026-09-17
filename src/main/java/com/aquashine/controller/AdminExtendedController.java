package com.aquashine.controller;

import com.aquashine.model.*;
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
public class AdminExtendedController {

    private final RefundRequestRepository refundRepo;
    private final BlockedSlotRepository blockedRepo;
    private final PromoRepository promoRepo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;

    public AdminExtendedController(RefundRequestRepository refundRepo,
                                   BlockedSlotRepository blockedRepo,
                                   PromoRepository promoRepo,
                                   UserRepository userRepo,
                                   BookingRepository bookingRepo) {
        this.refundRepo = refundRepo;
        this.blockedRepo = blockedRepo;
        this.promoRepo = promoRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id == null ? null : ((Number) id).longValue();
    }

    // ==================== REFUNDS ====================
    @GetMapping("/refunds")
    public ResponseEntity<?> refunds(@RequestParam(required = false) String status) {
        List<RefundRequest> list = (status == null || status.isBlank())
            ? refundRepo.findAll()
            : refundRepo.findByStatusOrderByCreatedAtDesc(status);
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        List<Map<String, Object>> enriched = list.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("bookingId", r.getBookingId());
            m.put("userEmail", userRepo.findById(r.getUserId()).map(User::getEmail).orElse("—"));
            m.put("amount", r.getAmount());
            m.put("reason", r.getReason());
            m.put("status", r.getStatus());
            m.put("createdAt", r.getCreatedAt());
            m.put("reviewedAt", r.getReviewedAt());
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(enriched);
    }

    @PostMapping("/refunds/{id}/approve")
    public ResponseEntity<?> approveRefund(@PathVariable Long id, HttpSession session) {
        var opt = refundRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        RefundRequest r = opt.get();
        r.setStatus("APPROVED");
        r.setReviewedBy(currentUserId(session));
        r.setReviewedAt(LocalDateTime.now());
        refundRepo.save(r);
        return ResponseEntity.ok(Map.of("message", "Refund approved"));
    }

    @PostMapping("/refunds/{id}/reject")
    public ResponseEntity<?> rejectRefund(@PathVariable Long id, HttpSession session) {
        var opt = refundRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        RefundRequest r = opt.get();
        r.setStatus("REJECTED");
        r.setReviewedBy(currentUserId(session));
        r.setReviewedAt(LocalDateTime.now());
        refundRepo.save(r);
        return ResponseEntity.ok(Map.of("message", "Refund rejected"));
    }

    // ==================== PROMOS ====================
    @GetMapping("/promos")
    public ResponseEntity<?> promos() {
        return ResponseEntity.ok(promoRepo.findAll());
    }

    @PostMapping("/promos")
    public ResponseEntity<?> createPromo(@RequestBody Map<String, Object> body) {
        String code = ((String) body.get("code")).toUpperCase();
        int percent = ((Number) body.get("discountPercent")).intValue();
        int minAmount = ((Number) body.get("minAmount")).intValue();
        int maxUses = ((Number) body.get("maxUses")).intValue();

        if (percent < 1 || percent > 100) return ResponseEntity.badRequest().body(Map.of("error", "Discount must be 1-100"));
        if (minAmount < 0) return ResponseEntity.badRequest().body(Map.of("error", "Min amount must be >= 0"));
        if (maxUses < 1) return ResponseEntity.badRequest().body(Map.of("error", "Max uses must be >= 1"));
        if (promoRepo.findByCode(code).isPresent()) return ResponseEntity.badRequest().body(Map.of("error", "Code already exists"));

        Promo p = new Promo(code, percent, minAmount, maxUses, LocalDateTime.now().plusYears(1));
        promoRepo.save(p);
        return ResponseEntity.ok(Map.of("message", "Promo created", "id", p.getId()));
    }

    @PostMapping("/promos/{id}/toggle")
    public ResponseEntity<?> togglePromo(@PathVariable Long id) {
        var opt = promoRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Promo p = opt.get();
        p.setActive(!p.getActive());
        promoRepo.save(p);
        return ResponseEntity.ok(Map.of("message", "Toggled", "active", p.getActive()));
    }

    // ==================== SCHEDULE (BLOCKED SLOTS) ====================
    @GetMapping("/schedule")
    public ResponseEntity<?> schedule(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank())
            ? LocalDate.now()
            : LocalDate.parse(date);
        List<BlockedSlot> slots = blockedRepo.findBySlotDate(d);
        return ResponseEntity.ok(Map.of("date", d.toString(), "blocked", slots));
    }

    @PostMapping("/schedule/block")
    public ResponseEntity<?> blockSlot(@RequestBody Map<String, Object> body, HttpSession session) {
        String dateStr = (String) body.get("slotDate");
        String timeStr = (String) body.get("slotTime");
        String reason = body.get("reason") == null ? "" : body.get("reason").toString();

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);

        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot block past dates"));
        }
        if (blockedRepo.existsBySlotDateAndSlotTime(date, time)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Slot already blocked"));
        }

        BlockedSlot bs = new BlockedSlot(date, time, reason, currentUserId(session));
        blockedRepo.save(bs);
        return ResponseEntity.ok(Map.of("message", "Slot blocked"));
    }

    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<?> unblockSlot(@PathVariable Long id) {
        if (!blockedRepo.existsById(id)) return ResponseEntity.notFound().build();
        blockedRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Slot unblocked"));
    }

    // ==================== ANALYTICS ====================
    @GetMapping("/analytics/revenue")
    public ResponseEntity<?> revenueAnalytics(@RequestParam(defaultValue = "7") int days) {
        List<Booking> bookings = bookingRepo.findAll();
        LocalDate start = LocalDate.now().minusDays(days - 1);
        List<Map<String, Object>> series = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            int revenue = bookings.stream()
                .filter(b -> b.getSlotDate().equals(d) && "COMPLETED".equals(b.getStatus()))
                .mapToInt(Booking::getTotalPrice)
                .sum();
            int count = (int) bookings.stream().filter(b -> b.getSlotDate().equals(d)).count();
            Map<String, Object> point = new HashMap<>();
            point.put("date", d.toString());
            point.put("revenue", revenue);
            point.put("bookings", count);
            series.add(point);
        }

        return ResponseEntity.ok(series);
    }
}