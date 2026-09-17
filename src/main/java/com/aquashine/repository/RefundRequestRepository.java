package com.aquashine.repository;

import com.aquashine.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<RefundRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
}