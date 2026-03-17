Feature: Session analytics

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * header Authorization = 'Bearer ' + login.authToken
    
  Scenario: Coach can get session averages
    Given path 'api', 'sessions', 2, 'averages'
    When method get
    Then status 200
    And match response ==
    """
    {
      sessionId: '#number',
      averageTotalDistance: '#number',
      averageDistancePerMin: '#number',
      averageHighIntensityDistance: '#number',
      averageTopSpeed: '#number',
      averageEffortRating: '#number'
    }
    """

  Scenario: Coach can get player metric breakdown
    Given path 'api', 'sessions', 2, 'metrics', 'totalDistance'
    When method get
    Then status 200
    And match response == '#[]'
    And match each response contains
    """
    {
      playerName: '#string',
      value: '#number'
    }
    """

  Scenario: Invalid metric is rejected
    Given path 'api', 'sessions', 1, 'metrics', 'invalidMetric'
    When method get
    Then status 400