package com.aquashine.selenium.memberb;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("Selenium")
@Tag("MemberB")
@DisplayName("Member B - Vehicle & Service Selenium Tests")
class MemberBVehicleSeleniumTests extends BaseTest {

    private void createFreshUser() {
        String uniqueEmail = "memberB" + System.currentTimeMillis() + "@test.com";
        navigate("/auth/register.html");
        type("email", uniqueEmail);
        type("fullName", "Member B Test");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    @Test
    @DisplayName("B-SE-01: Services page lists 3 wash services")
    void servicesListed() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");

        wait.until(d -> !d.findElements(By.cssSelector(".service-card, .bento-card")).isEmpty());
        List<WebElement> services = driver.findElements(By.cssSelector(".service-card, .bento-card"));
        assertThat(services.size()).isGreaterThanOrEqualTo(3);

        String html = driver.getPageSource();
        assertThat(html).containsIgnoringCase("Basic");
        assertThat(html).containsIgnoringCase("Premium");
    }

    @Test
    @DisplayName("B-SE-02: Add vehicle form is accessible")
    void addVehicleForm() {
        createFreshUser();
        navigate("/auth/add-vehicle.html");

        assertThat(findById("plateNumber").isDisplayed()).isTrue();
        assertThat(findById("type").isDisplayed()).isTrue();
        assertThat(findById("year").isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("B-SE-03: Adding a vehicle redirects to vehicles list")
    void addVehicleFlow() {
        createFreshUser();
        navigate("/auth/add-vehicle.html");

        String plate = "SEL" + (System.currentTimeMillis() % 10000);
        type("plateNumber", plate);

        Select typeSelect = new Select(findById("type"));
        typeSelect.selectByValue("SEDAN");

        type("make", "Toyota");
        type("model", "Camry");
        type("year", "2020");
        click("addBtn");

        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
        wait.until(d -> d.getPageSource().contains(plate));
        assertThat(driver.getPageSource()).contains(plate);
    }
}