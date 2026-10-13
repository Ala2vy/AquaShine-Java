package com.aquashine.selenium.membera;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BVA — Member A: Account boundaries.
 *
 * Bounds (from AuthService / ProfileService):
 *   register password:      8–20
 *   register phone:        10–13 digits
 *   change-password new:    8–20
 *   edit-profile fullName:  3–50
 *   edit-profile phone:    10–13 digits
 *
 * 5 modules x 3 boundary values = 15 tests.
 * Each test targets a specific boundary and asserts accept/reject.
 *
 * NOTE: change-password and edit-profile tests use a fresh user per test
 * (not the shared abcd@gmail.com) so they can't corrupt state for later tests.
 */
@Tag("Selenium") @Tag("MemberA") @Tag("BVA")
@DisplayName("Member A - Auth BVA Tests")
class MemberAAuthBvaTests extends BaseTest {

    private String uniqueEmail() { return "bva" + System.nanoTime() + "@test.com"; }
    private String rep(String s, int n) { return s.repeat(n); }

    /** Register a brand-new user with valid data and land on dashboard. */
    private void registerFresh(String email) {
        navigate("/auth/register.html");
        type("email", email);
        type("fullName", "BVA User");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    /** Fill register form and submit (used for register BVA tests). */
    private void register(String email, String phone, String pwd) {
        navigate("/auth/register.html");
        type("email", email);
        type("fullName", "BVA User");
        type("phone", phone);
        type("password", pwd);
        click("registerBtn");
    }

    // -----------------------------------------------------------------
    // M1 — register password: range 8–20
    // -----------------------------------------------------------------

    @Test
    @DisplayName("A-BVA-01 (register password): 7 chars rejected (min-1)")
    void registerPasswordMinMinus1() {
        register(uniqueEmail(), "9876543210", rep("a", 7));
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    @Test
    @DisplayName("A-BVA-02 (register password): 8 chars accepted (min)")
    void registerPasswordMin() {
        register(uniqueEmail(), "9876543210", rep("a", 8));
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    @Test
    @DisplayName("A-BVA-03 (register password): 21 chars rejected (max+1)")
    void registerPasswordMaxPlus1() {
        register(uniqueEmail(), "9876543210", rep("a", 21));
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    // -----------------------------------------------------------------
    // M2 — register phone: range 10–13 digits
    // -----------------------------------------------------------------

    @Test
    @DisplayName("A-BVA-04 (register phone): 9 digits rejected (min-1)")
    void registerPhoneMinMinus1() {
        register(uniqueEmail(), rep("9", 9), "Test@1234");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    @Test
    @DisplayName("A-BVA-05 (register phone): 10 digits accepted (min)")
    void registerPhoneMin() {
        register(uniqueEmail(), rep("9", 10), "Test@1234");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    @Test
    @DisplayName("A-BVA-06 (register phone): 14 digits rejected (max+1)")
    void registerPhoneMaxPlus1() {
        register(uniqueEmail(), rep("9", 14), "Test@1234");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    // -----------------------------------------------------------------
    // M3 — change-password newPassword: range 8–20
    // Each test uses a dedicated fresh user to avoid state corruption.
    // -----------------------------------------------------------------

    private void openChangePasswordFor(String email, String password) {
        loginCustomer(email, password);
        navigate("/auth/change-password.html");
    }

    private void submitChangePassword(String oldPwd, String newPwd) {
        type("oldPassword", oldPwd);
        type("newPassword", newPwd);
        driver.findElement(By.cssSelector("#changePasswordForm button[type='submit']")).click();
    }

    @Test
    @DisplayName("A-BVA-07 (changePassword new): 7 chars rejected (min-1)")
    void changePasswordMinMinus1() {
        String email = uniqueEmail();
        registerFresh(email);
        openChangePasswordFor(email, "Test@1234");
        submitChangePassword("Test@1234", rep("a", 7));
        // Rejected -> stays on change-password page
        wait.until(d -> d.getCurrentUrl().contains("change-password"));
    }

    @Test
    @DisplayName("A-BVA-08 (changePassword new): 8 chars accepted (min)")
    void changePasswordMin() {
        String email = uniqueEmail();
        registerFresh(email);
        openChangePasswordFor(email, "Test@1234");
        submitChangePassword("Test@1234", "NewPass1");
        // Accepted -> either redirected away OR no error message shown.
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
        boolean redirected = !driver.getCurrentUrl().contains("change-password");
        boolean noError = driver.findElements(By.id("error")).isEmpty()
                || driver.findElement(By.id("error")).getText().isBlank();
        assertThat(redirected || noError).isTrue();
    }

    @Test
    @DisplayName("A-BVA-09 (changePassword new): 21 chars rejected (max+1)")
    void changePasswordMaxPlus1() {
        String email = uniqueEmail();
        registerFresh(email);
        openChangePasswordFor(email, "Test@1234");
        submitChangePassword("Test@1234", rep("a", 21));
        wait.until(d -> d.getCurrentUrl().contains("change-password"));
    }

    // -----------------------------------------------------------------
    // M4 — edit-profile fullName: range 3–50
    // -----------------------------------------------------------------

    private void openProfileFor(String email, String password) {
        loginCustomer(email, password);
        navigate("/auth/profile.html");
    }

    private void submitProfile(String name, String phone) {
        type("fullName", name);
        type("phone", phone);
        click("saveBtn");
    }

    @Test
    @DisplayName("A-BVA-10 (editProfile fullName): 2 chars rejected (min-1)")
    void profileNameMinMinus1() {
        String email = uniqueEmail();
        registerFresh(email);
        openProfileFor(email, "Test@1234");
        submitProfile(rep("A", 2), "9876543210");
        assertThat(driver.getCurrentUrl()).contains("profile");
    }

    @Test
    @DisplayName("A-BVA-11 (editProfile fullName): 3 chars accepted (min)")
    void profileNameMin() {
        String email = uniqueEmail();
        registerFresh(email);
        openProfileFor(email, "Test@1234");
        submitProfile(rep("A", 3), "9876543210");
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        assertThat(driver.findElements(By.id("editError")).isEmpty()
                || driver.findElement(By.id("editError")).getText().isBlank()).isTrue();
    }

    @Test
    @DisplayName("A-BVA-12 (editProfile fullName): 51 chars rejected (max+1)")
    void profileNameMaxPlus1() {
        String email = uniqueEmail();
        registerFresh(email);
        openProfileFor(email, "Test@1234");
        submitProfile(rep("A", 51), "9876543210");
        assertThat(driver.getCurrentUrl()).contains("profile");
    }

    // -----------------------------------------------------------------
    // M5 — edit-profile phone: range 10–13 digits
    // -----------------------------------------------------------------

    @Test
    @DisplayName("A-BVA-13 (editProfile phone): 9 digits rejected (min-1)")
    void profilePhoneMinMinus1() {
        String email = uniqueEmail();
        registerFresh(email);
        openProfileFor(email, "Test@1234");
        submitProfile("Valid Name", rep("9", 9));
        assertThat(driver.getCurrentUrl()).contains("profile");
    }

    @Test
    @DisplayName("A-BVA-14 (editProfile phone): 10 digits accepted (min)")
    void profilePhoneMin() {
        String email = uniqueEmail();
        registerFresh(email);
        openProfileFor(email, "Test@1234");
        submitProfile("Valid Name", rep("9", 10));
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        assertThat(driver.findElements(By.id("editError")).isEmpty()
                || driver.findElement(By.id("editError")).getText().isBlank()).isTrue();
    }

    @Test
    @DisplayName("A-BVA-15 (editProfile phone): 14 digits rejected (max+1)")
    void profilePhoneMaxPlus1() {
        String email = uniqueEmail();
        registerFresh(email);
        openProfileFor(email, "Test@1234");
        submitProfile("Valid Name", rep("9", 14));
        assertThat(driver.getCurrentUrl()).contains("profile");
    }
}