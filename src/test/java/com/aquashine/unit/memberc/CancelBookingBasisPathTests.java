package com.aquashine.unit.memberc;

import com.aquashine.model.Booking;
import com.aquashine.repository.BookingRepository;
import com.aquashine.service.CancellationService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member C - Cancel Booking Basis Path (V(G)=5)")
class CancelBookingBasisPathTests {

    private CancellationService serviceWith(Booking booking) {
        BookingRepository repo = mock(BookingRepository.class);
        when(repo.findById(1L)).thenReturn(Optional.ofNullable(booking));
        when(repo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        return new CancellationService(repo);
    }

    private Booking makeBooking(Long userId, LocalDate date, LocalTime time, String status) {
        Booking b = new Booking(userId, 1L, 1L, date, time, 500);
        b.setId(1L);
        b.setStatus(status);
        return b;
    }

    @Test @DisplayName("P1: booking not found -> throws")
    void p1_not_found() {
        CancellationService svc = serviceWith(null);
        assertThatThrownBy(() -> svc.cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: wrong owner -> throws")
    void p2_wrong_owner() {
        Booking b = makeBooking(2L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), "CONFIRMED");
        CancellationService svc = serviceWith(b);
        assertThatThrownBy(() -> svc.cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: not CONFIRMED -> throws")
    void p3_not_confirmed() {
        Booking b = makeBooking(1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), "CANCELLED");
        CancellationService svc = serviceWith(b);
        assertThatThrownBy(() -> svc.cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P4: past booking -> throws")
    void p4_past_booking() {
        Booking b = makeBooking(1L, LocalDate.now().minusDays(1), LocalTime.of(10, 0), "CONFIRMED");
        CancellationService svc = serviceWith(b);
        assertThatThrownBy(() -> svc.cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: valid cancel -> status changes")
    void p5_valid_cancel() {
        Booking b = makeBooking(1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), "CONFIRMED");
        CancellationService svc = serviceWith(b);
        Booking result = svc.cancel(1L, 1L, "changed mind");
        assertThat(result.getStatus()).isEqualTo("CANCELLED");
    }
}