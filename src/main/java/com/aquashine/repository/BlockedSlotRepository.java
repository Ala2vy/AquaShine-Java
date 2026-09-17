package com.aquashine.repository;

import com.aquashine.model.BlockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {
    List<BlockedSlot> findBySlotDate(LocalDate slotDate);
    List<BlockedSlot> findBySlotDateGreaterThanEqualOrderBySlotDateAscSlotTimeAsc(LocalDate date);
    Optional<BlockedSlot> findBySlotDateAndSlotTime(LocalDate date, java.time.LocalTime time);
    boolean existsBySlotDateAndSlotTime(LocalDate date, java.time.LocalTime time);
}