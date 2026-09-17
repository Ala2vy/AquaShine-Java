package com.aquashine.selenium.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1400,900");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        // Uncomment for headless:
        // options.addArguments("--headless=new");

        driver = new ChromeDriver(options);

        // Never mix implicit and explicit waits — leave implicit at zero.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
                // Browser may already be dead; nothing useful to do.
            } finally {
                driver = null;
            }
        }
    }

    // ============ Helpers ============

    protected void navigate(String path) {
        driver.get(BASE_URL + path);
    }

    protected WebElement findById(String id) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

    protected void type(String id, String text) {
        WebElement el = findById(id);
        el.clear();
        el.sendKeys(text);
    }

    protected void click(String id) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        el.click();
    }

    protected void loginCustomer(String email, String password) {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='USER']")).click();
        type("email", email);
        type("password", password);
        click("loginBtn");
        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    protected void loginAdmin(String email, String password) {
        navigate("/auth/login.html");
        driver.findElement(By.cssSelector("[data-role='ADMIN']")).click();
        type("email", email);
        type("password", password);
        click("loginBtn");
        wait.until(ExpectedConditions.urlContains("/admin/"));
    }
}