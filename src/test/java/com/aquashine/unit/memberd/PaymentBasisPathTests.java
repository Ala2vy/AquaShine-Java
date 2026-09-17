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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member D - Payment Basis Path (V(G)=5)")
class PaymentBasisPathTests {

    private PaymentService serviceWithBooking(Booking b, boolean alreadyPaid) {
        BookingRepository bRepo = mock(BookingRepository.class);
        PaymentRepository pRepo = mock(PaymentRepository.class);
        PromoRepository promoRepo = mock(PromoRepository.class);
        when(bRepo.findById(1L)).thenReturn(Optional.ofNullable(b));
        when(pRepo.findByBookingId(1L)).thenReturn(alreadyPaid ? Optional.of(new Payment()) : Optional.empty());
        when(pRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        return new PaymentService(pRepo, bRepo, new PromoService(promoRepo));
    }

    private Booking makeBooking(Long userId, String status, int price) {
        Booking b = new Booking(userId, 1L, 1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), price);
        b.setId(1L);
        b.setStatus(status);
        return b;
    }

    @Test @DisplayName("P1: booking not found -> throws")
    void p1_not_found() {
        PaymentService svc = serviceWithBooking(null, false);
        assertThatThrownBy(() -> svc.processPayment(1L, 1L, null, "4111111111111111"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: wrong owner -> throws")
    void p2_wrong_owner() {
        Booking b = makeBooking(2L, "CONFIRMED", 1000);
        PaymentService svc = serviceWithBooking(b, false);
        assertThatThrownBy(() -> svc.processPayment(1L, 1L, null, "4111111111111111"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: not CONFIRMED -> throws")
    void p3_not_confirmed() {
        Booking b = makeBooking(1L, "CANCELLED", 1000);
        PaymentService svc = serviceWithBooking(b, false);
        assertThatThrownBy(() -> svc.processPayment(1L, 1L, null, "4111111111111111"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P4: already paid -> throws")
    void p4_already_paid() {
        Booking b = makeBooking(1L, "CONFIRMED", 1000);
        PaymentService svc = serviceWithBooking(b, true);
        assertThatThrownBy(() -> svc.processPayment(1L, 1L, null, "4111111111111111"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: valid -> payment saved")
    void p5_valid() {
        Booking b = makeBooking(1L, "CONFIRMED", 1000);
        PaymentService svc = serviceWithBooking(b, false);
        Payment p = svc.processPayment(1L, 1L, null, "4111111111111111");
        assertThat(p).isNotNull();
        assertThat(p.getFinalAmount()).isEqualTo(1000);
        assertThat(p.getStatus()).isEqualTo("SUCCESS");
    }
}