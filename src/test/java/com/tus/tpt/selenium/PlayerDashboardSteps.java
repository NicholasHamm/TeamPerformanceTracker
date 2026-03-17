package com.tus.tpt.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PlayerDashboardSteps {

    private final DriverFactory df;
    
    public PlayerDashboardSteps(DriverFactory df) {
        this.df = df;
    }

    @Given("a player user is logged in")
    public void coachUserIsLoggedIn() {
        df.startDriver();

        df.driver().findElement(By.id("loginUsername")).clear();
        df.driver().findElement(By.id("loginUsername")).sendKeys("player1");

        df.driver().findElement(By.id("loginPassword")).clear();
        df.driver().findElement(By.id("loginPassword")).sendKeys("player1");

        df.driver().findElement(By.id("loginButton")).click();

        df.waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));
        assertTrue(df.driver().findElement(By.id("logoutBtn")).isDisplayed());
    }
    
    @When("the user navigates to the trends page")
    public void userNavsTrendsPage() {
        var trendsLink = df.waitFor().until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("#navItems [data-page='trends']"))
        );

        ((org.openqa.selenium.JavascriptExecutor) df.driver())
                .executeScript("arguments[0].click();", trendsLink);

        df.waitFor().until(
            ExpectedConditions.visibilityOfElementLocated(By.id("trendSummaryLabel"))
        );
    }
    
    @Then("a graph should be displayed")
    public void graphIsDisplayed() {
        df.waitFor().until(
            ExpectedConditions.visibilityOfElementLocated(By.id("distanceChart"))
        );

        assertTrue(
            df.driver().findElement(By.id("distanceChart")).isDisplayed()
        );
    }
}
