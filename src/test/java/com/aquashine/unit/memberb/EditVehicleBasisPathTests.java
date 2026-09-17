package com.aquashine.unit.memberb;

import com.aquashine.model.Vehicle;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.service.VehicleService;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member B - EditVehicle Basis Path (V(G)=5)")
class EditVehicleBasisPathTests {

    private VehicleService serviceWithVehicle(Long userId) {
        VehicleRepository repo = mock(VehicleRepository.class);
        Vehicle v = new Vehicle(userId, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        v.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(v));
        when(repo.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));
        return new VehicleService(repo);
    }

    @Test @DisplayName("P1: invalid type -> throws")
    void p1_invalid_type() {
        assertThatThrownBy(() -> serviceWithVehicle(1L).editVehicle(1L, 1L, "TRUCK", "X", "Y", 2020))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: invalid year -> throws")
    void p2_invalid_year() {
        assertThatThrownBy(() -> serviceWithVehicle(1L).editVehicle(1L, 1L, "SEDAN", "X", "Y", 1800))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: vehicle not found -> empty")
    void p3_not_found() {
        VehicleRepository repo = mock(VehicleRepository.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        VehicleService svc = new VehicleService(repo);
        assertThat(svc.editVehicle(1L, 99L, "SEDAN", "X", "Y", 2020)).isEmpty();
    }

    @Test @DisplayName("P4: wrong owner -> throws")
    void p4_wrong_owner() {
        VehicleService svc = serviceWithVehicle(1L); // owned by user 1
        assertThatThrownBy(() -> svc.editVehicle(2L, 1L, "SEDAN", "X", "Y", 2020))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: valid edit -> updated")
    void p5_valid_edit() {
        VehicleService svc = serviceWithVehicle(1L);
        Optional<Vehicle> updated = svc.editVehicle(1L, 1L, "SUV", "Honda", "CRV", 2022);
        assertThat(updated).isPresent();
        assertThat(updated.get().getType()).isEqualTo("SUV");
    }
}