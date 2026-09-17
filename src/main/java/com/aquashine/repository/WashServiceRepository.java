package com.aquashine.repository;

import com.aquashine.model.WashService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WashServiceRepository extends JpaRepository<WashService, Long> {
    Optional<WashService> findByCode(String code);
    List<WashService> findByActiveTrue();
    List<WashService> findByCategoryAndActiveTrue(String category);
}