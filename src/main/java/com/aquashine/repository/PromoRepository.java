package com.aquashine.repository;

import com.aquashine.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromoRepository extends JpaRepository<Promo, Long> {
    Optional<Promo> findByCode(String code);
    Optional<Promo> findByCodeAndActiveTrue(String code);
}