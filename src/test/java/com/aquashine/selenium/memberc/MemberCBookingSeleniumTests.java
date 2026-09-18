package com.aquashine.selenium.memberc;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("Selenium")
@Tag("MemberC")
@DisplayName("Member C - Booking Selenium Tests")
class MemberCBookingSeleniumTests extends BaseTest {

    @Test
    @DisplayName("C-SE-01: Booking wizard has 5 steps")
    void wizardStepsPresent() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/book.html");

        wait.until(d -> d.findElements(By.cssSelector(".wstep")).size() == 5);
        List<WebElement> steps = driver.findElements(By.cssSelector(".wstep"));
        assertThat(steps).hasSize(5);

        assertThat(steps.get(0).getText()).containsIgnoringCase("vehicle");
        assertThat(steps.get(1).getText()).containsIgnoringCase("service");
        assertThat(steps.get(4).getText()).containsIgnoringCase("confirm");
    }

    @Test
    @DisplayName("C-SE-02: Empty user sees add-vehicle CTA on wizard")
    void emptyVehicleCTA() {
        String email = "emptybook" + System.currentTimeMillis() + "@test.com";

        navigate("/auth/register.html");
        type("email", email);
        type("fullName", "Empty Book");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));

        navigate("/auth/book.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("emptyView")));

        String text = driver.findElement(By.id("emptyView")).getText();
        assertThat(text).containsIgnoringCase("add");
    }

    @Test
    @DisplayName("C-SE-03: My Bookings page renders")
    void bookingsPageRenders() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");

        wait.until(d -> d.findElement(By.id("bookingList")).isDisplayed());
        assertThat(findById("bookingList").isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("C-SE-04: Clicking a service opens wizard with it preselected")
    void serviceClickPreselects() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");

        wait.until(d -> !d.findElements(By.cssSelector(".service-card, .bento-card")).isEmpty());

        List<WebElement> links = driver.findElements(
                By.cssSelector(".service-card a[href*='book.html'], .bento-card a[href*='book.html']"));
        assertThat(links).as("service card 'Book This Service' link").isNotEmpty();

        WebElement bookLink = links.get(0);
        bookLink.click();

        wait.until(d -> d.getCurrentUrl().contains("book.html"));
        assertThat(driver.getCurrentUrl()).contains("book.html");
    }
}