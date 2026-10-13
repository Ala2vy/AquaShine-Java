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

    /** True if the element is in the DOM AND visible on screen. */
    protected boolean isVisible(String id) {
        var els = driver.findElements(By.id(id));
        return !els.isEmpty() && els.get(0).isDisplayed();
    }

    /** Returns true if the current page source contains any of the given keywords (case-insensitive). */
    protected boolean pageMentions(String... keywords) {
        String src = driver.getPageSource().toLowerCase();
        for (String k : keywords) {
            if (src.contains(k.toLowerCase())) return true;
        }
        return false;
    }

    /** Registers a fresh user and lands on dashboard. Returns the email. */
    protected String registerFreshUser() {
        String email = "t" + System.nanoTime() + "@test.com";
        navigate("/auth/register.html");
        type("email", email);
        type("fullName", "Test User");
        type("phone", "9876543210");
        type("password", "Test@1234");
        click("registerBtn");
        wait.until(ExpectedConditions.urlContains("dashboard"));
        return email;
    }

    /** Registers a fresh user and adds one vehicle, then returns to vehicles page. */
    protected String registerFreshUserWithVehicle() {
        String email = registerFreshUser();
        addVehicleForCurrentUser("AUTO" + (System.nanoTime() % 100000));
        return email;
    }

    /** Adds a vehicle to the currently-logged-in user and returns to the vehicles page. */
    protected void addVehicleForCurrentUser(String plate) {
        navigate("/auth/add-vehicle.html");
        type("plateNumber", plate);
        if (!driver.findElements(By.id("type")).isEmpty()) {
            new org.openqa.selenium.support.ui.Select(findById("type")).selectByValue("SEDAN");
        }
        type("make", "Toyota");
        type("model", "Camry");
        type("year", "2020");
        click("addBtn");
        wait.until(ExpectedConditions.urlContains("vehicles"));
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