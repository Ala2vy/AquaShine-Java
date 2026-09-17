package com.aquashine.service;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;
    public static final int PHONE_MIN_DIGITS   = 10;
    public static final int PHONE_MAX_DIGITS   = 13;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String email, String password, String fullName, String phone) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (password == null || password.length() < PASSWORD_MIN_LENGTH
                || password.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Password must be " + PASSWORD_MIN_LENGTH + "-" + PASSWORD_MAX_LENGTH + " characters");
        }

        String digits = phone == null ? "" : phone.replaceAll("\\D", "");
        if (digits.length() < PHONE_MIN_DIGITS || digits.length() > PHONE_MAX_DIGITS) {
            throw new IllegalArgumentException(
                "Phone must be " + PHONE_MIN_DIGITS + "-" + PHONE_MAX_DIGITS + " digits");
        }

        String normalized = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalized)) {
            return null;
        }

        User user = new User(
            normalized,
            passwordEncoder.encode(password),
            fullName.trim(),
            digits
        );
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String normalized = email.trim().toLowerCase();
        Optional<User> found = userRepository.findByEmail(normalized);
        if (found.isEmpty()) return null;

        User user = found.get();
        return passwordEncoder.matches(password, user.getPasswordHash()) ? user : null;
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
}