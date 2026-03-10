Feature: Authentication

  Background:
    * url baseUrl

  Scenario Outline: Login authentication
    Given path 'auth/login'
    And request
    """
    {
      "username": "<username>",
      "password": "<password>"
    }
    """
    When method post
    Then status <status>

    Examples:
      | username | password  | status |
      | admin    | admin     | 200    |
      | admin    | wrongpass | 401    |
      | fakeuser | password  | 401    |
      | null     | admin     | 401    |
      | admin    | null      | 401    |