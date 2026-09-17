package com.aquashine.selenium.memberd;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("Selenium")
@Tag("MemberD")
@DisplayName("Member D - Payment & Admin Selenium Tests")
class MemberDPaymentSeleniumTests extends BaseTest {

    @Test
    @DisplayName("D-SE-01: Payment page loads with card form")
    void paymentPageLoads() {
        loginCustomer("abcd@gmail.com", "Pass@123");
        // Use a known booking ID — must exist in your DB
        navigate("/auth/payment.html?bookingId=8");

        wait.until(d -> d.findElement(By.id("paymentForm")).isDisplayed());
        assertThat(findById("cardNumber").isDisplayed()).isTrue();
        assertThat(findById("promoCode").isDisplayed()).isTrue();
        assertThat(findById("payBtn").isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("D-SE-02: Admin dashboard shows KPIs")
    void adminDashboardKPIs() {
        loginAdmin("abc@gmail.com", "Pass@123");
        navigate("/admin/index.html");

        wait.until(d -> d.findElement(By.id("statUsers")).isDisplayed());
        String users = driver.findElement(By.id("statUsers")).getText();
        assertThat(users).isNotBlank();
        assertThat(Integer.parseInt(users)).isGreaterThan(0);
    }

    @Test
    @DisplayName("D-SE-03: Admin payments page shows transactions")
    void adminPaymentsList() {
        loginAdmin("abc@gmail.com", "Pass@123");
        navigate("/admin/payments.html");

        wait.until(d -> {
            String txt = d.findElement(By.id("kpiCount")).getText();
            return !txt.isBlank() && !txt.equals("0");
        });

        String count = driver.findElement(By.id("kpiCount")).getText();
        assertThat(Integer.parseInt(count)).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("D-SE-04: Customer cannot access admin panel")
    void customerBlockedFromAdmin() {
        loginCustomer("abcd@gmail.com", "Pass@123");
        navigate("/admin/index.html");

        // requireAdmin() should redirect away
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/index.html");
    }
}