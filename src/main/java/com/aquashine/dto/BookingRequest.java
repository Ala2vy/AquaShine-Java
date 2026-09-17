package com.aquashine.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingRequest {

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long serviceId;

    @NotNull
    private LocalDate slotDate;

    @NotNull
    private LocalTime slotTime;

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }

    public LocalTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalTime slotTime) { this.slotTime = slotTime; }
}