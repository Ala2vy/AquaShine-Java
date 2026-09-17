package com.aquashine.unit.memberd;

import com.aquashine.model.Promo;
import com.aquashine.repository.PromoRepository;
import com.aquashine.service.PromoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("DecisionTable")
@DisplayName("Member D - Promo Decision Table")
class PaymentDecisionTableTests {

    private PromoService promoWith(int percent, int minAmount, int usedCount, int maxUses, LocalDateTime expiry, boolean active) {
        PromoRepository repo = mock(PromoRepository.class);
        Promo p = new Promo("TEST10", percent, minAmount, maxUses, expiry);
        p.setUsedCount(usedCount);
        p.setActive(active);
        when(repo.findByCode("TEST10")).thenReturn(Optional.of(p));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        return new PromoService(repo);
    }

    @ParameterizedTest
    @CsvSource({
        "1000, 50, 500",
        "1000, 10, 100",
        "2000, 20, 400",
        "1500, 30, 450"
    })
    @DisplayName("DT: discount = amount * percent / 100")
    void discount_Calc(int amount, int percent, int expected) {
        PromoService svc = promoWith(percent, 100, 0, 100, LocalDateTime.now().plusDays(30), true);
        assertThat(svc.calculateDiscount("TEST10", amount)).isEqualTo(expected);
    }

    @Test @DisplayName("DT: amount below min -> reject")
    void belowMin() {
        PromoService svc = promoWith(50, 1000, 0, 100, LocalDateTime.now().plusDays(30), true);
        assertThatThrownBy(() -> svc.calculateDiscount("TEST10", 500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("DT: max uses reached -> reject")
    void maxedOut() {
        PromoService svc = promoWith(50, 100, 100, 100, LocalDateTime.now().plusDays(30), true);
        assertThatThrownBy(() -> svc.calculateDiscount("TEST10", 1000))
            .isInstanceOf(IllegalArgumentException.class);
    }
}