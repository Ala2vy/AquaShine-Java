package com.aquashine.unit.memberb;

import com.aquashine.model.Vehicle;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.service.VehicleService;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member B - DeleteVehicle Basis Path (V(G)=4)")
class DeleteVehicleBasisPathTests {

    @Test @DisplayName("P1: vehicle not found -> false")
    void p1_not_found() {
        VehicleRepository repo = mock(VehicleRepository.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThat(new VehicleService(repo).deleteVehicle(1L, 99L)).isFalse();
    }

    @Test @DisplayName("P2: wrong owner -> throws")
    void p2_wrong_owner() {
        VehicleRepository repo = mock(VehicleRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "", "", 2020);
        v.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(v));
        assertThatThrownBy(() -> new VehicleService(repo).deleteVehicle(2L, 1L))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: correct owner -> deleted")
    void p3_correct_owner() {
        VehicleRepository repo = mock(VehicleRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "", "", 2020);
        v.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(v));
        assertThat(new VehicleService(repo).deleteVehicle(1L, 1L)).isTrue();
        verify(repo, times(1)).delete(v);
    }

    @Test @DisplayName("P4: user with no vehicles -> not found")
    void p4_empty() {
        VehicleRepository repo = mock(VehicleRepository.class);
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThat(new VehicleService(repo).deleteVehicle(99L, 1L)).isFalse();
    }
}