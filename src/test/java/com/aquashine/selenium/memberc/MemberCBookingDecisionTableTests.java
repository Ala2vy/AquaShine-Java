package com.aquashine.selenium.memberc;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/** Decision Table — Member C: Booking. */
@Tag("Selenium") @Tag("MemberC") @Tag("DecisionTable")
@DisplayName("Member C - Booking Decision Table Tests")
class MemberCBookingDecisionTableTests extends BaseTest {

    private static final String BID = "2";

    private void newUser() {
        navigate("/auth/register.html");
        type("email", "dtc" + System.nanoTime() + "@test.com");
        type("fullName", "DT C"); type("phone", "9876543210");
        type("password", "Test@1234"); click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }
    private void book() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/book.html"); }

    @Test @DisplayName("DT-01: V=N -> empty-state view shown")
    void v0() { newUser(); navigate("/auth/book.html");
        wait.until(d -> d.findElement(By.id("emptyView")).isDisplayed()); }
    @Test @DisplayName("DT-02: V=Y -> booking form shown")
    void v1() { book(); wait.until(d -> !d.findElements(By.id("wizardView")).isEmpty()); }
    @Test @DisplayName("DT-03: V=Y -> vehicle list container present")
    void v1VehicleList() { book(); assertThat(driver.findElements(By.id("vehicleList"))).isNotEmpty(); }
    @Test @DisplayName("DT-04: V=Y, S=Y -> service list container present")
    void v1ServiceList() { book(); assertThat(driver.findElements(By.id("serviceList"))).isNotEmpty(); }
    @Test @DisplayName("DT-05: V=Y, S=Y -> confirm button present")
    void confirmBtn() { book(); assertThat(driver.findElements(By.id("confirmBtn"))).isNotEmpty(); }
    @Test @DisplayName("DT-06: V=Y -> next buttons present")
    void nextButtons() { book();
        assertThat(driver.findElements(By.id("next1"))).isNotEmpty();
        assertThat(driver.findElements(By.id("next2"))).isNotEmpty(); }
    @Test @DisplayName("DT-07: V=Y -> date input present")
    void dateInput() { book(); assertThat(driver.findElements(By.id("slotDate"))).isNotEmpty(); }
    @Test @DisplayName("DT-08: V=Y -> summary bar present")
    void summaryBar() { book(); assertThat(driver.findElements(By.id("summaryBar"))).isNotEmpty(); }
    @Test @DisplayName("DT-09: V=N -> empty-state text mentions 'add'")
    void emptyText() { newUser(); navigate("/auth/book.html");
        wait.until(d -> d.findElement(By.id("emptyView")).isDisplayed());
        assertThat(driver.findElement(By.id("emptyView")).getText()).containsIgnoringCase("add"); }
    @Test @DisplayName("DT-10: cancel page reachable for owned booking")
    void cancelPage() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> d.getPageSource().toLowerCase().contains("cancel")); }
}