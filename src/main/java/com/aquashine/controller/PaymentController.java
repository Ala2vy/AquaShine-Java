package com.aquashine.controller;

import com.aquashine.dto.PaymentRequest;
import com.aquashine.dto.PromoRequest;
import com.aquashine.model.Payment;
import com.aquashine.service.PaymentService;
import com.aquashine.service.PromoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PromoService promoService;

    public PaymentController(PaymentService paymentService, PromoService promoService) {
        this.paymentService = paymentService;
        this.promoService = promoService;
    }

    private Long currentUserId(HttpSession session) {
        if (session == null) return null;
        Object id = session.getAttribute("userId");
        if (id == null) return null;
        if (id instanceof Number) return ((Number) id).longValue();
        try { return Long.parseLong(id.toString()); } catch (Exception e) { return null; }
    }

    @PostMapping
    public ResponseEntity<?> pay(@Valid @RequestBody PaymentRequest req, HttpSession session) {
        try {
            Long userId = currentUserId(session);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
            }

            Payment p = paymentService.processPayment(
                userId,
                req.getBookingId(),
                req.getPromoCode(),
                req.getCardNumber()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment successful");
            response.put("paymentId", p.getId());
            response.put("transactionId", p.getTransactionId());
            response.put("originalAmount", p.getOriginalAmount());
            response.put("discountAmount", p.getDiscountAmount());
            response.put("finalAmount", p.getFinalAmount());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            log.warn("Payment rejected: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Payment failed with exception", ex);
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Payment processing failed",
                "type", ex.getClass().getSimpleName(),
                "message", ex.getMessage() == null ? "unknown" : ex.getMessage()
            ));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getByBooking(@PathVariable Long bookingId, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        return paymentService.findByBookingId(bookingId)
            .map(p -> {
                Map<String, Object> m = new HashMap<>();
                m.put("transactionId", p.getTransactionId());
                m.put("finalAmount", p.getFinalAmount());
                m.put("discountAmount", p.getDiscountAmount());
                m.put("originalAmount", p.getOriginalAmount());
                m.put("promoCode", p.getPromoCode() == null ? "" : p.getPromoCode());
                return ResponseEntity.ok(m);
            })
            .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Payment not found")));
    }

    @PostMapping("/validate-promo")
    public ResponseEntity<?> validatePromo(@Valid @RequestBody PromoRequest req) {
        try {
            int discount = promoService.calculateDiscount(req.getCode(), req.getAmount());
            Map<String, Object> m = new HashMap<>();
            m.put("valid", true);
            m.put("discount", discount);
            m.put("finalAmount", req.getAmount() - discount);
            return ResponseEntity.ok(m);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Promo validation failed", ex);
            return ResponseEntity.status(500).body(Map.of("valid", false, "error", "Validation failed"));
        }
    }
}