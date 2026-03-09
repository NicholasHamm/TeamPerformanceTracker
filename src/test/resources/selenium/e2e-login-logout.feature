Feature: Login

  Scenario: Admin logs into the application
    Given an admin user exists in the system
    And the user opens the login page
    When the user enters username "admin"
    And the user enters password "admin"
    And the user submits the login form
    Then the dashboard should be visible