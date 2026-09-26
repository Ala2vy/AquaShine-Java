package com.aquashine.selenium.memberc;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/** Basis Path — Member C: 1 main-flow test per module. */
@Tag("Selenium") @Tag("MemberC") @Tag("BasisPath")
@DisplayName("Member C - Booking Basis Path Tests")
class MemberCBookingBasisPathTests extends BaseTest {

    private static final String BID = "2";

    /** Returns true if the current page source contains any of the given keywords. */
    private boolean pageMentions(String... keywords) {
        String src = driver.getPageSource().toLowerCase();
        for (String k : keywords) {
            if (src.contains(k.toLowerCase())) return true;
        }
        return false;
    }

    @Test @DisplayName("BP-01 (createBooking): booking page renders form for customer with vehicles")
    void bpCreate() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/book.html");
        wait.until(d -> !d.findElements(By.id("wizardView")).isEmpty()); }

    @Test @DisplayName("BP-02 (viewBookings): bookings list renders")
    void bpView() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/bookings.html");
        wait.until(d -> d.findElement(By.id("bookingList")).isDisplayed()); }

    @Test @DisplayName("BP-03 (cancelBooking): cancel page loads with owned bookingId")
    void bpCancel() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> pageMentions("cancel")); }

    @Test @DisplayName("BP-04 (reschedule): reschedule page loads with owned bookingId")
    void bpReschedule() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/reschedule.html?bookingId=" + BID);
        wait.until(d -> pageMentions("reschedule", "date", "slot")); }

    @Test @DisplayName("BP-05 (refund): cancel page shows refund/cancel info")
    void bpRefund() { loginCustomer("abcd@gmail.com", "123456789");
        navigate("/auth/cancel.html?bookingId=" + BID);
        wait.until(d -> pageMentions("refund", "cancel")); }
}