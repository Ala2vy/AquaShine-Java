package com.aquashine.unit.memberd;

import com.aquashine.model.Booking;
import com.aquashine.model.Payment;
import com.aquashine.model.Promo;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.PaymentRepository;
import com.aquashine.repository.PromoRepository;
import com.aquashine.service.PaymentService;
import com.aquashine.service.PromoService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Member D — component unit tests.
 *
 *   [1] PaymentService.processPayment       — creates a SUCCESS payment
 *   [2] PaymentService.processPayment       — rejects an invalid card
 *   [3] PromoService.calculateDiscount      — applies the promo percent
 *   [4] PromoService.calculateDiscount      — rejects an unknown code
 *   [5] PromoService.calculateDiscount      — rejects an expired promo
 */
@DisplayName("Member D - Payment & Promo Unit Tests")
class PaymentServiceUnitTests {

    private PaymentService paymentService() {
        BookingRepository bRepo = mock(BookingRepository.class);
        PaymentRepository pRepo = mock(PaymentRepository.class);
        PromoRepository promoRepo = mock(PromoRepository.class);

        Booking b = new Booking(1L, 1L, 1L, LocalDate.now().plusDays(3), LocalTime.of(10, 0), 1000);
        b.setId(1L);
        b.setStatus("CONFIRMED");

        when(bRepo.findById(1L)).thenReturn(Optional.of(b));
        when(pRepo.findByBookingId(1L)).thenReturn(Optional.empty());
        when(pRepo.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        return new PaymentService(pRepo, bRepo, new PromoService(promoRepo));
    }

    private PromoService promoService() {
        PromoRepository repo = mock(PromoRepository.class);
        Promo p = new Promo("WASH50", 50, 500, 100, LocalDateTime.now().plusDays(30));
        when(repo.findByCode("WASH50")).thenReturn(Optional.of(p));
        return new PromoService(repo);
    }

    @Test
    @DisplayName("[1] PaymentService.processPayment() — creates a SUCCESS payment")
    void processPaymentCreates() {
        Payment p = paymentService().processPayment(1L, 1L, null, "4111111111111111");
        assertThat(p).isNotNull();
        assertThat(p.getStatus()).isEqualTo("SUCCESS");
        assertThat(p.getFinalAmount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("[2] PaymentService.processPayment() — rejects an invalid card number")
    void processPaymentRejectsBadCard() {
        try {
            paymentService().processPayment(1L, 1L, null, "123");
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // pass — service rejected the card
        }
    }

    @Test
    @DisplayName("[3] PromoService.calculateDiscount() — applies the promo percent")
    void promoCalculatesDiscount() {
        int discount = promoService().calculateDiscount("WASH50", 1000);
        assertThat(discount).isEqualTo(500);
    }

    @Test
    @DisplayName("[4] PromoService.calculateDiscount() — rejects an unknown promo code")
    void promoRejectsUnknown() {
        PromoRepository repo = mock(PromoRepository.class);
        when(repo.findByCode(anyString())).thenReturn(Optional.empty());
        PromoService svc = new PromoService(repo);

        try {
            svc.calculateDiscount("GHOST", 1000);
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }

    @Test
    @DisplayName("[5] PromoService.calculateDiscount() — rejects an expired promo")
    void promoRejectsExpired() {
        PromoRepository repo = mock(PromoRepository.class);
        Promo expired = new Promo("OLD", 50, 500, 100, LocalDateTime.now().minusDays(1));
        when(repo.findByCode("OLD")).thenReturn(Optional.of(expired));
        PromoService svc = new PromoService(repo);

        try {
            svc.calculateDiscount("OLD", 1000);
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }
}