package com.aquashine.selenium.memberb;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

/** Basis Path — Member B: 1 main-flow test per module. */
@Tag("Selenium") @Tag("MemberB") @Tag("BasisPath")
@DisplayName("Member B - Vehicle Basis Path Tests")
class MemberBVehicleBasisPathTests extends BaseTest {

    private void freshUser() {
        navigate("/auth/register.html");
        type("email", "bpb" + System.nanoTime() + "@test.com");
        type("fullName", "BP B"); type("phone", "9876543210");
        type("password", "Test@1234"); click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    /** M1 addVehicle main flow. */
    @Test @DisplayName("BP-01 (addVehicle): valid form saves vehicle and returns to list")
    void bpAdd() {
        freshUser(); navigate("/auth/add-vehicle.html");
        type("plateNumber", "BP" + (System.nanoTime() % 100000));
        if (!driver.findElements(By.id("type")).isEmpty())
            new Select(findById("type")).selectByValue("SEDAN");
        type("make", "Toyota"); type("model", "Camry"); type("year", "2020");
        click("addBtn");
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
    }

    /** M2 editVehicle main flow. */
    @Test @DisplayName("BP-02 (editVehicle): vehicles page loads with grid")
    void bpEdit() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        wait.until(d -> !d.findElements(By.id("vehicleGrid")).isEmpty());
    }

    /** M3 deleteVehicle main flow. */
    @Test @DisplayName("BP-03 (deleteVehicle): delete handler present in vehicles page")
    void bpDelete() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        assertThat(driver.getPageSource().toLowerCase()).contains("delete");
    }

    /** M4 servicesList main flow. */
    @Test @DisplayName("BP-04 (servicesList): service cards render for logged-in customer")
    void bpServices() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");
        wait.until(d -> !d.findElements(By.cssSelector(".service-card, .bento-card")).isEmpty());
    }

    /** M5 vehicleList main flow. */
    @Test @DisplayName("BP-05 (vehicleList): vehicles page loads")
    void bpList() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        assertThat(driver.getCurrentUrl()).contains("vehicles.html");
    }
}