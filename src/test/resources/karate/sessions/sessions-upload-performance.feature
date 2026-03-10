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
        "playerId": 3,
        "totalDistance": 5000.0,
        "distancePerMin": 120.5,
        "highIntensityDistance": 800.0,
        "topSpeed": 31.2,
        "effortRating": 7
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