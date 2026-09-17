package com.aquashine.controller;

import com.aquashine.dto.VehicleRequest;
import com.aquashine.model.Vehicle;
import com.aquashine.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return id == null ? null : ((Number) id).longValue();
    }

    @GetMapping
    public ResponseEntity<?> list(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        List<Vehicle> vehicles = vehicleService.listVehicles(userId);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody VehicleRequest req, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            Vehicle v = vehicleService.addVehicle(
                userId, req.getPlateNumber(), req.getType(),
                req.getMake(), req.getModel(), req.getYear());
            return ResponseEntity.ok(Map.of("message", "Vehicle added", "id", v.getId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @Valid @RequestBody VehicleRequest req,
                                  HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            Optional<Vehicle> updated = vehicleService.editVehicle(
                userId, id, req.getType(), req.getMake(), req.getModel(), req.getYear());
            if (updated.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Vehicle not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Vehicle updated"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        try {
            boolean ok = vehicleService.deleteVehicle(userId, id);
            if (!ok) return ResponseEntity.status(404).body(Map.of("error", "Vehicle not found"));
            return ResponseEntity.ok(Map.of("message", "Vehicle deleted"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}