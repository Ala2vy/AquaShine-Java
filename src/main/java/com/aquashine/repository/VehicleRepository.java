package com.aquashine.repository;

import com.aquashine.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUserId(Long userId);
    long countByUserId(Long userId);
    boolean existsByUserIdAndPlateNumber(Long userId, String plateNumber);
}