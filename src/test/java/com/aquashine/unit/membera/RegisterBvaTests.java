package com.aquashine.unit.membera;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import com.aquashine.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member A - Register BVA Tests")
class RegisterBvaTests {

    private AuthService newService() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        return new AuthService(repo);
    }

    @ParameterizedTest
    @CsvSource({
        "Abc@123,false",
        "Abcd@123,true",
        "Abcde@123,true",
        "Abcdefghijklmnopq@12,true",
        "Abcdefghijklmnopqr@123,false"
    })
    @DisplayName("BVA: password length 8-20")
    void passwordLength_Boundary(String password, boolean shouldSucceed) {
        AuthService svc = newService();
        String email = "u" + System.nanoTime() + "@test.com";
        if (shouldSucceed) {
            assertThatCode(() -> svc.register(email, password, "Test User", "1234567890"))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> svc.register(email, password, "Test User", "1234567890"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "123456789,false",
        "1234567890,true",
        "12345678901,true",
        "123456789012,true",
        "1234567890123,true",
        "12345678901234,false"
    })
    @DisplayName("BVA: phone digits 10-13")
    void phoneDigits_Boundary(String phone, boolean shouldSucceed) {
        AuthService svc = newService();
        String email = "u" + System.nanoTime() + "@test.com";
        if (shouldSucceed) {
            assertThatCode(() -> svc.register(email, "Pass@123", "Test User", phone))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> svc.register(email, "Pass@123", "Test User", phone))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}