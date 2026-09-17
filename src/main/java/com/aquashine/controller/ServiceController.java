package com.aquashine.controller;

import com.aquashine.model.WashService;
import com.aquashine.service.ServiceCatalogueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceCatalogueService service;

    public ServiceController(ServiceCatalogueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WashService>> list() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<WashService>> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.listByCategory(category));
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<?> priceFor(@PathVariable Long id, @RequestParam String vehicleType) {
        try {
            int price = service.calculatePrice(id, vehicleType);
            return ResponseEntity.ok(Map.of("serviceId", id, "vehicleType", vehicleType, "price", price));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}