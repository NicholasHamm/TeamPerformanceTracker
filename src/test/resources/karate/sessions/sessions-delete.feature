Feature: Training Sessions DELETE API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * def token = login.authToken
    * match token != null
    * configure headers = { Authorization: '#("Bearer " + token)' }

  Scenario: Delete training session successfully
    Given path 'api', 'sessions'
    And request
    """
    {
      "datetime": "2026-02-09T18:00:00",
      "type": "CONDITIONING",
      "duration": 60
    }
    """
    When method post
    Then status 200
    * print 'CREATE RESPONSE =', response
    * def sessionId = response.id
    * match sessionId != null

    Given path 'api', 'sessions', sessionId
    When method delete
    Then status 200
    * print 'DELETE RESPONSE =', response

    Given path 'api', 'sessions', sessionId
    When method get
    Then status 404