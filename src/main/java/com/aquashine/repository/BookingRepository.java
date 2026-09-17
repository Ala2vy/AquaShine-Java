package com.aquashine.repository;

import com.aquashine.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderBySlotDateDescSlotTimeDesc(Long userId);
    List<Booking> findByUserIdAndStatus(Long userId, String status);
    boolean existsBySlotDateAndSlotTimeAndStatus(LocalDate date, LocalTime time, String status);
}