Feature: JWT Authentication

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'

  Scenario: Admin can log in and receive a JWT
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "#(adminUser)",
        "password": "#(adminPass)"
      }
      """
    When method post
    Then status 200
    And match response.token == '#string'

  Scenario: Coach can log in and receive a JWT
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "#(coachUser)",
        "password": "#(coachPass)"
      }
      """
    When method post
    Then status 200
    And match response.token == '#string'

  Scenario: Player can log in and receive a JWT
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "#(playerUser)",
        "password": "#(playerPass)"
      }
      """
    When method post
    Then status 200
    And match response.token == '#string'

  Scenario: Wrong password
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "#(adminUser)",
        "password": "wrongpassword"
      }
      """
    When method post
    Then status 401
    And match response.error == 'Unauthorized'

  Scenario: Unknown username
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "unknown",
        "password": "#(adminPass)"
      }
      """
    When method post
    Then status 401
    And match response.error == 'Unauthorized'

  Scenario: Empty username
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "",
        "password": "#(adminPass)"
      }
      """
    When method post
    Then status 401

  Scenario: Empty password
    Given path 'api', 'auth', 'login'
    And request
      """
      {
        "username": "#(adminUser)",
        "password": ""
      }
      """
    When method post
    Then status 401