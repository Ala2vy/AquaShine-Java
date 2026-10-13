package com.aquashine.unit.memberc;

import com.aquashine.model.Booking;
import com.aquashine.model.Vehicle;
import com.aquashine.model.WashService;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.BookingService;
import com.aquashine.service.CancellationService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Member C — component unit tests.
 *
 *   [1] BookingService.createBooking       — creates a booking
 *   [2] BookingService.createBooking       — computes the price from vehicle type × base
 *   [3] BookingService.findById            — fetches a booking by id
 *   [4] CancellationService.cancel         — changes status to CANCELLED
 *   [5] CancellationService.calculateRefundPercent — 100% refund at 24h
 */
@DisplayName("Member C - Booking & Cancellation Unit Tests")
class BookingServiceUnitTests {

    private BookingService bookingService() {
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

    private CancellationService cancellationService() {
        BookingRepository repo = mock(BookingRepository.class);
        Booking b = new Booking(1L, 1L, 1L, LocalDate.now().plusDays(3), LocalTime.of(10, 0), 500);
        b.setId(1L);
        b.setStatus("CONFIRMED");
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        when(repo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        return new CancellationService(repo);
    }

    @Test
    @DisplayName("[1] BookingService.createBooking() — creates a booking and returns it")
    void createBookingSaves() {
        Booking b = bookingService().createBooking(
                1L, 1L, 1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0));
        assertThat(b).isNotNull();
        assertThat(b.getUserId()).isEqualTo(1L);
        assertThat(b.getVehicleId()).isEqualTo(1L);
        assertThat(b.getServiceId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[2] BookingService.createBooking() — computes the price from vehicle type × base")
    void createBookingComputesPrice() {
        Booking b = bookingService().createBooking(
                1L, 1L, 1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0));
        // SEDAN multiplier is 1.5x, base 500 → 750
        assertThat(b.getTotalPrice()).isEqualTo(750);
    }

    @Test
    @DisplayName("[3] BookingService.findById() — returns the booking")
    void findByIdReturns() {
        BookingRepository repo = mock(BookingRepository.class);
        Booking b = new Booking(1L, 1L, 1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), 500);
        b.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(b));

        BookingService svc = new BookingService(repo, mock(VehicleRepository.class), mock(WashServiceRepository.class));
        Optional<Booking> found = svc.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[4] CancellationService.cancel() — changes the booking status to CANCELLED")
    void cancelChangesStatus() {
        Booking result = cancellationService().cancel(1L, 1L, "changed mind");
        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        assertThat(result.getCancelledReason()).isEqualTo("changed mind");
    }

    @Test
    @DisplayName("[5] CancellationService.calculateRefundPercent() — 100% for >= 24h notice")
    void refundPercentFull() {
        CancellationService svc = new CancellationService(mock(BookingRepository.class));
        LocalDateTime now = LocalDateTime.now();
        assertThat(svc.calculateRefundPercent(now.plusHours(24), now)).isEqualTo(100);
    }
}