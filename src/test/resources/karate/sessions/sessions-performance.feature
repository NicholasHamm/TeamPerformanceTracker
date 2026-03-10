Feature: Training Session Performance API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario: Upload player performance
    Given path 'api/sessions', 1, 'performance'
    And request
    """
    {
      "username": "player1",
      "distance": 9500,
      "topSpeed": 31.2,
      "sprints": 14,
      "heartRate": 155
    }
    """
    When method post
    Then status 200
    And match response.message == 'Player data uploaded successfully'

  Scenario: Get performance for session
    Given path 'api/sessions', 1, 'performance'
    When method get
    Then status 200
    And match response == '#[]'