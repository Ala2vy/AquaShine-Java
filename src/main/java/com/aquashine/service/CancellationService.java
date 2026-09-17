package com.aquashine.service;

import com.aquashine.model.Booking;
import com.aquashine.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CancellationService {

    private final BookingRepository bookingRepository;

    public CancellationService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Calculate refund percentage based on hours before the slot.
     *   >= 24 hours : 100%
     *   12-23 hours : 75%
     *   4-11 hours  : 50%
     *   1-3 hours   : 25%
     *   < 1 hour    : 0%
     */
    public int calculateRefundPercent(LocalDateTime slotDateTime, LocalDateTime now) {
        if (slotDateTime.isBefore(now)) {
            throw new IllegalArgumentException("Cannot cancel past bookings");
        }
        long hoursUntil = Duration.between(now, slotDateTime).toHours();

        if (hoursUntil >= 24) return 100;
        if (hoursUntil >= 12) return 75;
        if (hoursUntil >= 4)  return 50;
        if (hoursUntil >= 1)  return 25;
        return 0;
    }

    public Booking cancel(Long userId, Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to cancel this booking");
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is not active");
        }

        LocalDateTime slotDateTime = LocalDateTime.of(booking.getSlotDate(), booking.getSlotTime());
        LocalDateTime now = LocalDateTime.now();

        if (slotDateTime.isBefore(now)) {
            throw new IllegalArgumentException("Cannot cancel past bookings");
        }

        booking.setStatus("CANCELLED");
        booking.setCancelledReason(reason == null ? "" : reason.trim());
        // Note: cancelled_at is @Column with automatic timestamp on save (or we set explicitly)
        return bookingRepository.save(booking);
    }

    public Booking reschedule(Long userId, Long bookingId, java.time.LocalDate newDate, java.time.LocalTime newTime) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to reschedule this booking");
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only active bookings can be rescheduled");
        }

        if (newDate == null || newDate.isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("New date must be today or later");
        }

        if (newTime == null || newTime.getHour() < 8 || newTime.getHour() >= 20) {
            throw new IllegalArgumentException("Slot must be between 8:00 and 20:00");
        }

        booking.setSlotDate(newDate);
        booking.setSlotTime(newTime);
        return bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }
}