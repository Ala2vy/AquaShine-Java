package com.aquashine.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "plate_number", nullable = false, length = 15)
    private String plateNumber;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 30)
    private String make;

    @Column(length = 30)
    private String model;

    @Column(name = "year_made", nullable = false)
    private Integer year;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Vehicle() {}

    public Vehicle(Long userId, String plateNumber, String type,
                   String make, String model, Integer year) {
        this.userId = userId;
        this.plateNumber = plateNumber;
        this.type = type;
        this.make = make;
        this.model = model;
        this.year = year;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}