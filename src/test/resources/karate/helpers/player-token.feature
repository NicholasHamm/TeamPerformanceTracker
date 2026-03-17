Feature: Player authentication helper

  Scenario: Get player JWT token
    Given url baseUrl
    And path 'auth/login'
    And request
    """
    {
      "username": "player1",
      "password": "player1"
    }
    """
    When method post
    Then status 200
    And match response.token == '#string'
    * def authToken = response.token