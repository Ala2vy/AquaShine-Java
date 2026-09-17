package com.aquashine.dto;

import jakarta.validation.constraints.*;

public class EditProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10,13}$", message = "Phone must be 10 to 13 digits")
    private String phone;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}