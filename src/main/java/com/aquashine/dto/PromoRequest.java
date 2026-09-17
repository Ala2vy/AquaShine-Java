package com.aquashine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PromoRequest {

    @NotBlank
    private String code;

    @Min(0)
    private Integer amount;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}