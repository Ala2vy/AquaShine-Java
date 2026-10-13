package com.aquashine.unit.membera;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import com.aquashine.service.AuthService;
import com.aquashine.service.ProfileService;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Member A — component unit tests.
 *
 *   [1] AuthService.register      — creates a user with a BCrypt hash
 *   [2] AuthService.login         — returns the user for valid credentials
 *   [3] AuthService.login         — returns null for wrong password
 *   [4] ProfileService.updateProfile — updates name and phone
 *   [5] ProfileService.changePassword — accepts the correct current password
 */
@DisplayName("Member A - Auth & Profile Unit Tests")
class AuthServiceUnitTests {

    private AuthService authService() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        return new AuthService(repo);
    }

    private ProfileService profileService() {
        UserRepository repo = mock(UserRepository.class);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        User u = new User("test@aquashine.com", enc.encode("Pass@123"), "Test User", "1234567890");
        u.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        return new ProfileService(repo);
    }

    @Test
    @DisplayName("[1] AuthService.register() — creates a user and returns it")
    void registerCreatesUser() {
        User u = authService().register("new@test.com", "Pass@123", "Test User", "1234567890");
        assertThat(u).isNotNull();
        assertThat(u.getEmail()).isEqualTo("new@test.com");
        // password should be stored as a BCrypt hash, not plaintext
        assertThat(u.getPasswordHash()).startsWith("$2a$").isNotEqualTo("Pass@123");
    }

    @Test
    @DisplayName("[2] AuthService.login() — returns the user for valid credentials")
    void loginReturnsUser() {
        UserRepository repo = mock(UserRepository.class);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        User stored = new User("test@aquashine.com", enc.encode("Pass@123"), "Test User", "1234567890");
        stored.setId(1L);
        when(repo.findByEmail("test@aquashine.com")).thenReturn(Optional.of(stored));

        AuthService svc = new AuthService(repo);
        User result = svc.login("test@aquashine.com", "Pass@123");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@aquashine.com");
    }

    @Test
    @DisplayName("[3] AuthService.login() — returns null for wrong password")
    void loginRejectsWrongPassword() {
        UserRepository repo = mock(UserRepository.class);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        User stored = new User("test@aquashine.com", enc.encode("Pass@123"), "Test User", "1234567890");
        stored.setId(1L);
        when(repo.findByEmail("test@aquashine.com")).thenReturn(Optional.of(stored));

        AuthService svc = new AuthService(repo);
        assertThat(svc.login("test@aquashine.com", "WrongPass")).isNull();
    }

    @Test
    @DisplayName("[4] ProfileService.updateProfile() — updates full name and phone")
    void updateProfileChangesFields() {
        User updated = profileService().updateProfile(1L, "New Name", "9876543210");
        assertThat(updated).isNotNull();
        assertThat(updated.getFullName()).isEqualTo("New Name");
        assertThat(updated.getPhone()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("[5] ProfileService.changePassword() — accepts the correct current password")
    void changePasswordSucceeds() {
        boolean ok = profileService().changePassword(1L, "Pass@123", "NewPass1");
        assertThat(ok).isTrue();
    }
}