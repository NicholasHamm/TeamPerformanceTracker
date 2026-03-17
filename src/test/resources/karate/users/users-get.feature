Feature: User Management GET API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/admin-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario: Get all users
    Given path 'api/users'
    When method get
    Then status 200
    And match response == '#[]'
    And match each response contains
    """
    {
      id: '#number',
      username: '#string',
      firstName: '#string',
      lastName: '#string',
      role: '#string'
    }
    """

  Scenario: Get user by username
    Given path 'api/users', 'admin'
    When method get
    Then status 200
    And match response contains
    """
    {
      id: '#number',
      username: 'admin',
      firstName: '#string',
      lastName: '#string',
      role: 'ADMIN'
    }
    """
    