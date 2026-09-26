package com.aquashine.selenium.membera;

import com.aquashine.selenium.base.BaseTest;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Decision Table — Member A: Login.
 *
 * Conditions: R = role tab, E = email belongs to that role, P = password matches.
 * 10 rules covering all meaningful combinations.
 */
@Tag("Selenium") @Tag("MemberA") @Tag("DecisionTable")
@DisplayName("Member A - Auth Decision Table Tests")
class MemberAAuthDecisionTableTests extends BaseTest {

    private void login(String role, String email, String pwd) {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='" + role + "']")).click();
        type("email", email);
        type("password", pwd);
        click("loginBtn");
    }

    /** Rule 1: USER role + USER email + correct password -> dashboard. */
    @Test
    @DisplayName("DT-01: USER role + USER email + correct password -> dashboard")
    void userUserCorrect() {
        login("USER", "abcd@gmail.com", "123456789");
        wait.until(d -> d.getCurrentUrl().contains("dashboard.html"));
    }

    /** Rule 2: USER role + USER email + wrong password -> rejected. */
    @Test
    @DisplayName("DT-02: USER role + USER email + wrong password -> rejected")
    void userUserWrongPwd() {
        login("USER", "abcd@gmail.com", "wrong");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    /** Rule 3: USER role + ADMIN email + correct password -> wrong-role rejection. */
    @Test
    @DisplayName("DT-03: USER role + ADMIN email + correct password -> rejected")
    void userAdminEmail() {
        login("USER", "abc@gmail.com", "12345678");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    /** Rule 4: USER role + ADMIN email + wrong password -> rejected. */
    @Test
    @DisplayName("DT-04: USER role + ADMIN email + wrong password -> rejected")
    void userAdminWrongPwd() {
        login("USER", "abc@gmail.com", "wrong");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    /** Rule 5: USER role + unknown email -> rejected. */
    @Test
    @DisplayName("DT-05: USER role + unknown email -> rejected")
    void userUnknown() {
        login("USER", "ghost@test.com", "anything");
        assertThat(driver.getCurrentUrl()).doesNotContain("dashboard");
    }

    /** Rule 6: ADMIN role + ADMIN email + correct password -> /admin/. */
    @Test
    @DisplayName("DT-06: ADMIN role + ADMIN email + correct password -> /admin/")
    void adminAdminCorrect() {
        login("ADMIN", "abc@gmail.com", "12345678");
        wait.until(d -> d.getCurrentUrl().contains("/admin/"));
    }

    /** Rule 7: ADMIN role + ADMIN email + wrong password -> rejected. */
    @Test
    @DisplayName("DT-07: ADMIN role + ADMIN email + wrong password -> rejected")
    void adminAdminWrongPwd() {
        login("ADMIN", "abc@gmail.com", "wrong");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/");
    }

    /** Rule 8: ADMIN role + USER email + correct password -> rejected. */
    @Test
    @DisplayName("DT-08: ADMIN role + USER email + correct password -> rejected")
    void adminUserEmail() {
        login("ADMIN", "abcd@gmail.com", "123456789");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/");
    }

    /** Rule 9: ADMIN role + USER email + wrong password -> rejected. */
    @Test
    @DisplayName("DT-09: ADMIN role + USER email + wrong password -> rejected")
    void adminUserWrongPwd() {
        login("ADMIN", "abcd@gmail.com", "wrong");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/");
    }

    /** Rule 10: ADMIN role + unknown email -> rejected. */
    @Test
    @DisplayName("DT-10: ADMIN role + unknown email -> rejected")
    void adminUnknown() {
        login("ADMIN", "ghost@test.com", "anything");
        assertThat(driver.getCurrentUrl()).doesNotContain("/admin/");
    }
}