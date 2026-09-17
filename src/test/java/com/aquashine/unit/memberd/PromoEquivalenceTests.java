package com.aquashine.unit.memberd;

import com.aquashine.model.Promo;
import com.aquashine.repository.PromoRepository;
import com.aquashine.service.PromoService;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("ECT")
@DisplayName("Member D - Promo Equivalence Classes")
class PromoEquivalenceTests {

    private PromoService svc(Promo p) {
        PromoRepository repo = mock(PromoRepository.class);
        when(repo.findByCode(anyString())).thenReturn(Optional.ofNullable(p));
        return new PromoService(repo);
    }

    private Promo valid() {
        return new Promo("WASH50", 50, 500, 100, LocalDateTime.now().plusDays(30));
    }

    @Test @DisplayName("ECT-1: valid code -> works")
    void ect1_valid() {
        assertThatCode(() -> svc(valid()).calculateDiscount("WASH50", 1000)).doesNotThrowAnyException();
    }

    @Test @DisplayName("ECT-2: unknown code -> throws")
    void ect2_unknown() {
        PromoRepository repo = mock(PromoRepository.class);
        when(repo.findByCode(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new PromoService(repo).calculateDiscount("GHOST", 1000))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ECT-3: expired code -> throws")
    void ect3_expired() {
        Promo p = new Promo("EXPIRED", 50, 500, 100, LocalDateTime.now().minusDays(1));
        assertThatThrownBy(() -> svc(p).calculateDiscount("EXPIRED", 1000))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ECT-4: inactive code -> throws")
    void ect4_inactive() {
        Promo p = valid();
        p.setActive(false);
        assertThatThrownBy(() -> svc(p).calculateDiscount("WASH50", 1000))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ECT-5: too short code -> throws")
    void ect5_too_short() {
        assertThatThrownBy(() -> svc(valid()).calculateDiscount("AB", 1000))
            .isInstanceOf(IllegalArgumentException.class);
    }
}