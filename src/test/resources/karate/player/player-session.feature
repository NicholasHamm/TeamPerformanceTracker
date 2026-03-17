Feature: Player session history

  Background:
    * url baseUrl

  Scenario: Player can get their sessions
    * def login = callonce read('classpath:karate/helpers/player-token.feature')
    * header Authorization = 'Bearer ' + login.authToken
    Given path 'api/player/sessions'
    When method get
    Then status 200
    And match response == '#[]'
    And match each response contains
    """
    {
      datetime: '#string',
      type: '#string',
      duration: '#number',
      totalDistance: '#number',
      highIntensityDistance: '#number',
      topSpeed: '#number',
      effortRating: '#number'
    }
    """

  Scenario: Unauthenticated user cannot get player
    Given path 'api', 'player', 'sessions'
    When method get
    Then status 401