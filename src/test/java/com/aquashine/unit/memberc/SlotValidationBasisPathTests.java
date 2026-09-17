package com.aquashine.unit.memberc;

import com.aquashine.model.Vehicle;
import com.aquashine.model.WashService;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.BookingService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member C - Slot Validation Basis Path")
class SlotValidationBasisPathTests {

    private BookingService service() {
        VehicleRepository vRepo = mock(VehicleRepository.class);
        WashServiceRepository sRepo = mock(WashServiceRepository.class);
        BookingRepository bRepo = mock(BookingRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "", "", 2020);
        v.setId(1L);
        WashService s = new WashService("BASIC", "Basic", "BASIC", 500);
        s.setId(1L);
        when(vRepo.findById(1L)).thenReturn(Optional.of(v));
        when(sRepo.findById(1L)).thenReturn(Optional.of(s));
        when(bRepo.existsBySlotDateAndSlotTimeAndStatus(any(), any(), any())).thenReturn(false);
        when(bRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        return new BookingService(bRepo, vRepo, sRepo);
    }

    @Test @DisplayName("P1: 6:00 AM (too early) -> throws")
    void p1_too_early() {
        assertThatThrownBy(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(6, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: 8:00 AM (min boundary) -> valid")
    void p2_min_boundary() {
        assertThatCode(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(8, 0)))
            .doesNotThrowAnyException();
    }

    @Test @DisplayName("P3: 19:30 (last slot) -> valid")
    void p3_last_slot() {
        assertThatCode(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(19, 30)))
            .doesNotThrowAnyException();
    }

    @Test @DisplayName("P4: 20:00 (too late) -> throws")
    void p4_too_late() {
        assertThatThrownBy(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(20, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: 15 min granularity -> throws")
    void p5_bad_granularity() {
        assertThatThrownBy(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 15)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}