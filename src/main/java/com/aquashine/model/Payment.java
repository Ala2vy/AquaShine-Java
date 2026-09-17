package com.aquashine.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "original_amount", nullable = false)
    private Integer originalAmount;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount = 0;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Column(name = "promo_code", length = 20)
    private String promoCode;

    @Column(nullable = false, length = 20)
    private String status = "SUCCESS";

    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {}

    public Payment(Long userId, Long bookingId, Integer originalAmount,
                   Integer discountAmount, Integer finalAmount, String promoCode) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.promoCode = promoCode;
        this.status = "SUCCESS";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(Integer originalAmount) { this.originalAmount = originalAmount; }
    public Integer getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Integer discountAmount) { this.discountAmount = discountAmount; }
    public Integer getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Integer finalAmount) { this.finalAmount = finalAmount; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}