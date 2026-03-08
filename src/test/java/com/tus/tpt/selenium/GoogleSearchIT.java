package com.tus.tpt.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleSearchIT {

    @Test
    void verifyGoogleHomePageLoads() throws Exception {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {

            driver.get("https://www.google.com");

            assertTrue(driver.getTitle().contains("Google"));

        } catch (AssertionError e) {

            File screenshot =
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            FileUtils.copyFile(
                    screenshot,
                    new File("target/screenshots/failure.png")
            );

            throw e;
        }

        finally {
            driver.quit();
        }
    }
}