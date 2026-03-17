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
    //private final TrainingSessionRepository trainingSessionRepository;
    
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
    	df.driver().findElement(By.cssSelector("[data-page='create-session']")).click();

        df.waitFor().until(
            ExpectedConditions.visibilityOfElementLocated(By.id("saveSessionBtn"))
        );
    }
    
    @When("the coach enters session date and time {string}")
    public void enterSessionDateTime(String dateTime) {
        df.driver().findElement(By.id("createDatetime")).clear();
        df.driver().findElement(By.id("createDatetime")).sendKeys(dateTime);
    }
    
    @When("the coach selects training type {string}")
    public void selectTrainingType(String type) {
        Select dropdown = new Select(df.driver().findElement(By.id("createType")));
        dropdown.selectByVisibleText(type);
    }
    
    @When("the coach enters duration {int}")
    public void enterDuration(Integer duration) {
        df.driver().findElement(By.id("createDuration")).clear();
        df.driver().findElement(By.id("createDuration")).sendKeys(duration.toString());
    }
    
    @When("the coach submits the session form")
    public void submitSessionForm() {
        df.driver().findElement(By.id("saveSessionBtn")).click();
    }

    @Then("a session success message should be displayed")
    public void successMessageDisplayed() {
        df.waitFor().until(
            ExpectedConditions.visibilityOfElementLocated(By.id("sessionMsg"))
        );

        assertTrue(df.driver().findElement(By.id("sessionMsg")).isDisplayed());
    }
    
    @After
    public void tearDown() {
        df.stopDriver();
    }
}