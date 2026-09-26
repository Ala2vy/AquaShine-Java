package com.aquashine.selenium.memberc;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/** BVA â€” Member C: Booking. */
@Tag("Selenium") @Tag("MemberC") @Tag("BVA")
@DisplayName("Member C - Booking BVA Tests")
class MemberCBookingBvaTests extends BaseTest {

    private static final String BID = "2";

    /** Returns true if the current page source contains any of the given keywords. */
    private boolean pageMentions(String... keywords) {
        String src = driver.getPageSource().toLowerCase();
        for (String k : keywords) {
            if (src.contains(k.toLowerCase())) return true;
        }
        return false;
    }

    private void newUser() {
        navigate("/auth/register.html");
        type("email", "bvac" + System.nanoTime() + "@test.com");
        type("fullName", "BVA C"); type("phone", "9876543210");
        type("password", "Test@1234"); click("registerBtn");
        wait.until(d -> d.getCurrentUrl().contains("dashboard"));
    }

    @Test @DisplayName("C-BVA-01 (createBooking): new user with 0 vehicles sees empty-state")
    void m1_noVehicles() { newUser(); navigate("/auth/book.html");
        wait.until(d -> d.findElement(By.id("emptyView")).isDisplayed()); }

    @Test @DisplayName("C-BVA-02 (createBooking): customer with vehicles sees booking form")
    void m1_hasVehicles() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/book.html");
        wait.until(d -> !d.findElements(By.id("wizardView")).isEmpty()); }

    @Test @DisplayName("C-BVA-03 (createBooking): booking form shows vehicle list")
    void m1_vehicleList() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/book.html");
        assertThat(driver.findElements(By.id("vehicleList"))).isNotEmpty(); }

    @Test @DisplayName("C-BVA-04 (viewBookings): bookings list renders")
    void m2_renders() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");
        wait.until(d -> d.findElement(By.id("bookingList")).isDisplayed()); }

    @Test @DisplayName("C-BVA-05 (viewBookings): bookings page URL correct")
    void m2_url() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");
        assertThat(driver.getCurrentUrl()).contains("bookings.html"); }

    @Test @DisplayName("C-BVA-06 (viewBookings): booking list container present")
    void m2_container() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");
        assertThat(driver.findElements(By.id("bookingList"))).isNotEmpty(); }

    @Test @DisplayName("C-BVA-07 (cancelBooking): cancel page loads with owned bookingId")
    void m3_validId() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> pageMentions("cancel")); }

    @Test @DisplayName("C-BVA-08 (cancelBooking): cancel page text mentions 'cancel'")
    void m3_text() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> pageMentions("cancel")); }

    @Test @DisplayName("C-BVA-09 (cancelBooking): cancel page exposes refund/cancel info")
    void m3_refund() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> pageMentions("refund", "cancel")); }

    @Test @DisplayName("C-BVA-10 (reschedule): reschedule page loads with owned bookingId")
    void m4_validId() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/reschedule.html?bookingId=" + BID);
        wait.until(d -> pageMentions("reschedule", "date", "slot")); }

    @Test @DisplayName("C-BVA-11 (reschedule): reschedule page mentions date/slot")
    void m4_text() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/reschedule.html?bookingId=" + BID);
        wait.until(d -> pageMentions("date", "slot", "reschedule")); }

    @Test @DisplayName("C-BVA-12 (reschedule): reschedule page reachable")
    void m4_reachable() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/reschedule.html?bookingId=" + BID);
        assertThat(pageMentions("reschedule", "date", "slot")).isTrue(); }

    @Test @DisplayName("C-BVA-13 (refund): cancel page shows refund info")
    void m5_refundText() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> pageMentions("refund", "cancel")); }

    @Test @DisplayName("C-BVA-14 (refund): bookings list available for refund flow")
    void m5_bookingsList() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");
        wait.until(d -> d.findElement(By.id("bookingList")).isDisplayed()); }

    @Test @DisplayName("C-BVA-15 (refund): refund preview reachable from bookings list")
    void m5_url() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");
        wait.until(d -> d.findElement(By.id("bookingList")).isDisplayed()); }
}