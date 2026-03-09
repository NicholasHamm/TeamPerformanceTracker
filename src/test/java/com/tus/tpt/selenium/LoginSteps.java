package com.tus.tpt.selenium;

import com.tus.tpt.dao.UserRepository;
import com.tus.tpt.model.User;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private final DriverFactory df;
    private final UserRepository userRepository;

    public LoginSteps(DriverFactory df, UserRepository userRepository) {
        this.df = df;
        this.userRepository = userRepository;
    }

    @Given("an admin user exists in the system")
    public void adminUserExists() {
        Optional<User> admin = userRepository.findByUsernameIgnoreCase("admin");
        assertTrue(admin.isPresent(), "Expected admin user to exist in H2 test database");
    }

    @Given("the user opens the login page")
    public void openLoginPage() {
        df.startDriver();
    }

    @When("the user enters username {string}")
    public void enterUsername(String username) {
        df.driver().findElement(By.id("loginUsername")).clear();
        df.driver().findElement(By.id("loginUsername")).sendKeys(username);
    }

    @When("the user enters password {string}")
    public void enterPassword(String password) {
        df.driver().findElement(By.id("loginPassword")).clear();
        df.driver().findElement(By.id("loginPassword")).sendKeys(password);
    }

    @When("the user submits the login form")
    public void submitLogin() {
        df.driver().findElement(By.id("loginButton")).click();
    }

    @Then("the dashboard should be visible")
    public void dashboardVisible() {
        df.waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));
        assertTrue(df.driver().findElement(By.id("logoutBtn")).isDisplayed());
    }

    @After
    public void tearDown() {
        df.stopDriver();
    }
}