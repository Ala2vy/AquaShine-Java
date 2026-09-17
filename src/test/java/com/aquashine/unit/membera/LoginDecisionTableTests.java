package com.aquashine.unit.membera;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import com.aquashine.service.AuthService;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("DecisionTable")
@DisplayName("Member A - Login Decision Table")
class LoginDecisionTableTests {

    private AuthService seededService() {
        UserRepository repo = mock(UserRepository.class);
        // Fake in-memory store
        final User[] stored = new User[1];

        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            stored[0] = u;
            return u;
        });
        when(repo.existsByEmail(anyString())).thenAnswer(inv -> {
            String email = inv.getArgument(0);
            return stored[0] != null && stored[0].getEmail().equals(email);
        });
        when(repo.findByEmail(anyString())).thenAnswer(inv -> {
            String email = inv.getArgument(0);
            if (stored[0] != null && stored[0].getEmail().equals(email)) {
                return Optional.of(stored[0]);
            }
            return Optional.empty();
        });

        AuthService svc = new AuthService(repo);
        svc.register("test@aquashine.com", "Pass@123", "Test User", "1234567890");
        return svc;
    }

    @Test @DisplayName("R1: Valid email + valid password + exists -> success")
    void r1_valid_valid_exists() {
        assertThat(seededService().login("test@aquashine.com", "Pass@123")).isNotNull();
    }

    @Test @DisplayName("R2: Valid email + wrong password -> fail")
    void r2_valid_wrong() {
        assertThat(seededService().login("test@aquashine.com", "WrongPass1")).isNull();
    }

    @Test @DisplayName("R3: Valid email + valid password + no user -> fail")
    void r3_valid_valid_nouser() {
        assertThat(seededService().login("ghost@aquashine.com", "Pass@123")).isNull();
    }

    @Test @DisplayName("R4: Empty email -> fail")
    void r4_empty_email() {
        assertThat(seededService().login("", "Pass@123")).isNull();
    }

    @Test @DisplayName("R5: Empty password -> fail")
    void r5_empty_password() {
        assertThat(seededService().login("test@aquashine.com", "")).isNull();
    }

    @Test @DisplayName("R6: Uppercase email -> still succeeds")
    void r6_uppercase_email() {
        assertThat(seededService().login("TEST@AQUASHINE.COM", "Pass@123")).isNotNull();
    }
}