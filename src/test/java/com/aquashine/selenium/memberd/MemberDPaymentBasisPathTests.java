package com.aquashine.selenium.memberd;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/** Basis Path — Member D: 1 main-flow test per module. */
@Tag("Selenium") @Tag("MemberD") @Tag("BasisPath")
@DisplayName("Member D - Payment Basis Path Tests")
class MemberDPaymentBasisPathTests extends BaseTest {

    private static final String BID = "2";

    @Test @DisplayName("BP-01 (payment): payment page loads with bookingId")
    void bpPayment() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/payment.html?bookingId=" + BID);
        assertThat(driver.getCurrentUrl()).contains("payment");
    }

    @Test @DisplayName("BP-02 (promo): promo field present on payment page")
    void bpPromo() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/payment.html?bookingId=" + BID);
        assertThat(driver.findElements(By.id("promoCode"))).isNotEmpty();
    }

    @Test @DisplayName("BP-03 (adminDashboard): dashboard loads with statUsers")
    void bpDash() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/index.html");
        wait.until(d -> d.findElement(By.id("statUsers")).isDisplayed());
    }

    @Test @DisplayName("BP-04 (adminBookings): admin bookings page loads")
    void bpBookings() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/bookings.html");
        assertThat(driver.getCurrentUrl()).contains("bookings.html");
    }

    @Test @DisplayName("BP-05 (adminPayments): admin payments page loads")
    void bpPayments() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        assertThat(driver.getCurrentUrl()).contains("payments.html");
    }
}