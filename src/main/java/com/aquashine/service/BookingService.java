package com.aquashine.service;

import com.aquashine.model.Booking;
import com.aquashine.repository.BookingRepository;
import com.aquashine.repository.UserRepository;
import com.aquashine.repository.VehicleRepository;
import com.aquashine.repository.WashServiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final WashServiceRepository serviceRepository;

    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          WashServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
    }

    public Booking createBooking(Long userId, Long vehicleId, Long serviceId,
                                 LocalDate slotDate, LocalTime slotTime) {
        // Validate vehicle
        var vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        if (!vehicle.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Vehicle does not belong to you");
        }

        // Validate service
        var service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (!service.getActive()) {
            throw new IllegalArgumentException("Service is not available");
        }

        // Validate date
        if (slotDate == null || slotDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date must be today or later");
        }
        if (slotDate.isAfter(LocalDate.now().plusDays(30))) {
            throw new IllegalArgumentException("Cannot book more than 30 days ahead");
        }

        // Validate time slot (business hours 8 AM - 8 PM)
        if (slotTime == null || slotTime.getHour() < 8 || slotTime.getHour() >= 20) {
            throw new IllegalArgumentException("Slot must be between 8:00 and 20:00");
        }

        // Validate time slot minute granularity (30-min slots)
        if (slotTime.getMinute() != 0 && slotTime.getMinute() != 30) {
            throw new IllegalArgumentException("Slots are every 30 minutes");
        }

        // Check slot availability
        if (bookingRepository.existsBySlotDateAndSlotTimeAndStatus(slotDate, slotTime, "CONFIRMED")) {
            throw new IllegalArgumentException("Slot already taken");
        }

        // Calculate price (base * vehicle multiplier)
        int base = service.getBasePrice();
        int multiplier = switch (vehicle.getType().toUpperCase()) {
            case "HATCHBACK" -> 1;
            case "SEDAN"     -> 2; // 1.5x rounded up
            case "SUV"       -> 2;
            default -> 1;
        };
        int price = (vehicle.getType().equalsIgnoreCase("SEDAN"))
            ? (int)(base * 1.5)
            : base * multiplier;

        Booking booking = new Booking(userId, vehicleId, serviceId, slotDate, slotTime, price);
        return bookingRepository.save(booking);
    }

    public List<Booking> listUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderBySlotDateDescSlotTimeDesc(userId);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }
}