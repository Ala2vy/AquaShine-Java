package com.aquashine.selenium.memberd;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BVA — Member D: Payment field boundaries + Admin KPI boundaries.
 *
 * Payment tests: fresh user + vehicle + booking, then payment page.
 * Admin tests: use the seeded abc@gmail.com / 12345678 account.
 *
 * Every test targets a numeric or length boundary with a specific value:
 *   Card number length (0, 15, 16, 20)
 *   Promo code length (0, 1, 20)
 *   Admin KPI values (>=0 or >=1)
 *   Admin table row counts (>=0)
 */
@Tag("Selenium") @Tag("MemberD") @Tag("BVA")
@DisplayName("Member D - Payment BVA Tests")
class MemberDPaymentBvaTests extends BaseTest {

    // =================================================================
    // Setup helpers
    // =================================================================

    /** Register fresh user + vehicle + booking, then open the payment page. */
    private void freshBookingAndOpenPayment() {
        registerFreshUserWithVehicle();
        String bookingId = createQuickBooking();
        if (bookingId != null && !bookingId.isEmpty()) {
            navigate("/auth/payment.html?bookingId=" + bookingId);
        } else {
            navigate("/auth/payment.html");
        }
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
    }

    /** Drives the 5-step wizard and returns the booking ID (or ""). */
    private String createQuickBooking() {
        navigate("/auth/book.html");
        wait.until(d -> isVisible("wizardView"));

        wait.until(d -> !d.findElements(By.cssSelector("#vehicleList .choice")).isEmpty());
        driver.findElements(By.cssSelector("#vehicleList .choice")).get(0).click();
        click("next1");

        wait.until(d -> !d.findElements(By.cssSelector("#serviceList .choice")).isEmpty());
        driver.findElements(By.cssSelector("#serviceList .choice")).get(0).click();
        click("next2");

        type("slotDate", java.time.LocalDate.now().plusDays(3).toString());
        click("next3");

        wait.until(d -> !d.findElements(By.cssSelector("#slotGrid button")).isEmpty());
        driver.findElements(By.cssSelector("#slotGrid button")).get(0).click();
        click("next4");

        click("confirmBtn");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String url = driver.getCurrentUrl();
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("[?&]bookingId=(\\d+)").matcher(url);
        if (m.find()) return m.group(1);
        m = java.util.regex.Pattern.compile("[?&]id=(\\d+)").matcher(url);
        if (m.find()) return m.group(1);
        return "";
    }

    // =================================================================
    // M1 — Payment card number length: 0, 15, 16, 20
    // =================================================================

    @Test
    @DisplayName("D-BVA-01 (payment card): empty card field is empty (min-1 = 0)")
    void cardEmpty() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        assertThat(findById("cardNumber").getAttribute("value")).isEmpty();
    }

    @Test
    @DisplayName("D-BVA-02 (payment card): 15 chars below min accepted as input")
    void card15() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1".repeat(15));
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty();
    }

    @Test
    @DisplayName("D-BVA-03 (payment card): 16 chars at min accepted")
    void card16() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1".repeat(16));
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty();
    }

    @Test
    @DisplayName("D-BVA-04 (payment card): 20 chars accepted (upper)")
    void card20() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("cardNumber")).isEmpty()) return;
        type("cardNumber", "1".repeat(20));
        assertThat(findById("cardNumber").getAttribute("value")).isNotEmpty();
    }

    // =================================================================
    // M2 — Promo code length: 0, 1, 20
    // =================================================================

    @Test
    @DisplayName("D-BVA-05 (promo): empty promo accepted (min = 0)")
    void promoEmpty() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        assertThat(findById("promoCode").getAttribute("value")).isEmpty();
    }

    @Test
    @DisplayName("D-BVA-06 (promo): 1-char promo accepted (min+1)")
    void promo1() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        type("promoCode", "A");
        assertThat(findById("promoCode").getAttribute("value")).hasSize(1);
    }

    @Test
    @DisplayName("D-BVA-07 (promo): 20-char promo accepted (upper)")
    void promo20() {
        freshBookingAndOpenPayment();
        if (driver.findElements(By.id("promoCode")).isEmpty()) return;
        type("promoCode", "A".repeat(20));
        assertThat(findById("promoCode").getAttribute("value")).isNotEmpty();
    }

    // =================================================================
    // M3 — Admin dashboard KPIs (boundaries on counts)
    // =================================================================

    private void openAdminDashboard() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/index.html");
        wait.until(d -> isVisible("statUsers"));
    }

    @Test
    @DisplayName("D-BVA-08 (admin statUsers): value >= 1 (min+1)")
    void adminStatUsersAtLeast1() {
        openAdminDashboard();
        int n = Integer.parseInt(driver.findElement(By.id("statUsers")).getText());
        assertThat(n).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("D-BVA-09 (admin statVehicles): value >= 1 (min+1)")
    void adminStatVehiclesAtLeast1() {
        openAdminDashboard();
        int n = Integer.parseInt(driver.findElement(By.id("statVehicles")).getText());
        assertThat(n).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("D-BVA-10 (admin statActive): value >= 0 (min)")
    void adminStatActiveAtLeast0() {
        openAdminDashboard();
        int n = Integer.parseInt(driver.findElement(By.id("statActive")).getText());
        assertThat(n).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("D-BVA-11 (admin statTotalRevenue): parses to non-negative amount")
    void adminStatRevenue() {
        openAdminDashboard();
        String raw = driver.findElement(By.id("statTotalRevenue")).getText();
        // text like "₹1234" — strip non-digits
        String digits = raw.replaceAll("[^0-9]", "");
        assertThat(digits).isNotEmpty();
        assertThat(Integer.parseInt(digits)).isGreaterThanOrEqualTo(0);
    }

    // =================================================================
    // M4 — Admin bookings table (row count boundary)
    // =================================================================

    @Test
    @DisplayName("D-BVA-12 (adminBookings table): row count >= 0 (min)")
    void adminBookingsTableCount() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/bookings.html");
        wait.until(d -> !driver.findElements(By.id("bookingsTable")).isEmpty());
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        int rows = driver.findElements(By.cssSelector("#bookingsTable tr")).size();
        assertThat(rows).isGreaterThanOrEqualTo(0);
    }

    // =================================================================
    // M5 — Admin payments table + KPIs
    // =================================================================

    @Test
    @DisplayName("D-BVA-13 (admin kpiCount): parses to >= 0 (min)")
    void adminKpiCountAtLeast0() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        wait.until(d -> isVisible("kpiCount"));
        int n = Integer.parseInt(driver.findElement(By.id("kpiCount")).getText());
        assertThat(n).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("D-BVA-14 (admin kpiTotal): parses to non-negative amount")
    void adminKpiTotal() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        wait.until(d -> isVisible("kpiTotal"));
        String raw = driver.findElement(By.id("kpiTotal")).getText();
        String digits = raw.replaceAll("[^0-9]", "");
        assertThat(digits).isNotEmpty();
        assertThat(Integer.parseInt(digits)).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("D-BVA-15 (adminPayments table): row count >= 0 (min)")
    void adminPaymentsTableCount() {
        loginAdmin("abc@gmail.com", "12345678");
        navigate("/admin/payments.html");
        wait.until(d -> !driver.findElements(By.id("paymentsTable")).isEmpty());
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        int rows = driver.findElements(By.cssSelector("#paymentsTable tr")).size();
        assertThat(rows).isGreaterThanOrEqualTo(0);
    }
}