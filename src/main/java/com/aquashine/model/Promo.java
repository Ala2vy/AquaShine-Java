package com.aquashine.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promos")
public class Promo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "min_amount", nullable = false)
    private Integer minAmount;

    @Column(name = "max_uses", nullable = false)
    private Integer maxUses;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean active = true;

    public Promo() {}

    public Promo(String code, Integer discountPercent, Integer minAmount,
                 Integer maxUses, LocalDateTime expiryDate) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.minAmount = minAmount;
        this.maxUses = maxUses;
        this.expiryDate = expiryDate;
        this.usedCount = 0;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public Integer getMinAmount() { return minAmount; }
    public void setMinAmount(Integer minAmount) { this.minAmount = minAmount; }

    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}