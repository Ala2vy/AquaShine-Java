package com.aquashine.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wash_services")
public class WashService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(name = "base_price", nullable = false)
    private Integer basePrice;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WashService() {}

    public WashService(String code, String name, String category, Integer basePrice) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getBasePrice() { return basePrice; }
    public void setBasePrice(Integer basePrice) { this.basePrice = basePrice; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}