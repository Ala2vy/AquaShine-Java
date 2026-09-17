package com.aquashine.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull
    private Long bookingId;

    private String promoCode;

    private String cardNumber;

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
}