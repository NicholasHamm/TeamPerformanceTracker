Feature: Upload Player Performance API with invalid inputs

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/helpers/coach-token.feature')
    * header Authorization = 'Bearer ' + login.authToken

  Scenario Outline: Upload player performance invalid inputs for a session
    Given path 'api/sessions/1/performance'
    And request
  """
  {
    "playerId": #(playerId),
    "totalDistance": #(totalDistance),
    "highIntensityDistance": #(highIntensityDistance),
    "topSpeed": #(topSpeed),
    "effortRating": #(effortRating)
  }
  """
    When method post
    Then status <status>

    * if (<status> == 400) karate.match(response.error, '#string')

    Examples:
      | playerId | totalDistance | highIntensityDistance | topSpeed | effortRating | status |
      | null     | 5000          | 800                   | 12.2     | 7            | 400    |
      | 3        | -10           | 800                   | 12.2     | 7            | 400    |
      | 3        | 21000         | 800                   | 12.2     | 7            | 400    |
      | 3        | 5000          | 12000                 | 12.2     | 7            | 400    |
      | 3        | 5000          | 800                   | 16       | 7            | 400    |
      | 3        | 5000          | 800                   | 12.2     | 11           | 400    |