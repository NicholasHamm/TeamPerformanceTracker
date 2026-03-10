Feature: User Management CREATE API

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/admin-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario: Create new user successfully
    Given path 'api/users'
    And request
    """
    {
      "username": "newUser",
      "password": "Password1",
      "firstName": "New",
      "lastName": "Player",
      "role": PLAYER
    }
    """
    When method post
    Then status 201
    And match response contains
    """
    {
      "username": "newUser",
      "firstName": "New",
      "lastName": "Player",
      "role": PLAYER
    }
    """

  Scenario Outline: Create user examples with invalid inputs
    Given path 'api/users'
    And request
    """
    {
      "username": #(username),
      "password": #(password),
      "firstName": #(firstName),
      "lastName": #(lastName),
      "role": #(role)
    }
    """
    When method post
    Then status <status>
    And match response contains
    """
    {
      "error": "#string"
    }
    """

    Examples:
      | username | password  | firstName | lastName | role  | status |
      | ab       | Password1 | Joe       | Bloggs   | ADMIN | 400    |
      | joe      | pass      | Joe       | Bloggs   | ADMIN | 400    |
      | joe      | Password1 |           | Bloggs   | ADMIN | 400    |
      | joe      | Password1 | Joe       |          | ADMIN | 400    |
      | joe      | Password1 | Joe       | Bloggs   | null  | 400    |
      | admin    | Password1 | System    | Admin    | ADMIN | 400    |
