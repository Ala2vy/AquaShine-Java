package com.aquashine.unit.membera;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import com.aquashine.service.ProfileService;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BasisPath")
@DisplayName("Member A - ChangePassword Basis Path (V(G)=5)")
class ChangePasswordBasisPathTests {

    private ProfileService serviceWithUser() {
        UserRepository repo = mock(UserRepository.class);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        User u = new User("test@aquashine.com", enc.encode("Pass@123"), "Test User", "1234567890");
        u.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        return new ProfileService(repo);
    }

    @Test @DisplayName("P1: short new password -> throws")
    void p1_short_new_password() {
        ProfileService svc = serviceWithUser();
        assertThatThrownBy(() -> svc.changePassword(1L, "Pass@123", "Abc@1"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: new == old -> throws")
    void p2_same_as_old() {
        ProfileService svc = serviceWithUser();
        assertThatThrownBy(() -> svc.changePassword(1L, "Pass@123", "Pass@123"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: wrong old password -> returns false")
    void p3_wrong_old() {
        ProfileService svc = serviceWithUser();
        assertThat(svc.changePassword(1L, "WrongOld1", "NewPass1")).isFalse();
    }

    @Test @DisplayName("P4: valid change -> returns true")
    void p4_valid_change() {
        ProfileService svc = serviceWithUser();
        assertThat(svc.changePassword(1L, "Pass@123", "NewPass1")).isTrue();
    }

    @Test @DisplayName("P5: unknown user -> returns false")
    void p5_unknown_user() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        ProfileService svc = new ProfileService(repo);
        assertThat(svc.changePassword(99L, "Pass@123", "NewPass1")).isFalse();
    }
}