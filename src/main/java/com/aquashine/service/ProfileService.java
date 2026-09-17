package com.aquashine.service;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    public static final int NAME_MIN_LENGTH  = 3;
    public static final int NAME_MAX_LENGTH  = 50;
    public static final int PHONE_MIN_DIGITS = 10;
    public static final int PHONE_MAX_DIGITS = 13;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateProfile(Long userId, String fullName, String phone) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name required");
        }
        if (fullName.length() < NAME_MIN_LENGTH || fullName.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Name must be " + NAME_MIN_LENGTH + "-" + NAME_MAX_LENGTH + " characters");
        }

        String digits = phone == null ? "" : phone.replaceAll("\\D", "");
        if (digits.length() < PHONE_MIN_DIGITS || digits.length() > PHONE_MAX_DIGITS) {
            throw new IllegalArgumentException(
                "Phone must be " + PHONE_MIN_DIGITS + "-" + PHONE_MAX_DIGITS + " digits");
        }

        Optional<User> found = userRepository.findById(userId);
        if (found.isEmpty()) return null;

        User user = found.get();
        user.setFullName(fullName.trim());
        user.setPhone(digits);
        return userRepository.save(user);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (newPassword == null
                || newPassword.length() < PASSWORD_MIN_LENGTH
                || newPassword.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Password must be " + PASSWORD_MIN_LENGTH + "-" + PASSWORD_MAX_LENGTH + " characters");
        }

        if (oldPassword != null && oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password must differ from old");
        }

        Optional<User> found = userRepository.findById(userId);
        if (found.isEmpty()) return false;

        User user = found.get();
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public Optional<User> getById(Long userId) {
        return userRepository.findById(userId);
    }
}