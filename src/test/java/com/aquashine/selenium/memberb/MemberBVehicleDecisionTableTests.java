package com.aquashine.selenium.memberb;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Decision Table — Member B: Add Vehicle.
 * Conditions: T = type selected, Y = valid year, P = non-empty plate.
 * 10 rules covering all meaningful combinations.
 */
@Tag("Selenium") @Tag("MemberB") @Tag("DecisionTable")
@DisplayName("Member B - Vehicle Decision Table Tests")
class MemberBVehicleDecisionTableTests extends BaseTest {

    private void freshUser() {
        navigate("/auth/register.html");
        type("email", "dtb" + System.nanoTime() + "@test.com");
        type("fullName", "DT B"); type("phone", "9876543210");
        type("password", "Test@1234"); click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    private void add(String plate, String type, String year) {
        navigate("/auth/add-vehicle.html");
        if (!plate.isEmpty()) type("plateNumber", plate);
        if (!type.isEmpty() && !driver.findElements(By.id("type")).isEmpty())
            new Select(findById("type")).selectByValue(type);
        type("make", "Toyota"); type("model", "Camry");
        if (!year.isEmpty()) type("year", year);
        click("addBtn");
    }

    private String p() { return "D" + (System.nanoTime() % 100000); }

    @Test @DisplayName("DT-01: type=Y, year=Y, plate=Y -> vehicle added")
    void allValid() { freshUser(); add(p(), "SEDAN", "2020");
        wait.until(d -> d.getCurrentUrl().contains("vehicles")); }

    @Test @DisplayName("DT-02: type=N -> rejected")
    void noType() { freshUser(); add(p(), "", "2020");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-03: year=N -> rejected")
    void noYear() { freshUser(); add(p(), "SEDAN", "1800");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-04: plate=N -> rejected")
    void noPlate() { freshUser(); add("", "SEDAN", "2020");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-05: type=N, year=N -> rejected")
    void noTypeNoYear() { freshUser(); add(p(), "", "1800");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-06: type=N, plate=N -> rejected")
    void noTypeNoPlate() { freshUser(); add("", "", "2020");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-07: year=N, plate=N -> rejected")
    void noYearNoPlate() { freshUser(); add("", "SEDAN", "1800");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-08: type=N, year=N, plate=N -> rejected")
    void allInvalid() { freshUser(); add("", "", "1800");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("DT-09: valid add -> plate appears in vehicles list")
    void plateAppears() {
        freshUser();
        String pl = p();
        add(pl, "SEDAN", "2020");
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
        wait.until(d -> d.getPageSource().contains(pl));
    }

    @Test @DisplayName("DT-10: services page shows service cards to logged-in customer")
    void servicesVisible() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");
        wait.until(d -> !d.findElements(By.cssSelector(".service-card, .bento-card")).isEmpty());
    }
}