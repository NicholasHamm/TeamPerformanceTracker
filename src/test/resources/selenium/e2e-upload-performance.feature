Feature: Coach creates a training session

  Background:
    Given the application is running
    And the coach logs in with valid credentials

  Scenario: Coach creates a new training session successfully
    Given the coach navigates to the session creation page
    When the coach enters a valid date and time
    And the coach selects training type "GYM"
    And the coach enters duration 60
    And the coach submits the session form
    Then a success message is displayed
    And the new session appears in the session list