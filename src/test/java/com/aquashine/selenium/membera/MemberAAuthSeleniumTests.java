package com.aquashine.selenium.membera;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("Selenium")
@Tag("MemberA")
@DisplayName("Member A - Auth Selenium Tests")
class MemberAAuthSeleniumTests extends BaseTest {

    @Test
    @DisplayName("A-SE-01: Customer login lands on dashboard")
    void customerLogin() {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='USER']")).click();
        type("email", "abcd@gmail.com");
        type("password", "123456789");
        click("loginBtn");

        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
        assertThat(driver.getCurrentUrl()).contains("dashboard.html");
    }

    @Test
    @DisplayName("A-SE-02: Admin login lands on admin portal")
    void adminLogin() {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='ADMIN']")).click();
        type("email", "abc@gmail.com");
        type("password", "12345678");
        click("loginBtn");

        wait.until(d -> d.getCurrentUrl().contains("/admin/"));
        assertThat(driver.getCurrentUrl()).contains("/admin/");
    }

    @Test
    @DisplayName("A-SE-03: Admin credentials on USER tab are rejected")
    void wrongRoleTab() {
        navigate("/auth/login.html");

        // USER tab is selected by default, but click it anyway to be explicit.
        driver.findElement(By.cssSelector("[data-role='USER']")).click();

        // Use the ADMIN account credentials while the USER role is selected.
        type("email", "abc@gmail.com");
        type("password", "12345678");
        click("loginBtn");

        // The app should reject this: URL must stay on login.html,
        // and the #error element must be visible.
        wait.until(d -> {
            List<WebElement> errs = d.findElements(By.id("error"));
            if (errs.isEmpty()) return false;
            return errs.get(0).isDisplayed();
        });

        // Must NOT have navigated to an admin page.
        assertThat(driver.getCurrentUrl())
                .doesNotContain("/admin/")
                .doesNotContain("dashboard");

        // The error message the app actually shows.
        assertThat(driver.findElement(By.id("error")).getText())
                .containsIgnoringCase("invalid");
    }

    @Test
    @DisplayName("A-SE-04: Register new user lands on dashboard")
    void registerNew() {
        String uniqueEmail = "memberA" + System.currentTimeMillis() + "@test.com";

        navigate("/auth/register.html");
        type("email", uniqueEmail);
        type("fullName", "Member A Test");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");

        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
        assertThat(driver.getCurrentUrl()).contains("dashboard.html");
    }

    @Test
    @DisplayName("DEBUG: what the login page looks like after submit")
    void debugLogin() {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='USER']")).click();
        type("email", "abcd@gmail.com");
        type("password", "Pass@123");
        click("loginBtn");

        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        System.out.println("=== DEBUG ===");
        System.out.println("URL after login: " + driver.getCurrentUrl());
        System.out.println("Page source:");
        System.out.println(driver.getPageSource());
        System.out.println("=== END ===");
    }
}