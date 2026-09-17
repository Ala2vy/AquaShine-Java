package com.aquashine.service;

import com.aquashine.model.WashService;
import com.aquashine.repository.WashServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ServiceCatalogueService {

    public static final int PRICE_MIN = 100;
    public static final int PRICE_MAX = 10000;
    public static final Set<String> VALID_CATEGORIES =
        Set.of("BASIC", "PREMIUM", "DELUXE");

    private final WashServiceRepository repo;

    public ServiceCatalogueService(WashServiceRepository repo) {
        this.repo = repo;
    }

    public WashService createService(String code, String name, String category, Integer basePrice) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Service code required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Service name required");
        }
        if (category == null || !VALID_CATEGORIES.contains(category.toUpperCase())) {
            throw new IllegalArgumentException("Invalid category");
        }
        if (basePrice == null || basePrice < PRICE_MIN || basePrice > PRICE_MAX) {
            throw new IllegalArgumentException(
                "Price must be between " + PRICE_MIN + " and " + PRICE_MAX);
        }
        if (repo.findByCode(code.toUpperCase()).isPresent()) {
            throw new IllegalArgumentException("Service code already exists");
        }

        WashService svc = new WashService(
            code.toUpperCase(),
            name.trim(),
            category.toUpperCase(),
            basePrice
        );
        return repo.save(svc);
    }

    public List<WashService> listActive() {
        return repo.findByActiveTrue();
    }

    public List<WashService> listByCategory(String category) {
        return repo.findByCategoryAndActiveTrue(category.toUpperCase());
    }

    public Optional<WashService> findById(Long id) {
        return repo.findById(id);
    }

    /**
     * Final price for a service given vehicle type.
     * Decision Table:
     *   HATCHBACK  -> basePrice * 1.0
     *   SEDAN      -> basePrice * 1.5
     *   SUV        -> basePrice * 2.0
     */
    public int calculatePrice(Long serviceId, String vehicleType) {
        Optional<WashService> found = repo.findById(serviceId);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Service not found");
        }
        int base = found.get().getBasePrice();
        return switch (vehicleType.toUpperCase()) {
            case "HATCHBACK" -> base;
            case "SEDAN"     -> (int)(base * 1.5);
            case "SUV"       -> base * 2;
            default -> throw new IllegalArgumentException("Invalid vehicle type");
        };
    }
}