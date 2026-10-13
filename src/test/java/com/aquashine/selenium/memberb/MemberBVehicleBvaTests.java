package com.aquashine.selenium.memberb;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BVA — Member B: Vehicle boundaries.
 *
 * Bounds (from VehicleService):
 *   plate length:      3–15 chars
 *   year:              1900–(currentYear+1)
 *   vehicles per user: 0–5
 *
 * 5 modules x 3 boundary values = 15 tests.
 * Each test supplies a boundary value and asserts accept/reject.
 *
 * NOTE: The UI has no edit-vehicle link (only Delete). So M4 is
 * "deleteVehicle" using the Delete button and confirm dialog.
 */
@Tag("Selenium") @Tag("MemberB") @Tag("BVA")
@DisplayName("Member B - Vehicle BVA Tests")
class MemberBVehicleBvaTests extends BaseTest {

    private static final int CURRENT_YEAR = Year.now().getValue();

    private String rep(String s, int n) { return s.repeat(n); }

    private void freshUser() {
        navigate("/auth/register.html");
        type("email", "bvab" + System.nanoTime() + "@test.com");
        type("fullName", "BVA B");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    /** Fill add-vehicle form and submit. */
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

    /** Count vehicle cards rendered inside #vehicleGrid. Empty state is not a card. */
    private int vehicleCardCount() {
        return driver.findElements(By.cssSelector("#vehicleGrid .card")).size();
    }

    /** True if the empty-state block is shown in the grid. */
    private boolean emptyStateShown() {
        return !driver.findElements(By.cssSelector("#vehicleGrid .empty-state")).isEmpty();
    }

    // =================================================================
    // M1 — addVehicle plate length: 3–15
    // =================================================================

    @Test
    @DisplayName("B-BVA-01 (addVehicle plate): 2 chars rejected (min-1)")
    void plateMinMinus1() {
        freshUser();
        addVehicle(rep("A", 2), "2020");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles");
    }

    @Test
    @DisplayName("B-BVA-02 (addVehicle plate): 3 chars accepted (min)")
    void plateMin() {
        freshUser();
        addVehicle(rep("A", 3), "2020");
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
    }

    @Test
    @DisplayName("B-BVA-03 (addVehicle plate): 16 chars rejected (max+1)")
    void plateMaxPlus1() {
        freshUser();
        addVehicle(rep("A", 16), "2020");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles");
    }

    // =================================================================
    // M2 — addVehicle year: 1900–(currentYear+1)
    // =================================================================

    @Test
    @DisplayName("B-BVA-04 (addVehicle year): 1899 rejected (min-1)")
    void yearMinMinus1() {
        freshUser();
        addVehicle(plate(), "1899");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles");
    }

    @Test
    @DisplayName("B-BVA-05 (addVehicle year): 1900 accepted (min)")
    void yearMin() {
        freshUser();
        addVehicle(plate(), "1900");
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
    }

    @Test
    @DisplayName("B-BVA-06 (addVehicle year): currentYear+2 rejected (max+1)")
    void yearMaxPlus1() {
        freshUser();
        addVehicle(plate(), String.valueOf(CURRENT_YEAR + 2));
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles");
    }

    // =================================================================
    // M3 — addVehicle vehicle count per user: 0–5
    // =================================================================

    private void addVehicleWithPlate(String plateValue) {
        navigate("/auth/add-vehicle.html");
        type("plateNumber", plateValue);
        if (!driver.findElements(By.id("type")).isEmpty())
            new Select(findById("type")).selectByValue("SEDAN");
        type("make", "Toyota");
        type("model", "Camry");
        type("year", "2020");
        click("addBtn");
    }

    @Test
    @DisplayName("B-BVA-07 (vehicleCount): 6th vehicle rejected (max+1)")
    void vehicleCountMaxPlus1() {
        freshUser();
        for (int i = 0; i < 5; i++) {
            addVehicleWithPlate("FULL" + i);
            wait.until(d -> d.getCurrentUrl().contains("vehicles"));
            navigate("/auth/add-vehicle.html");
        }
        addVehicleWithPlate("FULL6");
        assertThat(driver.getCurrentUrl()).doesNotContain("/vehicles");
    }

    @Test
    @DisplayName("B-BVA-08 (vehicleCount): 5th vehicle accepted (max)")
    void vehicleCountMax() {
        freshUser();
        for (int i = 0; i < 4; i++) {
            addVehicleWithPlate("FOUR" + i);
            wait.until(d -> d.getCurrentUrl().contains("vehicles"));
            navigate("/auth/add-vehicle.html");
        }
        addVehicleWithPlate("FOUR4");
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
    }

    @Test
    @DisplayName("B-BVA-09 (vehicleCount): 1st vehicle accepted (min)")
    void vehicleCountMin() {
        freshUser();
        addVehicleWithPlate("FIRST1");
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
    }

    // =================================================================
    // M4 — deleteVehicle: card count 1 -> 0
    // Uses the Delete button + confirm dialog (JS alert).
    // =================================================================

    /** Adds a vehicle, waits for the vehicles page to load. */
    private void addAndGoToVehicles(String plateValue) {
        freshUser();
        addVehicleWithPlate(plateValue);
        wait.until(d -> d.getCurrentUrl().contains("vehicles"));
        // Wait for JS to render the grid
        wait.until(d -> vehicleCardCount() > 0 || emptyStateShown());
    }

    /**
     * M4 boundary: 0 vehicles -> empty state shown (min boundary of count).
     */
    @Test
    @DisplayName("B-BVA-10 (deleteVehicle): empty state shown at 0 vehicles")
    void deleteEmptyState() {
        freshUser();
        navigate("/auth/vehicles.html");
        wait.until(d -> emptyStateShown());
        assertThat(emptyStateShown()).isTrue();
    }

    /**
     * M4 boundary: 1 vehicle -> exactly 1 card rendered.
     */
    @Test
    @DisplayName("B-BVA-11 (deleteVehicle): 1 vehicle -> exactly 1 card")
    void deleteOneCard() {
        addAndGoToVehicles(plate());
        wait.until(d -> vehicleCardCount() == 1);
        assertThat(vehicleCardCount()).isEqualTo(1);
    }

    /**
     * M4 boundary: delete the only vehicle -> back to empty state.
     * Handles the JS confirm dialog.
     */
    @Test
    @DisplayName("B-BVA-12 (deleteVehicle): delete only vehicle -> empty state")
    void deleteOneBack() {
        addAndGoToVehicles(plate());
        wait.until(d -> vehicleCardCount() == 1);
        // Click the Delete button on the only card
        driver.findElement(By.cssSelector("#vehicleGrid .card button")).click();
        // Accept the confirm dialog
        try {
            driver.switchTo().alert().accept();
        } catch (Exception ignored) {}
        // The JS reloads the page after delete
        wait.until(d -> emptyStateShown());
        assertThat(emptyStateShown()).isTrue();
    }

    // =================================================================
    // M5 — vehicleList rendered card count: 0–5
    // =================================================================

    @Test
    @DisplayName("B-BVA-13 (vehicleList): 0 vehicles -> empty state div")
    void listCountEmpty() {
        freshUser();
        navigate("/auth/vehicles.html");
        wait.until(d -> emptyStateShown());
        assertThat(vehicleCardCount()).isZero();
    }

    @Test
    @DisplayName("B-BVA-14 (vehicleList): 1 vehicle -> 1 card")
    void listCountOne() {
        addAndGoToVehicles(plate());
        wait.until(d -> vehicleCardCount() == 1);
        assertThat(vehicleCardCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("B-BVA-15 (vehicleList): 5 vehicles -> 5 cards (max)")
    void listCountMax() {
        freshUser();
        for (int i = 0; i < 5; i++) {
            addVehicleWithPlate("MAX" + i);
            wait.until(d -> d.getCurrentUrl().contains("vehicles"));
            if (i < 4) navigate("/auth/add-vehicle.html");
        }
        wait.until(d -> vehicleCardCount() == 5);
        assertThat(vehicleCardCount()).isEqualTo(5);
    }
}