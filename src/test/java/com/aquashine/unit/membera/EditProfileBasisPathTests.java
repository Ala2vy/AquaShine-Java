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
@DisplayName("Member A - EditProfile Basis Path (V(G)=5)")
class EditProfileBasisPathTests {

    private ProfileService serviceWithUser() {
        UserRepository repo = mock(UserRepository.class);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        User u = new User("test@aquashine.com", enc.encode("Pass@123"), "Old Name", "1234567890");
        u.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        return new ProfileService(repo);
    }

    @Test @DisplayName("P1: empty name -> throws")
    void p1_empty_name() {
        ProfileService svc = serviceWithUser();
        assertThatThrownBy(() -> svc.updateProfile(1L, "", "1234567890"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P2: short name (2 chars) -> throws")
    void p2_short_name() {
        ProfileService svc = serviceWithUser();
        assertThatThrownBy(() -> svc.updateProfile(1L, "AB", "1234567890"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P3: long name (51 chars) -> throws")
    void p3_long_name() {
        ProfileService svc = serviceWithUser();
        String longName = "A".repeat(51);
        assertThatThrownBy(() -> svc.updateProfile(1L, longName, "1234567890"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P4: bad phone (9 digits) -> throws")
    void p4_bad_phone() {
        ProfileService svc = serviceWithUser();
        assertThatThrownBy(() -> svc.updateProfile(1L, "Valid Name", "123456789"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("P5: valid update -> succeeds")
    void p5_valid_update() {
        ProfileService svc = serviceWithUser();
        User updated = svc.updateProfile(1L, "New Name", "9876543210");
        assertThat(updated).isNotNull();
        assertThat(updated.getFullName()).isEqualTo("New Name");
        assertThat(updated.getPhone()).isEqualTo("9876543210");
    }
}