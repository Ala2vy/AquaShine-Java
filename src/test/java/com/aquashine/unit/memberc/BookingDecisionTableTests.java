package com.aquashine.unit.memberc;

import com.aquashine.model.Vehicle;
import com.aquashine.model.WashService;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.BookingService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("DecisionTable")
@DisplayName("Member C - Booking Price Decision Table")
class BookingDecisionTableTests {

    private BookingService serviceForVehicleType(String vehicleType, int basePrice) {
        VehicleRepository vRepo = mock(VehicleRepository.class);
        WashServiceRepository sRepo = mock(WashServiceRepository.class);
        BookingRepository bRepo = mock(BookingRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", vehicleType, "", "", 2020);
        v.setId(1L);
        WashService s = new WashService("X", "X", "BASIC", basePrice);
        s.setId(1L);
        when(vRepo.findById(1L)).thenReturn(Optional.of(v));
        when(sRepo.findById(1L)).thenReturn(Optional.of(s));
        when(bRepo.existsBySlotDateAndSlotTimeAndStatus(any(), any(), any())).thenReturn(false);
        when(bRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        return new BookingService(bRepo, vRepo, sRepo);
    }

    @ParameterizedTest
    @CsvSource({
        "HATCHBACK,500,500",
        "SEDAN,500,750",
        "SUV,500,1000",
        "HATCHBACK,1000,1000",
        "SEDAN,1000,1500",
        "SUV,1000,2000"
    })
    @DisplayName("DT: total = base * multiplier(vehicleType)")
    void priceMatrix(String vehicleType, int basePrice, int expected) {
        BookingService svc = serviceForVehicleType(vehicleType, basePrice);
        var booking = svc.createBooking(1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        assertThat(booking.getTotalPrice()).isEqualTo(expected);
    }
}