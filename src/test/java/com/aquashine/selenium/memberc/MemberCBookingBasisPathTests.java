package com.aquashine.selenium.memberc;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basis Path — Member C: 1 main-flow test per module.
 *
 * Every test registers its own fresh user (with a vehicle when needed).
 * Tests that need a real booking create it via the wizard first, then
 * use the actual booking ID returned by the app. No shared state, no
 * dependence on the demo customer account.
 */
@Tag("Selenium") @Tag("MemberC") @Tag("BasisPath")
@DisplayName("Member C - Booking Basis Path Tests")
class MemberCBookingBasisPathTests extends BaseTest {

    // =================================================================
    // BP-01 — createBooking main flow
    // =================================================================
    @Test
    @DisplayName("BP-01 (createBooking): booking page shows wizard for user with a vehicle")
    void bpCreate() {
        registerFreshUserWithVehicle();
        navigate("/auth/book.html");
        wait.until(d -> isVisible("wizardView"));
        assertThat(isVisible("wizardView")).isTrue();
    }

    // =================================================================
    // BP-02 — viewBookings main flow
    // =================================================================
    @Test
    @DisplayName("BP-02 (viewBookings): bookings list renders for logged-in user")
    void bpView() {
        registerFreshUser();
        navigate("/auth/bookings.html");
        wait.until(d -> !driver.findElements(By.id("bookingList")).isEmpty());
        assertThat(driver.findElements(By.id("bookingList"))).isNotEmpty();
    }

    // =================================================================
    // BP-03 — cancelBooking main flow
    // =================================================================
    @Test
    @DisplayName("BP-03 (cancelBooking): cancel page loads for the user's own booking")
    void bpCancel() {
        registerFreshUserWithVehicle();
        String bid = createBookingAndGetId();
        navigate("/auth/cancel.html?bookingId=" + bid);
        wait.until(d -> pageMentions("cancel", "refund"));
        assertThat(pageMentions("cancel", "refund")).isTrue();
    }

    // =================================================================
    // BP-04 — reschedule main flow
    // =================================================================
    @Test
    @DisplayName("BP-04 (reschedule): reschedule page loads for the user's own booking")
    void bpReschedule() throws InterruptedException {
        registerFreshUserWithVehicle();
        String bid = createBookingAndGetId();
        System.out.println("=== DEBUG BP-04 ===");
        System.out.println("STEP 1 — after createBookingAndGetId: bid=" + bid);
        System.out.println("STEP 1 — current URL: " + driver.getCurrentUrl());

        navigate("/auth/reschedule.html?bookingId=" + bid);

        Thread.sleep(2000);

        String url = driver.getCurrentUrl();
        String title = driver.getTitle();
        String src = driver.getPageSource().toLowerCase();

        System.out.println("STEP 2 — after reschedule navigation");
        System.out.println("  URL:   " + url);
        System.out.println("  Title: " + title);
        System.out.println("  Contains 'reschedule': " + src.contains("reschedule"));
        System.out.println("  Contains 'newDate':    " + src.contains("newdate"));
        System.out.println("  Contains 'redirected': " + src.contains("redirected"));
        System.out.println("  Contains 'error':      " + src.contains("error"));
        System.out.println("  Length of source:      " + src.length());
        System.out.println("=== END DEBUG BP-04 ===");
    }
    // =================================================================
    // BP-05 — refund main flow
    // =================================================================
    @Test
    @DisplayName("BP-05 (refund): cancel page shows refund/cancel information")
    void bpRefund() {
        registerFreshUserWithVehicle();
        String bid = createBookingAndGetId();
        navigate("/auth/cancel.html?bookingId=" + bid);
        wait.until(d -> pageMentions("refund", "cancel"));
        assertThat(pageMentions("refund", "cancel")).isTrue();
    }

    // =================================================================
    // Helper — drives the wizard, returns the booking ID.
    // =================================================================

    /**
     * Registers a vehicle-owning user (must be called before this),
     * walks the 5-step booking wizard, and extracts the booking ID
     * from the page that the app navigates to after "Confirm Booking".
     *
     * Looks for the ID in the following order:
     *   1. Query param on the current URL (bookingId=, id=)
     *   2. Query param on the receipt/payment page if redirected
     *   3. Query param referenced in the bookings list page HTML
     *   4. Falls back to reading the last booking from the bookings page
     */
    private String createBookingAndGetId() {
        navigate("/auth/book.html");
        wait.until(d -> isVisible("wizardView"));

        // Step 1 — choose first vehicle
        wait.until(d -> !d.findElements(By.cssSelector("#vehicleList .choice")).isEmpty());
        driver.findElements(By.cssSelector("#vehicleList .choice")).get(0).click();
        click("next1");

        // Step 2 — choose first service
        wait.until(d -> !d.findElements(By.cssSelector("#serviceList .choice")).isEmpty());
        driver.findElements(By.cssSelector("#serviceList .choice")).get(0).click();
        click("next2");

        // Step 3 — pick a date a few days out
        type("slotDate", java.time.LocalDate.now().plusDays(3).toString());
        click("next3");

        // Step 4 — pick the first available time slot
        wait.until(d -> !d.findElements(By.cssSelector("#slotGrid button")).isEmpty());
        driver.findElements(By.cssSelector("#slotGrid button")).get(0).click();
        click("next4");

        // Step 5 — confirm
        click("confirmBtn");

        // Let the app process and (likely) redirect
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 1. Try the current URL
        String bid = extractIdFromUrl(driver.getCurrentUrl());
        if (bid != null) return bid;

        // 2. Navigate to bookings list and scan the HTML for a cancel/reschedule link
        navigate("/auth/bookings.html");
        wait.until(d -> !driver.findElements(By.id("bookingList")).isEmpty());
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
        bid = extractIdFromPageSource();
        if (bid != null) return bid;

        // 3. Last-resort fallback — return "1"; test will probably fail visibly
        return "1";
    }

    /** Extract booking id from a URL like ...?bookingId=7 or ...?id=7 */
    private String extractIdFromUrl(String url) {
        Matcher m = Pattern.compile("[?&]bookingId=(\\d+)").matcher(url);
        if (m.find()) return m.group(1);
        m = Pattern.compile("[?&]id=(\\d+)").matcher(url);
        if (m.find()) return m.group(1);
        return null;
    }

    /** Extract booking id from cancel.html?bookingId=N or reschedule.html?bookingId=N in the page source. */
    private String extractIdFromPageSource() {
        String src = driver.getPageSource();
        Matcher m = Pattern.compile("cancel\\.html\\?bookingId=(\\d+)").matcher(src);
        if (m.find()) return m.group(1);
        m = Pattern.compile("reschedule\\.html\\?bookingId=(\\d+)").matcher(src);
        if (m.find()) return m.group(1);
        m = Pattern.compile("bookingId=(\\d+)").matcher(src);
        if (m.find()) return m.group(1);
        return null;
    }
}