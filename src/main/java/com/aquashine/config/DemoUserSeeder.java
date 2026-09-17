package com.aquashine.config;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoUserSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DemoUserSeeder(UserRepository users) {
        this.users = users;
    }

    @Override
    public void run(String... args) {
        seedIfMissing("abc@gmail.com",  "12345678",  "Admin Demo",    "9000000001", "ADMIN");
        seedIfMissing("abcd@gmail.com", "123456789", "Customer Demo", "9000000002", "USER");
    }

    private void seedIfMissing(String email, String pass, String name, String phone, String role) {
        if (users.findByEmail(email).isEmpty()) {
            User u = new User();
            u.setEmail(email);
            u.setPasswordHash(encoder.encode(pass));
            u.setFullName(name);
            u.setPhone(phone);
            u.setRole(role);
            users.save(u);
            System.out.println("Seeded demo user: " + email + " (" + role + ")");
        }
    }
}