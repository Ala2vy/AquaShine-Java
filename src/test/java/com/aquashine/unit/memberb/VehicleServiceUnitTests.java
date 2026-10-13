package com.aquashine.unit.memberb;

import com.aquashine.model.Vehicle;
import com.aquashine.model.WashService;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.ServiceCatalogueService;
import com.aquashine.service.VehicleService;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Member B — component unit tests.
 *
 *   [1] VehicleService.addVehicle     — creates a vehicle
 *   [2] VehicleService.editVehicle    — updates an existing vehicle
 *   [3] VehicleService.deleteVehicle  — removes the vehicle
 *   [4] VehicleService.listVehicles   — returns all vehicles for a user
 *   [5] ServiceCatalogueService.createService — saves a new wash service
 */
@DisplayName("Member B - Vehicle & Service Unit Tests")
class VehicleServiceUnitTests {

    private VehicleService vehicleService() {
        VehicleRepository repo = mock(VehicleRepository.class);
        when(repo.countByUserId(anyLong())).thenReturn(0L);
        when(repo.existsByUserIdAndPlateNumber(anyLong(), anyString())).thenReturn(false);
        when(repo.save(any(Vehicle.class))).thenAnswer(inv -> {
            Vehicle v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });
        return new VehicleService(repo);
    }

    private VehicleService vehicleServiceWithOne() {
        VehicleRepository repo = mock(VehicleRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        v.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(v));
        when(repo.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));
        return new VehicleService(repo);
    }

    @Test
    @DisplayName("[1] VehicleService.addVehicle() — creates a vehicle and returns it")
    void addVehicleCreatesVehicle() {
        Vehicle v = vehicleService().addVehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        assertThat(v).isNotNull();
        assertThat(v.getPlateNumber()).isEqualTo("ABC123");
        assertThat(v.getType()).isEqualTo("SEDAN");
        assertThat(v.getYear()).isEqualTo(2020);
    }

    @Test
    @DisplayName("[2] VehicleService.editVehicle() — updates an existing vehicle")
    void editVehicleUpdates() {
        Optional<Vehicle> updated = vehicleServiceWithOne()
                .editVehicle(1L, 1L, "SUV", "Honda", "CRV", 2022);
        assertThat(updated).isPresent();
        assertThat(updated.get().getType()).isEqualTo("SUV");
        assertThat(updated.get().getMake()).isEqualTo("Honda");
        assertThat(updated.get().getYear()).isEqualTo(2022);
    }

    @Test
    @DisplayName("[3] VehicleService.deleteVehicle() — removes the vehicle from the repository")
    void deleteVehicleRemoves() {
        VehicleRepository repo = mock(VehicleRepository.class);
        Vehicle v = new Vehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        v.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(v));
        VehicleService svc = new VehicleService(repo);

        assertThat(svc.deleteVehicle(1L, 1L)).isTrue();
        verify(repo, times(1)).delete(v);
    }

    @Test
    @DisplayName("[4] VehicleService.listVehicles() — returns all vehicles for the user")
    void listVehiclesReturnsList() {
        VehicleRepository repo = mock(VehicleRepository.class);
        Vehicle v1 = new Vehicle(1L, "ABC123", "SEDAN", "Toyota", "Camry", 2020);
        Vehicle v2 = new Vehicle(1L, "XYZ789", "SUV", "Honda", "CRV", 2022);
        when(repo.findByUserId(1L)).thenReturn(List.of(v1, v2));

        VehicleService svc = new VehicleService(repo);
        List<Vehicle> result = svc.listVehicles(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Vehicle::getPlateNumber)
                .containsExactlyInAnyOrder("ABC123", "XYZ789");
    }

    @Test
    @DisplayName("[5] ServiceCatalogueService.createService() — saves a new wash service")
    void createServiceSaves() {
        WashServiceRepository repo = mock(WashServiceRepository.class);
        when(repo.findByCode(anyString())).thenReturn(Optional.empty());
        when(repo.save(any(WashService.class))).thenAnswer(inv -> {
            WashService s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });
        ServiceCatalogueService svc = new ServiceCatalogueService(repo);

        WashService created = svc.createService("DELUXE", "Deluxe Wash", "DELUXE", 2000);
        assertThat(created).isNotNull();
        assertThat(created.getCode()).isEqualTo("DELUXE");
        assertThat(created.getBasePrice()).isEqualTo(2000);
    }
}