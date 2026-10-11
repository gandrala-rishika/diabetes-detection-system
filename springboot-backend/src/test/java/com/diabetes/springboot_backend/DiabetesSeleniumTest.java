
package com.diabetes.springboot_backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiabetesSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }

    @Test
    void loginPageShouldOpen() {
        driver.get("http://localhost:5001/login");

        String pageTitle = driver.getTitle();
        String pageContent = driver.findElement(By.tagName("body"))
                                    .getText();

        System.out.println("Page title: " + pageTitle);
        System.out.println("Login page opened successfully.");

        assertTrue(
            pageContent.toLowerCase().contains("login")
                || pageContent.toLowerCase().contains("sign in"),
            "Login page should contain login or sign-in text"
        );
    }

    @AfterEach
    void tearDown() {
        // Intentionally do not close Chrome yet.
        // This allows you to inspect the browser after the test.
    }
}
