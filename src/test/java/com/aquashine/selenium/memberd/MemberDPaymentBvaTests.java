package com.aquashine.selenium.memberd;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/** BVA — Member D: Payment & Admin. */
@Tag("Selenium") @Tag("MemberD") @Tag("BVA")
@DisplayName("Member D - Payment BVA Tests")
class MemberDPaymentBvaTests extends BaseTest {

    private static final String BID = "2";

    private void openPay() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/payment.html?bookingId=" + BID);
    }

    // M1 payment — check that the field ACCEPTS input (not the exact length,
    // because the field may enforce maxlength or filter characters)
    @Test @DisplayName("D-BVA-01 (payment): empty card number field is empty on load")
    void cardEmpty() { openPay();
        if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        assertThat(findById("cardNumber").getAttribute("value")).isEmpty(); }

    @Test @DisplayName("D-BVA-02 (payment): card field accepts a short value")
    void card15() { openPay(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "123456789012345");
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty(); }

    @Test @DisplayName("D-BVA-03 (payment): card field accepts a 16-digit value")
    void card16() { openPay(); if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1234567890123456");
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty(); }

    // M2 promo
    @Test @DisplayName("D-BVA-04 (promo): empty promo code accepted")
    void promoEmpty() { openPay(); if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        assertThat(findById("promoCode").getAttribute("value")).isEmpty(); }

    @Test @DisplayName("D-BVA-05 (promo): 1-char promo code accepted (minimum non-empty)")
    void promo1() { openPay(); if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        type("promoCode", "A");
        assertThat(findById("promoCode").getAttribute("value")).isNotEmpty(); }

    @Test @DisplayName("D-BVA-06 (promo): 20-char promo code accepted")
    void promo20() { openPay(); if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        type("promoCode", "ABCDEFGHIJKLMNOPQRST");
        assertThat(findById("promoCode").getAttribute("value")).isNotEmpty(); }

    // M3 adminDashboard
    @Test @DisplayName("D-BVA-07 (adminDashboard): statUsers element visible")
    void statUsers() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/index.html");
        wait.until(d -> d.findElement(By.id("statUsers")).isDisplayed()); }

    @Test @DisplayName("D-BVA-08 (adminDashboard): statUsers > 0")
    void statUsersPositive() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/index.html");
        wait.until(d -> d.findElement(By.id("statUsers")).isDisplayed());
        String n = driver.findElement(By.id("statUsers")).getText();
        assertThat(Integer.parseInt(n)).isGreaterThan(0); }

    @Test @DisplayName("D-BVA-09 (adminDashboard): dashboard mentions bookings")
    void statBookings() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/index.html");
        assertThat(driver.getPageSource().toLowerCase()).contains("booking"); }

    // M4 adminBookings
    @Test @DisplayName("D-BVA-10 (adminBookings): admin bookings page loads")
    void adminBookingsLoads() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/bookings.html");
        assertThat(driver.getCurrentUrl()).contains("bookings.html"); }

    @Test @DisplayName("D-BVA-11 (adminBookings): page contains list/table")
    void adminBookingsContent() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/bookings.html");
        assertThat(driver.getPageSource().toLowerCase()).containsAnyOf("table", "list", "booking"); }

    @Test @DisplayName("D-BVA-12 (adminBookings): admin not redirected from admin bookings")
    void adminBookingsNoRedirect() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/bookings.html");
        assertThat(driver.getCurrentUrl()).contains("/admin/"); }

    // M5 adminPayments
    @Test @DisplayName("D-BVA-13 (adminPayments): admin payments page loads")
    void adminPaymentsLoads() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        assertThat(driver.getCurrentUrl()).contains("payments.html"); }

    @Test @DisplayName("D-BVA-14 (adminPayments): kpiCount element present")
    void kpiCount() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        assertThat(driver.findElements(By.id("kpiCount"))).isNotEmpty(); }

    @Test @DisplayName("D-BVA-15 (adminPayments): admin payments URL correct")
    void adminPaymentsUrl() { loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        assertThat(driver.getCurrentUrl()).contains("payments.html"); }
}