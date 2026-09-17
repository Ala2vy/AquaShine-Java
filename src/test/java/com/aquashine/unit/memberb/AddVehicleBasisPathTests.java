package com.aquashine.unit.memberb;

import com.aquashine.model.Vehicle;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.service.VehicleService;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member B - AddVehicle Basis Path (V(G)=5)")
class AddVehicleBasisPathTests {

    private VehicleService serviceAllowing() {
        VehicleRepository repo = mock(VehicleRepository.class);
        when(repo.countByUserId(1L)).thenReturn(0L);
        when(repo.existsByUserIdAndPlateNumber(eq(1L), anyString())).thenReturn(false);
        when(repo.save(any(Vehicle.class))).thenAnswer(inv -> {
            Vehicle v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });
        return new VehicleService(repo);
    }

    @Test
    @DisplayName("P1: empty plate -> throws")
    void p1_empty_plate() {
        assertThatThrownBy(() -> serviceAllowing().addVehicle(1L, "", "SEDAN", "Toyota", "Camry", 2020))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("P2: short plate (<3) -> throws")
    void p2_short_plate() {
        assertThatThrownBy(() -> serviceAllowing().addVehicle(1L, "AB", "SEDAN", "Toyota", "Camry", 2020))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("P3: invalid type -> throws")
    void p3_invalid_type() {
        assertThatThrownBy(() -> serviceAllowing().addVehicle(1L, "ABC123", "TRUCK", "Tata", "X", 2020))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("P4: invalid year (1899) -> throws")
    void p4_invalid_year() {
        assertThatThrownBy(() -> serviceAllowing().addVehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 1899))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("P5: valid vehicle -> saved")
    void p5_valid() {
        Vehicle v = serviceAllowing().addVehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        assertThat(v).isNotNull();
        assertThat(v.getPlateNumber()).isEqualTo("ABC123");
    }
}