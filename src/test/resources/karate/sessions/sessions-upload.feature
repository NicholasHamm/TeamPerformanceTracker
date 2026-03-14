Feature: Training Session upload performance data API

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
        "highIntensityDistance": 800.0,
        "topSpeed": 12.2,
        "effortRating": 7
      }
    """
    When method post
    Then status 200
    And match response ==
    """
      {
        playerId: #number,
        playerName: #string,
        totalDistance: #number,
        distancePerMin: #number,
        highIntensityDistance: #number,
        topSpeed: #number,
        effortRating: #number
      }
    """

  Scenario: Upload duplicate player performance
    Given path 'api/sessions', 1, 'performance'
    And request
    """
      {
        "playerId": 3,
        "totalDistance": 5000.0,
        "highIntensityDistance": 800.0,
        "topSpeed": 12.2,
        "effortRating": 7
      }
    """
    When method post
    Then status 400
    And match response contains
    """
      {
        "error": "Data already exists for this player in this session"
      }
    """

  Scenario: Get performance for session
    Given path 'api/sessions', 1, 'performance'
    When method get
    Then status 200
    And match response ==
    """
    {
      sessionId: '#number',
      performances: '#[]'
    }
    """