Feature: Training session find all available players API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario: Get available players for a session
    Given path 'api/sessions', 1, 'available'
    When method get
    Then status 200
    And match response == '#[]'