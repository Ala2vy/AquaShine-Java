package com.aquashine.unit.memberd;

import com.aquashine.model.Booking;
import com.aquashine.model.Promo;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.PaymentRepository;
import com.aquashine.repository.PromoRepository;
import com.aquashine.service.PaymentService;
import com.aquashine.service.PromoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member D - Payment BVA")
class PaymentBvaTests {

    private PaymentService service() {
        BookingRepository bRepo = mock(BookingRepository.class);
        PaymentRepository pRepo = mock(PaymentRepository.class);
        PromoRepository promoRepo = mock(PromoRepository.class);
        PromoService promoService = new PromoService(promoRepo);

        Booking b = new Booking(1L, 1L, 1L, LocalDate.now().plusDays(2), LocalTime.of(10, 0), 1000);
        b.setId(1L);
        when(bRepo.findById(1L)).thenReturn(Optional.of(b));
        when(pRepo.findByBookingId(1L)).thenReturn(Optional.empty());
        when(pRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        return new PaymentService(pRepo, bRepo, promoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"4111111111111111", "1234567890123456", "9999999999999999"})
    @DisplayName("BVA: 16-digit cards accepted")
    void card_Valid(String card) {
        assertThatCode(() -> service().processPayment(1L, 1L, null, card))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"411111111111111", "41111111111111111", "123", ""})
    @DisplayName("BVA: non-16-digit cards rejected")
    void card_Invalid(String card) {
        assertThatThrownBy(() -> service().processPayment(1L, 1L, null, card))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("BVA: 16 digits with spaces accepted")
    void card_WithSpaces() {
        assertThatCode(() -> service().processPayment(1L, 1L, null, "4111 1111 1111 1111"))
            .doesNotThrowAnyException();
    }

    @Test @DisplayName("BVA: null card rejected")
    void card_Null() {
        assertThatThrownBy(() -> service().processPayment(1L, 1L, null, null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}