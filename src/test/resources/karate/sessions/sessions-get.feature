Feature: Training Sessions GET API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario: Get all training sessions
    Given path 'api/sessions'
    When method get
    Then status 200
    And match response == '#[]'

  Scenario: Get training session by id
    Given path 'api/sessions', 1
    When method get
    Then status 200
    And match response contains
    """
    {
      id: '#number',
      datetime: '#string',
      type: '#string',
      duration: '#number'
    }
    """
    And match response.players == '#[]'

  Scenario: Get training session by id returns 404 when not found
    Given path 'api/sessions', 99999
    When method get
    Then status 404