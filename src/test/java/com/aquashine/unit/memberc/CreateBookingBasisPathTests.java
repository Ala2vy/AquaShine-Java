package com.aquashine.unit.memberc;

import com.aquashine.model.Booking;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member C - CreateBooking Basis Path (V(G)=5)")
class CreateBookingBasisPathTests {

    private BookingService service() {
        VehicleRepository vRepo = mock(VehicleRepository.class);
        WashServiceRepository sRepo = mock(WashServiceRepository.class);
        BookingRepository bRepo = mock(BookingRepository.class);

        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        v.setId(1L);
        WashService s = new WashService("BASIC", "Basic", "BASIC", 500);
        s.setId(1L);

        when(vRepo.findById(1L)).thenReturn(Optional.of(v));
        when(sRepo.findById(1L)).thenReturn(Optional.of(s));
        when(bRepo.existsBySlotDateAndSlotTimeAndStatus(any(), any(), any())).thenReturn(false);
        when(bRepo.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        return new BookingService(bRepo, vRepo, sRepo);
    }

    @Test @DisplayName("P1: unknown vehicle -> throws")
    void p1_unknown_vehicle() {
        VehicleRepository vRepo = mock(VehicleRepository.class);
        when(vRepo.findById(99L)).thenReturn(Optional.empty());
        BookingService svc = new BookingService(mock(BookingRepository.class), vRepo, mock(WashServiceRepository.class));
        assertThatThrownBy(() -> svc.createBooking(1L, 99L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: past date -> throws")
    void p2_past_date() {
        BookingService svc = service();
        assertThatThrownBy(() -> svc.createBooking(1L, 1L, 1L, LocalDate.now().minusDays(1), LocalTime.of(10, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: invalid time (early morning) -> throws")
    void p3_invalid_time() {
        BookingService svc = service();
        assertThatThrownBy(() -> svc.createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(6, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P4: slot already taken -> throws")
    void p4_slot_taken() {
        VehicleRepository vRepo = mock(VehicleRepository.class);
        WashServiceRepository sRepo = mock(WashServiceRepository.class);
        BookingRepository bRepo = mock(BookingRepository.class);

        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "", "", 2020);
        v.setId(1L);
        WashService s = new WashService("BASIC", "Basic", "BASIC", 500);
        s.setId(1L);

        when(vRepo.findById(1L)).thenReturn(Optional.of(v));
        when(sRepo.findById(1L)).thenReturn(Optional.of(s));
        when(bRepo.existsBySlotDateAndSlotTimeAndStatus(any(), any(), eq("CONFIRMED"))).thenReturn(true);

        BookingService svc = new BookingService(bRepo, vRepo, sRepo);
        assertThatThrownBy(() -> svc.createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: valid booking -> saved")
    void p5_valid() {
        BookingService svc = service();
        Booking b = svc.createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        assertThat(b).isNotNull();
        assertThat(b.getUserId()).isEqualTo(1L);
    }
}