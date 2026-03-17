Feature: Training Sessions CREATE API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario: Create new training session
    Given path 'api/sessions'
    And request
    """
    {
      "datetime": "2026-03-09T17:00:00",
      "type": "RECOVERY",
      "duration": 60
    }
    """
    When method post
    Then status 200
    And match response contains
    """
    {
      id: '#number',
      datetime: '2026-03-09T17:00:00',
      type: 'RECOVERY',
      duration: 60
    }
    """
    And match response.players == '#[]'

  Scenario Outline: Create session examples
    Given path 'api/sessions'
    And request { datetime: #(datetime), type: #(type), duration: #(duration) }
    When method post
    Then status <status>

    Examples:
      | datetime            | type  | duration | status |
      | null                | SPEED | 60       | 400    |