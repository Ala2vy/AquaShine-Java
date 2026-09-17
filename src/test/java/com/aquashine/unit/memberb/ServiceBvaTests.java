package com.aquashine.unit.memberb;

import com.aquashine.model.WashService;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.ServiceCatalogueService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member B - Service BVA Tests")
class ServiceBvaTests {

    private ServiceCatalogueService service() {
        WashServiceRepository repo = mock(WashServiceRepository.class);
        when(repo.findByCode(anyString())).thenReturn(Optional.empty());
        when(repo.save(any(WashService.class))).thenAnswer(inv -> inv.getArgument(0));
        return new ServiceCatalogueService(repo);
    }

    @ParameterizedTest
    @CsvSource({
        "50,false",
        "99,false",
        "100,true",
        "101,true",
        "5000,true",
        "9999,true",
        "10000,true",
        "10001,false"
    })
    @DisplayName("BVA: price range 100-10000")
    void priceRange_Boundary(int price, boolean shouldSucceed) {
        ServiceCatalogueService svc = service();
        if (shouldSucceed) {
            assertThatCode(() -> svc.createService("TEST" + price, "Test", "BASIC", price))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> svc.createService("TEST" + price, "Test", "BASIC", price))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}