Feature: Admin authentication helper

  Scenario: Get admin JWT token
    Given url baseUrl
    And path 'auth/login'
    And request
    """
    {
      "username": "admin",
      "password": "admin"
    }
    """
    When method post
    Then status 200
    And match response.token == '#string'
    * def authToken = response.token