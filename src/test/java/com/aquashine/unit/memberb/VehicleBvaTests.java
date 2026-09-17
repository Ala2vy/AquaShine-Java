package com.aquashine.unit.memberb;

import com.aquashine.model.Vehicle;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.service.VehicleService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Year;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member B - Vehicle BVA Tests")
class VehicleBvaTests {

    private VehicleService serviceWithCount(long existing) {
        VehicleRepository repo = mock(VehicleRepository.class);
        when(repo.countByUserId(1L)).thenReturn(existing);
        when(repo.existsByUserIdAndPlateNumber(eq(1L), anyString())).thenReturn(false);
        when(repo.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));
        return new VehicleService(repo);
    }

    @ParameterizedTest
    @CsvSource({
        "AB,false",
        "ABC,true",
        "ABCD,true",
        "ABCDEFGHIJKLMNO,true",
        "ABCDEFGHIJKLMNOP,false"
    })
    @DisplayName("BVA: plate number length 3-15")
    void plateLength_Boundary(String plate, boolean shouldSucceed) {
        VehicleService svc = serviceWithCount(0);
        if (shouldSucceed) {
            assertThatCode(() -> svc.addVehicle(1L, plate, "SEDAN", "Toyota", "Camry", 2020))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> svc.addVehicle(1L, plate, "SEDAN", "Toyota", "Camry", 2020))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("BVA: valid year range (1900 to current_year+1)")
    void year_validRange() {
        int currentYear = Year.now().getValue();
        int maxYear = currentYear + 1;

        VehicleService svc = serviceWithCount(0);

        // 1900 = min boundary
        assertThatCode(() -> svc.addVehicle(1L, "ABC123", "SEDAN", "", "", 1900))
            .doesNotThrowAnyException();

        // 1899 = below min
        assertThatThrownBy(() -> svc.addVehicle(1L, "ABC124", "SEDAN", "", "", 1899))
            .isInstanceOf(IllegalArgumentException.class);

        // current year = valid
        assertThatCode(() -> svc.addVehicle(1L, "ABC125", "SEDAN", "", "", currentYear))
            .doesNotThrowAnyException();

        // current + 1 = max boundary = valid
        assertThatCode(() -> svc.addVehicle(1L, "ABC126", "SEDAN", "", "", maxYear))
            .doesNotThrowAnyException();

        // current + 2 = above max = reject
        assertThatThrownBy(() -> svc.addVehicle(1L, "ABC127", "SEDAN", "", "", maxYear + 1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("BVA: 5 vehicles already -> 6th rejected")
    void maxVehicles() {
        VehicleService svc = serviceWithCount(5);
        assertThatThrownBy(() -> svc.addVehicle(1L, "ABC123", "SEDAN", "", "", 2020))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("BVA: 4 vehicles already -> 5th accepted")
    void underMaxVehicles() {
        VehicleService svc = serviceWithCount(4);
        assertThatCode(() -> svc.addVehicle(1L, "ABC123", "SEDAN", "", "", 2020))
            .doesNotThrowAnyException();
    }
}