package com.aquashine.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "slot_time", nullable = false)
    private LocalTime slotTime;

    @Column(nullable = false, length = 20)
    private String status = "CONFIRMED";

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Booking() {}

    public Booking(Long userId, Long vehicleId, Long serviceId,
                   LocalDate slotDate, LocalTime slotTime, Integer totalPrice) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.serviceId = serviceId;
        this.slotDate = slotDate;
        this.slotTime = slotTime;
        this.totalPrice = totalPrice;
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }

    public LocalTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalTime slotTime) { this.slotTime = slotTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_reason", length = 200)
    private String cancelledReason;

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelledReason() { return cancelledReason; }
    public void setCancelledReason(String cancelledReason) { this.cancelledReason = cancelledReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}