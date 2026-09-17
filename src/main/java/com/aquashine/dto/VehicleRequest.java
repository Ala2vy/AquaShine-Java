package com.aquashine.dto;

import jakarta.validation.constraints.*;

public class VehicleRequest {

    @NotBlank(message = "Plate number is required")
    @Size(min = 3, max = 15, message = "Plate must be 3-15 characters")
    private String plateNumber;

    @NotBlank(message = "Vehicle type is required")
    private String type;

    @Size(max = 30)
    private String make;

    @Size(max = 30)
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

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
}