package com.aquashine.config;

import com.aquashine.model.User;
import com.aquashine.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds demo users plus a small amount of demo data so admin dashboards
 * have something to show. All inserts are best-effort: failures are logged
 * but do not crash startup.
 */
@Component
public class DemoUserSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DemoUserSeeder(UserRepository users, JdbcTemplate jdbc) {
        this.users = users;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        Long adminId    = seedUser("abc@gmail.com",  "12345678",  "Admin Demo",    "9000000001", "ADMIN");
        Long customerId = seedUser("abcd@gmail.com", "123456789", "Customer Demo", "9000000002", "USER");

        if (customerId != null) {
            seedVehicles(customerId);
            seedBookings(customerId);
            seedPayments(customerId);
        }
        System.out.println("[DemoUserSeeder] Done.");
    }

    private Long seedUser(String email, String pass, String name, String phone, String role) {
        var existing = users.findByEmail(email);
        if (existing.isPresent()) return existing.get().getId();
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(pass));
        u.setFullName(name);
        u.setPhone(phone);
        u.setRole(role);
        users.save(u);
        System.out.println("Seeded demo user: " + email + " (" + role + ")");
        return u.getId();
    }

    private void seedVehicles(Long userId) {
        try {
            Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM vehicles WHERE user_id = ?", Integer.class, userId);
            if (n != null && n > 0) return;
            jdbc.update("INSERT INTO vehicles (user_id, plate_number, type, make, model, year) VALUES (?,?,?,?,?,?)",
                    userId, "DEMO-01", "SEDAN", "Toyota", "Camry", 2020);
            jdbc.update("INSERT INTO vehicles (user_id, plate_number, type, make, model, year) VALUES (?,?,?,?,?,?)",
                    userId, "DEMO-02", "SUV", "Honda", "CR-V", 2022);
            System.out.println("[DemoUserSeeder] Seeded 2 demo vehicles");
        } catch (Exception e) {
            System.out.println("[DemoUserSeeder] vehicles seed skipped: " + e.getMessage());
        }
    }

    private void seedBookings(Long userId) {
        try {
            Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM bookings WHERE user_id = ?", Integer.class, userId);
            if (n != null && n > 0) return;
            jdbc.update("INSERT INTO bookings (user_id, status) VALUES (?,?)", userId, "COMPLETED");
            jdbc.update("INSERT INTO bookings (user_id, status) VALUES (?,?)", userId, "PENDING");
            jdbc.update("INSERT INTO bookings (user_id, status) VALUES (?,?)", userId, "CANCELLED");
            System.out.println("[DemoUserSeeder] Seeded 3 demo bookings");
        } catch (Exception e) {
            System.out.println("[DemoUserSeeder] bookings seed skipped: " + e.getMessage());
        }
    }

    private void seedPayments(Long userId) {
        try {
            Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM payments WHERE user_id = ?", Integer.class, userId);
            if (n != null && n > 0) return;
            jdbc.update("INSERT INTO payments (user_id, amount, status) VALUES (?,?,?)", userId, 499.00, "SUCCESS");
            jdbc.update("INSERT INTO payments (user_id, amount, status) VALUES (?,?,?)", userId, 899.00, "SUCCESS");
            System.out.println("[DemoUserSeeder] Seeded 2 demo payments");
        } catch (Exception e) {
            System.out.println("[DemoUserSeeder] payments seed skipped: " + e.getMessage());
        }
    }
}