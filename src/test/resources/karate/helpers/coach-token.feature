Feature: Coach authentication helper

  Scenario: Get coach JWT token
    Given url baseUrl
    And path 'auth/login'
    And request
    """
    {
      "username": "coach1",
      "password": "coach1"
    }
    """
    When method post
    Then status 200
    And match response.token == '#string'
    * def authToken = response.token