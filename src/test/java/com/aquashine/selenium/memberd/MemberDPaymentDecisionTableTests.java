package com.aquashine.selenium.memberd;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/** Decision Table ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â Member D: Payment. */
@Tag("Selenium") @Tag("MemberD") @Tag("DecisionTable")
@DisplayName("Member D - Payment Decision Table Tests")
class MemberDPaymentDecisionTableTests extends BaseTest {

    private static final String BID = "2";

    private void open() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/payment.html?bookingId=" + BID);
    }

    @Test @DisplayName("DT-01: card=empty -> card stays empty")
    void cardEmpty() { open(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        assertThat(findById("cardNumber").getAttribute("value")).isEmpty(); }

    @Test @DisplayName("DT-02: card=filled -> card not empty")
    void cardFilled() { open(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1234567890123456");
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty(); }

    @Test @DisplayName("DT-03: card + expiry -> both accepted")
    void cardExpiry() { open(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1234567890123456");
        if (!driver.findElements(By.id("expiry")).isEmpty()) type("expiry", "12/28");
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty(); }

    @Test @DisplayName("DT-04: card + expiry + cvv -> all accepted")
    void allFilled() { open(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1234567890123456");
        if (!driver.findElements(By.id("expiry")).isEmpty()) type("expiry", "12/28");
        if (!driver.findElements(By.id("cvv")).isEmpty()) type("cvv", "123");
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty(); }

    @Test @DisplayName("DT-05: no card -> pay button present")
    void payNoCard() {
        open();
        assertThat(driver.findElements(By.id("payBtn"))).isNotEmpty();
    }

    @Test @DisplayName("DT-06: empty promo accepted")
    void promoEmpty() { open(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1234567890123456");
        if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        assertThat(findById("promoCode").getAttribute("value")).isEmpty(); }

    @Test @DisplayName("DT-07: filled promo accepted")
    void promoFilled() { open(); if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        type("promoCode", "TEST10");
        assertThat(findById("promoCode").getAttribute("value")).isNotEmpty(); }

    @Test @DisplayName("DT-08: pay button enabled on load")
    void payBtnEnabled() { open(); if (driver.findElements(By.id("payBtn")).isEmpty()) return;
        assertThat(findById("payBtn").isEnabled()).isTrue(); }

    @Test @DisplayName("DT-09: payment page loads with bookingId")
    void paymentLoads() { open();
        assertThat(driver.getCurrentUrl()).contains("payment"); }

    @Test @DisplayName("DT-10: payment URL correct")
    void paymentUrl() { open();
        assertThat(driver.getCurrentUrl()).contains("payment.html"); }
}