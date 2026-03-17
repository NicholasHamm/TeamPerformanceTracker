package com.tus.tpt.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateTrainingSessionSteps {

    private final DriverFactory df;
    
    public CreateTrainingSessionSteps(DriverFactory df) {
        this.df = df;
    }

    @Given("a coach user is logged in")
    public void coachUserIsLoggedIn() {
        df.startDriver();

        df.driver().findElement(By.id("loginUsername")).clear();
        df.driver().findElement(By.id("loginUsername")).sendKeys("coach1");

        df.driver().findElement(By.id("loginPassword")).clear();
        df.driver().findElement(By.id("loginPassword")).sendKeys("coach1");

        df.driver().findElement(By.id("loginButton")).click();

        df.waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));
        assertTrue(df.driver().findElement(By.id("logoutBtn")).isDisplayed());
    }
    
    @When("the coach navigates to the create session form")
    public void navigateToCreateSessionForm() {
    	var trendsLink = df.waitFor().until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("#navItems [data-page='create-session']"))
        );

        ((org.openqa.selenium.JavascriptExecutor) df.driver())
                .executeScript("arguments[0].click();", trendsLink);

        df.waitFor().until(
            ExpectedConditions.visibilityOfElementLocated(By.id("sessionModal"))
        );
    }
    
    @When("the coach enters session date and time {string}")
    public void enterSessionDateTime(String dateTime) {
        var input = df.driver().findElement(By.id("createDatetime"));

        ((org.openqa.selenium.JavascriptExecutor) df.driver()).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            input,
            dateTime
        );
    }
    
    @When("the coach selects training type {string}")
    public void selectTrainingType(String type) {
        Select dropdown = new Select(df.driver().findElement(By.id("createType")));
        dropdown.selectByVisibleText(type);
    }
    
    @When("the coach enters duration {int}")
    public void enterDuration(Integer duration) {
        var input = df.driver().findElement(By.id("createDuration"));

        ((org.openqa.selenium.JavascriptExecutor) df.driver()).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            input,
            duration.toString()
        );

        System.out.println("createDuration value = " +
            df.driver().findElement(By.id("createDuration")).getAttribute("value"));
    }
    
    @When("the coach submits the session form")
    public void submitSessionForm() {
        df.waitFor().until(
                ExpectedConditions.visibilityOfElementLocated(By.id("sessionModal"))
        );

        var saveButton = df.waitFor().until(
                ExpectedConditions.elementToBeClickable(By.id("saveSessionBtn"))
        );

        ((org.openqa.selenium.JavascriptExecutor) df.driver())
                .executeScript("arguments[0].click();", saveButton);
    }

    @Then("a session success message should be displayed")
    public void successMessageDisplayed() {
        df.waitFor().until(
            ExpectedConditions.invisibilityOfElementLocated(By.id("sessionModal"))
        );

        df.waitFor().until(
            ExpectedConditions.visibilityOfElementLocated(By.id("createSessionSuccess"))
        );

        assertTrue(df.driver().findElement(By.id("createSessionSuccess")).isDisplayed());
    }
    
    @After
    public void tearDown() {
        df.stopDriver();
    }
}