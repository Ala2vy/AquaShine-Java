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

@Tag("ECT")
@DisplayName("Member C - Cancellation Equivalence Classes")
class CancellationEquivalenceTests {

    private CancellationService serviceWith(Booking b) {
        BookingRepository repo = mock(BookingRepository.class);
        when(repo.findById(1L)).thenReturn(Optional.ofNullable(b));
        when(repo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        return new CancellationService(repo);
    }

    private Booking makeBooking(String status) {
        Booking b = new Booking(1L, 1L, 1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), 500);
        b.setId(1L);
        b.setStatus(status);
        return b;
    }

    @Test @DisplayName("ECT-1: status=CONFIRMED -> cancellable")
    void ect1_confirmed() {
        Booking b = makeBooking("CONFIRMED");
        assertThatCode(() -> serviceWith(b).cancel(1L, 1L, "test")).doesNotThrowAnyException();
    }

    @Test @DisplayName("ECT-2: status=CANCELLED -> not cancellable")
    void ect2_cancelled() {
        Booking b = makeBooking("CANCELLED");
        assertThatThrownBy(() -> serviceWith(b).cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ECT-3: status=COMPLETED -> not cancellable")
    void ect3_completed() {
        Booking b = makeBooking("COMPLETED");
        assertThatThrownBy(() -> serviceWith(b).cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ECT-4: status=PENDING -> not cancellable")
    void ect4_pending() {
        Booking b = makeBooking("PENDING");
        assertThatThrownBy(() -> serviceWith(b).cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ECT-5: empty status -> not cancellable")
    void ect5_empty() {
        Booking b = makeBooking("");
        assertThatThrownBy(() -> serviceWith(b).cancel(1L, 1L, "test"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}