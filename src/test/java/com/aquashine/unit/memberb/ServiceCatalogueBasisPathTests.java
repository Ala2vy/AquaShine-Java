package com.aquashine.unit.memberb;

import com.aquashine.model.WashService;
import com.aquashine.repository.WashServiceRepository;
import com.aquashine.service.ServiceCatalogueService;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member B - ServiceCatalogue Basis Path (V(G)=5)")
class ServiceCatalogueBasisPathTests {

    private ServiceCatalogueService service() {
        WashServiceRepository repo = mock(WashServiceRepository.class);
        when(repo.findByCode(anyString())).thenReturn(Optional.empty());
        when(repo.save(any(WashService.class))).thenAnswer(inv -> {
            WashService s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });
        return new ServiceCatalogueService(repo);
    }

    @Test @DisplayName("P1: empty code -> throws")
    void p1_empty_code() {
        assertThatThrownBy(() -> service().createService("", "Basic", "BASIC", 500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: empty name -> throws")
    void p2_empty_name() {
        assertThatThrownBy(() -> service().createService("BASIC", "", "BASIC", 500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: invalid category -> throws")
    void p3_invalid_category() {
        assertThatThrownBy(() -> service().createService("BASIC", "Basic", "SUPER", 500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P4: duplicate code -> throws")
    void p4_duplicate_code() {
        WashServiceRepository repo = mock(WashServiceRepository.class);
        WashService existing = new WashService("BASIC", "Basic", "BASIC", 500);
        when(repo.findByCode("BASIC")).thenReturn(Optional.of(existing));
        ServiceCatalogueService svc = new ServiceCatalogueService(repo);

        assertThatThrownBy(() -> svc.createService("BASIC", "Basic2", "BASIC", 500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: valid create -> saved")
    void p5_valid() {
        WashService s = service().createService("DELUXE", "Deluxe Wash", "DELUXE", 2000);
        assertThat(s).isNotNull();
        assertThat(s.getCode()).isEqualTo("DELUXE");
    }
}