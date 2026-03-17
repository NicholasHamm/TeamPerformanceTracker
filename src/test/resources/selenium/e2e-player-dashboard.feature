Feature: Player can view training session trends

  Scenario: Player can view training session trends
    Given a player user is logged in
    When the user navigates to the trends page
    Then a graph should be displayed