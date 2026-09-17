package com.aquashine.unit.membera;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import com.aquashine.service.AuthService;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member A - Login Basis Path (V(G)=5)")
class LoginBasisPathTests {

    private AuthService seededService() {
        UserRepository repo = mock(UserRepository.class);
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

    @Test @DisplayName("P1: empty inputs -> return null")
    void p1_empty_inputs() {
        assertThat(seededService().login("", "")).isNull();
    }

    @Test @DisplayName("P2: unknown user -> return null")
    void p2_unknown_user() {
        assertThat(seededService().login("unknown@test.com", "Pass@123")).isNull();
    }

    @Test @DisplayName("P3: wrong password -> return null")
    void p3_wrong_password() {
        assertThat(seededService().login("test@aquashine.com", "WrongPass1")).isNull();
    }

    @Test @DisplayName("P4: valid credentials -> return user")
    void p4_valid_credentials() {
        User u = seededService().login("test@aquashine.com", "Pass@123");
        assertThat(u).isNotNull();
        assertThat(u.getEmail()).isEqualTo("test@aquashine.com");
    }

    @Test @DisplayName("P5: whitespace around email -> still succeeds")
    void p5_whitespace_email() {
        assertThat(seededService().login("  test@aquashine.com  ", "Pass@123")).isNotNull();
    }
}