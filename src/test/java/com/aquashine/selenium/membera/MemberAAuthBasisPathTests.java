package com.aquashine.selenium.membera;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basis Path — Member A.
 * One main-flow test through each of the 5 modules.
 */
@Tag("Selenium") @Tag("MemberA") @Tag("BasisPath")
@DisplayName("Member A - Auth Basis Path Tests")
class MemberAAuthBasisPathTests extends BaseTest {

    /** M1 register: fill valid form -> dashboard. */
    @Test
    @DisplayName("BP-01 (register): valid registration reaches dashboard")
    void bpRegister() {
        navigate("/auth/register.html");
        type("email", "bp" + System.nanoTime() + "@test.com");
        type("fullName", "BP User");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    /** M2 customerLogin: valid credentials -> dashboard. */
    @Test
    @DisplayName("BP-02 (customerLogin): valid credentials reach dashboard")
    void bpCustomerLogin() {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='USER']")).click();
        type("email", "abcd@gmail.com");
        type("password", "123456789");
        click("loginBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard.html"));
    }

    /** M3 adminLogin: valid credentials -> /admin/. */
    @Test
    @DisplayName("BP-03 (adminLogin): valid credentials reach admin portal")
    void bpAdminLogin() {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='ADMIN']")).click();
        type("email", "abc@gmail.com");
        type("password", "12345678");
        click("loginBtn");
        wait.until(d -> d.getCurrentUrl().contains("/admin/"));
    }

    /** M4 changePassword: page loads for logged-in customer. */
    @Test
    @DisplayName("BP-04 (changePassword): page loads for logged-in customer")
    void bpChangePwd() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/change-password.html");
        assertThat(driver.getCurrentUrl()).contains("change-password");
    }

    /** M5 editProfile: page loads for logged-in customer. */
    @Test
    @DisplayName("BP-05 (editProfile): profile page loads for logged-in customer")
    void bpProfile() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/profile.html");
        assertThat(driver.getCurrentUrl()).contains("profile");
    }
}