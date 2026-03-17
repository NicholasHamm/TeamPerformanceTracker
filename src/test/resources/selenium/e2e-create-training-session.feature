Feature: Coach creates training session

  Scenario: Coach creates a training session successfully
    Given a coach user is logged in
    When the coach navigates to the create session form
    And the coach enters session date and time "2026-03-20T18:00"
    And the coach selects training type "SPEED"
    And the coach enters duration 60
    And the coach submits the session form
    Then a session success message should be displayed