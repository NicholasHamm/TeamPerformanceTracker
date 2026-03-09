package com.tus.tpt.selenium;

import java.time.Duration;

import io.cucumber.spring.ScenarioScope;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class DriverFactory {

    private final Environment env;
    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    public DriverFactory(Environment env) {
        this.env = env;
    }

    public void startDriver() {
        if (driver != null) {
            return;
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--window-size=1920,1080");
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//
//        if (Boolean.parseBoolean(env.getProperty("selenium.headless", "true"))) {
//            options.addArguments("--headless=new");
//        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(baseUrl());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginUsername")));
    }

    public void stopDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            wait = null;
        }
    }

    public WebDriver driver() {
        return driver;
    }

    public WebDriverWait waitFor() {
        return wait;
    }

    public String baseUrl() {
        String port = env.getProperty("local.server.port",
                env.getProperty("server.port", "8082"));
        return "http://localhost:" + port;
    }
}