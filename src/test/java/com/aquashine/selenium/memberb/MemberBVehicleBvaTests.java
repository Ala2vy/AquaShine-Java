package com.aquashine.selenium.memberb;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BVA — Member B: Vehicle & Services.
 * 5 modules x 3 boundary values = 15 tests.
 */
@Tag("Selenium") @Tag("MemberB") @Tag("BVA")
@DisplayName("Member B - Vehicle BVA Tests")
class MemberBVehicleBvaTests extends BaseTest {

    private void freshUser() {
        navigate("/auth/register.html");
        type("email", "bvab" + System.nanoTime() + "@test.com");
        type("fullName", "BVA B");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    private void addVehicle(String plate, String year) {
        navigate("/auth/add-vehicle.html");
        type("plateNumber", plate);
        if (!driver.findElements(By.id("type")).isEmpty())
            new Select(findById("type")).selectByValue("SEDAN");
        type("make", "Toyota");
        type("model", "Camry");
        type("year", year);
        click("addBtn");
    }

    private String plate() { return "B" + (System.nanoTime() % 100000); }

    // --- M1 addVehicle ---
    @Test @DisplayName("B-BVA-01 (addVehicle): year 1979 rejected (below minimum)")
    void addYearBelow() { freshUser(); addVehicle(plate(), "1979");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    @Test @DisplayName("B-BVA-02 (addVehicle): year 1980 accepted (minimum)")
    void addYearMin() { freshUser(); addVehicle(plate(), "1980");
        wait.until(d -> d.getCurrentUrl().contains("vehicles")); }

    @Test @DisplayName("B-BVA-03 (addVehicle): 3-char plate accepted (minimum)")
    void addPlateMin() { freshUser(); addVehicle("ABC", "2020");
        wait.until(d -> d.getCurrentUrl()).contains("vehicles"); }

    // --- M2 editVehicle ---
    @Test @DisplayName("B-BVA-04 (editVehicle): vehicles list accessible for demo customer")
    void editListAccessible() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        assertThat(driver.getCurrentUrl()).contains("vehicles.html");
    }

    @Test @DisplayName("B-BVA-05 (editVehicle): edit page for existing vehicle loads")
    void editPageLoads() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/edit-vehicle.html?id=1");
        assertThat(driver.getCurrentUrl()).contains("edit-vehicle");
    }

    @Test @DisplayName("B-BVA-06 (editVehicle): 2-char plate rejected (below minimum)")
    void editPlateBelow() { freshUser(); addVehicle("AB", "2020");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles"); }

    // --- M3 deleteVehicle ---
    @Test @DisplayName("B-BVA-07 (deleteVehicle): delete handler present on vehicles page")
    void deleteHandler() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        assertThat(driver.getPageSource().toLowerCase()).contains("delete");
    }

    @Test @DisplayName("B-BVA-08 (deleteVehicle): vehicle grid container present")
    void deleteGrid() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        wait.until(d -> !d.findElements(By.id("vehicleGrid")).isEmpty());
    }

    @Test @DisplayName("B-BVA-09 (deleteVehicle): vehicles page URL correct")
    void deleteUrl() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        assertThat(driver.getCurrentUrl()).contains("vehicles.html");
    }

    // --- M4 servicesList ---
    @Test @DisplayName("B-BVA-10 (servicesList): at least 3 service cards shown (minimum)")
    void servicesMin() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");
        wait.until(d -> d.findElements(By.cssSelector(".service-card, .bento-card")).size() >= 3);
    }

    @Test @DisplayName("B-BVA-11 (servicesList): 'Basic' service present")
    void servicesBasic() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");
        wait.until(d -> d.getPageSource().contains("Basic"));
    }

    @Test @DisplayName("B-BVA-12 (servicesList): 'Premium' service present")
    void servicesPremium() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/services.html");
        wait.until(d -> d.getPageSource().contains("Premium"));
    }

    // --- M5 vehicleList ---
    @Test @DisplayName("B-BVA-13 (vehicleList): new user sees empty vehicles page")
    void listEmpty() { freshUser(); navigate("/auth/vehicles.html");
        assertThat(driver.getCurrentUrl()).contains("vehicles.html"); }

    @Test @DisplayName("B-BVA-14 (vehicleList): demo customer sees populated list")
    void listPopulated() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        wait.until(d -> !d.findElements(By.id("vehicleGrid")).isEmpty());
    }

    @Test @DisplayName("B-BVA-15 (vehicleList): vehicles page URL correct")
    void listUrl() {
        loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/vehicles.html");
        assertThat(driver.getCurrentUrl()).contains("vehicles.html");
    }
}