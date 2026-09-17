package com.aquashine.unit.memberc;

import com.aquashine.repository.BookingRepository;
import com.aquashine.service.CancellationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member C - Refund BVA")
class RefundPreviewBvaTests {

    private CancellationService service() {
        return new CancellationService(mock(BookingRepository.class));
    }

    @ParameterizedTest
    @CsvSource({
        "0,0",
        "1,25",
        "3,25",
        "4,50",
        "11,50",
        "12,75",
        "23,75",
        "24,100",
        "48,100",
        "72,100"
    })
    @DisplayName("BVA: hours before slot -> refund percent")
    void refundPercent_Boundary(long hoursBefore, int expected) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slot = now.plusHours(hoursBefore);
        int actual = service().calculateRefundPercent(slot, now);
        assertThat(actual).isEqualTo(expected);
    }

    @Test @DisplayName("Past booking -> throws")
    void past_booking() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusHours(1);
        assertThatThrownBy(() -> service().calculateRefundPercent(past, now))
            .isInstanceOf(IllegalArgumentException.class);
    }
}