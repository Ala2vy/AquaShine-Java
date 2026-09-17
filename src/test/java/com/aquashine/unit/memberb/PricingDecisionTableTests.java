package com.aquashine.unit.memberb;

import com.aquashine.model.WashService;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.ServiceCatalogueService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("DecisionTable")
@DisplayName("Member B - Pricing Decision Table")
class PricingDecisionTableTests {

    private ServiceCatalogueService serviceWithBase(int basePrice) {
        WashServiceRepository repo = mock(WashServiceRepository.class);
        WashService svc = new WashService("X", "Test", "BASIC", basePrice);
        svc.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(svc));
        return new ServiceCatalogueService(repo);
    }

    @ParameterizedTest
    @CsvSource({
        "HATCHBACK,500,500",
        "SEDAN,500,750",
        "SUV,500,1000",
        "HATCHBACK,1000,1000",
        "SEDAN,1000,1500",
        "SUV,1000,2000",
        "HATCHBACK,2000,2000",
        "SUV,2000,4000"
    })
    @DisplayName("DT: price = base * multiplier(vehicleType)")
    void priceMatrix(String vehicleType, int basePrice, int expected) {
        ServiceCatalogueService svc = serviceWithBase(basePrice);
        int actual = svc.calculatePrice(1L, vehicleType);
        assertThat(actual).isEqualTo(expected);
    }

    @Test @DisplayName("DT: invalid vehicle type -> throws")
    void invalidVehicleType() {
        ServiceCatalogueService svc = serviceWithBase(500);
        assertThatThrownBy(() -> svc.calculatePrice(1L, "TRUCK"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}