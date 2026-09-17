package com.aquashine.unit.memberc;

import com.aquashine.model.Vehicle;
import com.aquashine.model.WashService;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.BookingService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member C - Booking BVA")
class BookingBvaTests {

    private BookingService service() {
        VehicleRepository vRepo = mock(VehicleRepository.class);
        WashServiceRepository sRepo = mock(WashServiceRepository.class);
        BookingRepository bRepo = mock(BookingRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", "HATCHBACK", "", "", 2020);
        v.setId(1L);
        WashService s = new WashService("BASIC", "Basic", "BASIC", 500);
        s.setId(1L);
        when(vRepo.findById(1L)).thenReturn(Optional.of(v));
        when(sRepo.findById(1L)).thenReturn(Optional.of(s));
        when(bRepo.existsBySlotDateAndSlotTimeAndStatus(any(), any(), any())).thenReturn(false);
        when(bRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        return new BookingService(bRepo, vRepo, sRepo);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 15, 29, 30})
    @DisplayName("BVA: days ahead 0-30 accepted")
    void daysAhead_Valid(int daysAhead) {
        assertThatCode(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(daysAhead), LocalTime.of(10, 0)))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {31, 40, 100})
    @DisplayName("BVA: days ahead > 30 rejected")
    void daysAhead_TooFar(int daysAhead) {
        assertThatThrownBy(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(daysAhead), LocalTime.of(10, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 12, 19})
    @DisplayName("BVA: hours 8, 12, 19 valid (0 min)")
    void hourBoundary_Valid(int hour) {
        assertThatCode(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(hour, 0)))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 7, 20, 22, 23})
    @DisplayName("BVA: hours outside 8-20 rejected")
    void hourBoundary_Invalid(int hour) {
        assertThatThrownBy(() -> service().createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(hour, 0)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}