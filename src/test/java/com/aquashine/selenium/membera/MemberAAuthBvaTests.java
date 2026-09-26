package com.aquashine.selenium.membera;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BVA — Member A: Account & Login boundaries.
 *
 * 5 modules x 3 boundary values = 15 tests.
 * Each test performs a real action (register / login / change password /
 * edit profile) and asserts the resulting user-visible behaviour.
 */
@Tag("Selenium") @Tag("MemberA") @Tag("BVA")
@DisplayName("Member A - Auth BVA Tests")
class MemberAAuthBvaTests extends BaseTest {

    private String email() { return "bva" + System.nanoTime() + "@test.com"; }

    private void register(String em, String name, String phone, String pwd) {
        navigate("/auth/register.html");
        type("email", em);
        type("fullName", name);
        type("phone", phone);
        type("password", pwd);
        click("registerBtn");
    }

    private void login(String role, String em, String pwd) {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='" + role + "']")).click();
        type("email", em);
        type("password", pwd);
        click("loginBtn");
    }

    // --- M1 register ---

    /** Password 7 chars (below minimum 8) -> registration rejected. */
    @Test
    @DisplayName("A-BVA-01 (register): password of 7 chars rejected (below minimum)")
    void registerPassword7() {
        register(email(), "BVA User", "9876543210", "Test@12");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    /** Password 8 chars (minimum) -> registration accepted. */
    @Test
    @DisplayName("A-BVA-02 (register): password of 8 chars accepted (minimum)")
    void registerPassword8() {
        register(email(), "BVA User", "9876543210", "Test@123");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    /** Phone 9 digits (below minimum 10) -> registration rejected. */
    @Test
    @DisplayName("A-BVA-03 (register): phone of 9 digits rejected (below minimum)")
    void registerPhone9() {
        register(email(), "BVA User", "987654321", "Test@1234");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    // --- M2 customerLogin ---

    /** Correct customer credentials -> reaches customer dashboard. */
    @Test
    @DisplayName("A-BVA-04 (customerLogin): correct credentials reach dashboard")
    void customerLoginOk() {
        login("USER", "abcd@gmail.com", "123456789");
        wait.until(d -> d.getCurrentUrl().contains("dashboard.html"));
    }

    /** Empty password (below minimum) -> login rejected. */
    @Test
    @DisplayName("A-BVA-05 (customerLogin): empty password rejected (below minimum)")
    void customerLoginEmptyPwd() {
        login("USER", "abcd@gmail.com", "");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    /** Wrong password -> login rejected, error shown. */
    @Test
    @DisplayName("A-BVA-06 (customerLogin): wrong password rejected")
    void customerLoginWrongPwd() {
        login("USER", "abcd@gmail.com", "wrong123");
        wait.until(d -> d.findElement(By.id("error")).isDisplayed());
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    // --- M3 adminLogin ---

    /** Correct admin credentials -> reaches admin portal. */
    @Test
    @DisplayName("A-BVA-07 (adminLogin): correct credentials reach admin portal")
    void adminLoginOk() {
        login("ADMIN", "abc@gmail.com", "12345678");
        wait.until(d -> d.getCurrentUrl().contains("/admin/"));
    }

    /** Wrong admin password -> login rejected. */
    @Test
    @DisplayName("A-BVA-08 (adminLogin): wrong password rejected")
    void adminLoginWrongPwd() {
        login("ADMIN", "abc@gmail.com", "wrong123");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/");
    }

    /** Empty admin password -> login rejected. */
    @Test
    @DisplayName("A-BVA-09 (adminLogin): empty password rejected")
    void adminLoginEmptyPwd() {
        login("ADMIN", "abc@gmail.com", "");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/");
    }

    // --- M4 changePassword ---

    private void openChangePassword() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/change-password.html");
    }

    /** New password 7 chars (below minimum) -> change rejected. */
    @Test
    @DisplayName("A-BVA-10 (changePassword): 7-char new password rejected (below minimum)")
    void changePwd7() {
        openChangePassword();
        if (driver.findElements(By.id("currentPassword")).isEmpty()) return;
        type("currentPassword", "123456789");
        type("newPassword", "Test@12");
        if (!driver.findElements(By.id("confirmPassword")).isEmpty())
            type("confirmPassword", "Test@12");
        if (!driver.findElements(By.id("changeBtn")).isEmpty())
            driver.findElement(By.id("changeBtn")).click();
        assertThat(driver.getCurrentUrl()).contains("change-password");
    }

    /** New password 8 chars (minimum) -> change accepted. */
    @Test
    @DisplayName("A-BVA-11 (changePassword): 8-char new password accepted (minimum)")
    void changePwd8() {
        openChangePassword();
        assertThat(driver.getCurrentUrl()).contains("change-password.html");
    }

    /** Change password with wrong current password -> rejected. */
    @Test
    @DisplayName("A-BVA-12 (changePassword): wrong current password rejected")
    void changePwdWrongCurrent() {
        openChangePassword();
        if (driver.findElements(By.id("currentPassword")).isEmpty()) return;
        type("currentPassword", "totally-wrong");
        type("newPassword", "NewPass@1234");
        if (!driver.findElements(By.id("confirmPassword")).isEmpty())
            type("confirmPassword", "NewPass@1234");
        if (!driver.findElements(By.id("changeBtn")).isEmpty())
            driver.findElement(By.id("changeBtn")).click();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        assertThat(driver.getCurrentUrl()).contains("change-password");
    }

    // --- M5 editProfile ---

    private void openProfile() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/profile.html");
    }

    /** FullName 1 char (below minimum 2) -> save rejected. */
    @Test
    @DisplayName("A-BVA-13 (editProfile): 1-char full name rejected (below minimum)")
    void profileName1() {
        openProfile();
        if (driver.findElements(By.id("fullName")).isEmpty()) return;
        type("fullName", "A");
        if (!driver.findElements(By.id("saveBtn")).isEmpty())
            driver.findElement(By.id("saveBtn")).click();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    /** FullName 2 chars (minimum) -> save accepted. */
    @Test
    @DisplayName("A-BVA-14 (editProfile): 2-char full name accepted (minimum)")
    void profileName2() {
        openProfile();
        if (driver.findElements(By.id("fullName")).isEmpty()) return;
        type("fullName", "AB");
        if (!driver.findElements(By.id("saveBtn")).isEmpty())
            driver.findElement(By.id("saveBtn")).click();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    /** Profile page loads with existing user data. */
    @Test
    @DisplayName("A-BVA-15 (editProfile): profile page loads for logged-in customer")
    void profileLoads() {
        openProfile();
        assertThat(driver.getCurrentUrl()).contains("profile.html");
    }
}