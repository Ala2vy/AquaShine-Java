package com.aquashine.service;

import com.aquashine.model.Vehicle;
import com.aquashine.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VehicleService {

    public static final int MAX_VEHICLES_PER_USER = 5;
    public static final int PLATE_MIN_LENGTH = 3;
    public static final int PLATE_MAX_LENGTH = 15;
    public static final int MIN_YEAR = 1900;
    public static final int MAX_YEAR = Year.now().getValue() + 1;

    public static final Set<String> VALID_TYPES =
        Set.of("HATCHBACK", "SEDAN", "SUV");

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle addVehicle(Long userId, String plateNumber, String type,
                              String make, String model, Integer year) {
        // Validation
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new IllegalArgumentException("Plate number required");
        }
        String plate = plateNumber.trim().toUpperCase();
        if (plate.length() < PLATE_MIN_LENGTH || plate.length() > PLATE_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Plate must be " + PLATE_MIN_LENGTH + "-" + PLATE_MAX_LENGTH + " characters");
        }

        if (type == null || !VALID_TYPES.contains(type.toUpperCase())) {
            throw new IllegalArgumentException("Invalid vehicle type");
        }

        if (year == null || year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException(
                "Year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }

        // Max vehicles per user
        long count = vehicleRepository.countByUserId(userId);
        if (count >= MAX_VEHICLES_PER_USER) {
            throw new IllegalArgumentException(
                "Cannot add more than " + MAX_VEHICLES_PER_USER + " vehicles");
        }

        // Duplicate plate
        if (vehicleRepository.existsByUserIdAndPlateNumber(userId, plate)) {
            throw new IllegalArgumentException("Vehicle with this plate already exists");
        }

        Vehicle vehicle = new Vehicle(
            userId, plate, type.toUpperCase(),
            make == null ? "" : make.trim(),
            model == null ? "" : model.trim(),
            year
        );
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> editVehicle(Long userId, Long vehicleId,
                                          String type, String make, String model, Integer year) {
        if (type == null || !VALID_TYPES.contains(type.toUpperCase())) {
            throw new IllegalArgumentException("Invalid vehicle type");
        }
        if (year == null || year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException(
                "Year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }

        Optional<Vehicle> found = vehicleRepository.findById(vehicleId);
        if (found.isEmpty()) return Optional.empty();

        Vehicle vehicle = found.get();
        if (!vehicle.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to edit this vehicle");
        }

        vehicle.setType(type.toUpperCase());
        vehicle.setMake(make == null ? "" : make.trim());
        vehicle.setModel(model == null ? "" : model.trim());
        vehicle.setYear(year);
        return Optional.of(vehicleRepository.save(vehicle));
    }

    public boolean deleteVehicle(Long userId, Long vehicleId) {
        Optional<Vehicle> found = vehicleRepository.findById(vehicleId);
        if (found.isEmpty()) return false;

        Vehicle vehicle = found.get();
        if (!vehicle.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this vehicle");
        }

        vehicleRepository.delete(vehicle);
        return true;
    }

    public List<Vehicle> listVehicles(Long userId) {
        return vehicleRepository.findByUserId(userId);
    }
}