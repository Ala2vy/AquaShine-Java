package com.aquashine.service;

import com.aquashine.model.Promo;
import com.aquashine.repository.PromoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PromoService {

    public static final int CODE_MIN_LENGTH = 4;
    public static final int CODE_MAX_LENGTH = 20;

    private final PromoRepository promoRepository;

    public PromoService(PromoRepository promoRepository) {
        this.promoRepository = promoRepository;
    }

    /**
     * Validate a promo code and return the discount for the given amount.
     * Throws IllegalArgumentException if the code is invalid for any reason.
     */
    public int calculateDiscount(String code, int amount) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Promo code required");
        }
        String normalized = code.trim().toUpperCase();

        if (normalized.length() < CODE_MIN_LENGTH || normalized.length() > CODE_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Promo code must be " + CODE_MIN_LENGTH + "-" + CODE_MAX_LENGTH + " characters");
        }

        Promo promo = promoRepository.findByCode(normalized)
            .orElseThrow(() -> new IllegalArgumentException("Promo code not found"));

        if (!promo.getActive()) {
            throw new IllegalArgumentException("Promo code is inactive");
        }
        if (promo.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Promo code expired");
        }
        if (promo.getUsedCount() >= promo.getMaxUses()) {
            throw new IllegalArgumentException("Promo code usage limit reached");
        }
        if (amount < promo.getMinAmount()) {
            throw new IllegalArgumentException(
                "Minimum amount for this promo is ₹" + promo.getMinAmount());
        }

        return amount * promo.getDiscountPercent() / 100;
    }

    public void incrementUsage(String code) {
        Optional<Promo> found = promoRepository.findByCode(code.toUpperCase());
        if (found.isPresent()) {
            Promo p = found.get();
            p.setUsedCount(p.getUsedCount() + 1);
            promoRepository.save(p);
        }
    }
}