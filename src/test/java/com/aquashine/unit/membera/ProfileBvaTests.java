package com.aquashine.unit.membera;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import com.aquashine.service.ProfileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("BVA")
@DisplayName("Member A - Profile BVA Tests")
class ProfileBvaTests {

    private ProfileService serviceWithUser() {
        UserRepository repo = mock(UserRepository.class);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        User u = new User("test@aquashine.com", enc.encode("Pass@123"), "Test User", "1234567890");
        u.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        return new ProfileService(repo);
    }

    @ParameterizedTest
    @CsvSource({
        "AB,false",
        "ABC,true",
        "ABCD,true",
        "ABCDE,true"
    })
    @DisplayName("BVA: name length boundaries")
    void nameLength_Boundary(String name, boolean shouldSucceed) {
        ProfileService svc = serviceWithUser();
        if (shouldSucceed) {
            assertThatCode(() -> svc.updateProfile(1L, name, "1234567890"))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> svc.updateProfile(1L, name, "1234567890"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "123456789,false",
        "1234567890,true",
        "1234567890123,true",
        "12345678901234,false"
    })
    @DisplayName("BVA: phone digits boundaries")
    void phoneDigits_Boundary(String phone, boolean shouldSucceed) {
        ProfileService svc = serviceWithUser();
        if (shouldSucceed) {
            assertThatCode(() -> svc.updateProfile(1L, "Valid Name", phone))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> svc.updateProfile(1L, "Valid Name", phone))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}