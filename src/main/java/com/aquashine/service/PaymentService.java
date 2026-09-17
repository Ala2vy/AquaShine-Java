package com.aquashine.service;

import com.aquashine.model.Booking;
import com.aquashine.model.Payment;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PromoService promoService;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          PromoService promoService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.promoService = promoService;
    }

    @Transactional
    public Payment processPayment(Long userId, Long bookingId, String promoCode, String cardNumber) {
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID is required");
        }

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Booking does not belong to you");
        }
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is not payable (status: " + booking.getStatus() + ")");
        }
        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new IllegalArgumentException("Booking already paid");
        }

        String digits = cardNumber == null ? "" : cardNumber.replaceAll("\\D", "");
        if (digits.length() != 16) {
            throw new IllegalArgumentException("Card number must be 16 digits");
        }

        int original = booking.getTotalPrice() == null ? 0 : booking.getTotalPrice();
        int discount = 0;
        String appliedCode = null;

        if (promoCode != null && !promoCode.isBlank()) {
            try {
                discount = promoService.calculateDiscount(promoCode, original);
                appliedCode = promoCode.trim().toUpperCase();
            } catch (IllegalArgumentException ex) {
                log.warn("Promo rejected: {}", ex.getMessage());
                throw ex;
            }
        }

        int finalAmount = Math.max(0, original - discount);

        Payment payment = new Payment(userId, bookingId, original, discount, finalAmount, appliedCode);
        payment.setTransactionId("TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000));

        Payment saved = paymentRepository.save(payment);
        log.info("Payment saved: booking={} amount={} txn={}", bookingId, finalAmount, saved.getTransactionId());

        if (appliedCode != null) {
            try {
                promoService.incrementUsage(appliedCode);
            } catch (Exception e) {
                log.warn("Promo usage increment failed: {}", e.getMessage());
            }
        }

        // Mark booking as paid
        try {
            booking.setStatus("PAID");
            bookingRepository.save(booking);
        } catch (Exception e) {
            log.warn("Could not mark booking as PAID: {}", e.getMessage());
        }

        return saved;
    }

    public Optional<Payment> findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}