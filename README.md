# TeamPerformanceTracker
Team Performance Tracker is a web-based application for managing football training sessions and player performance data. It supports roles such as Admin, Coach, and Player, allowing session creation, performance upload, and analysis of individual and team metrics.

## Features
User Management
  * Admin can create users
  * Admin can assign roles

Training Sessions
  - Coach can create sessions
  - Sessions include type, date, duration
  - Coach can delete training sessions
  - Coach can upload player performance data for a session
  - Coach can update and delete player performance data

Player Performance
  - Upload performance metrics
  - View trends and charts
  
### Technologies Used
- Java 21
- Spring Boot
- Maven

- Spring Data JPA
- MySQL

- HTML, CSS, JavaScript
- jQuery
- Bootstrap

- JUnit, Mockito, Karate, Selenium

- Jenkins

## How to Run
##### Prerequisites 
- Java 21 installed
- Maven installed
- MySQL installed and running

##### Clone/Download the repository
git clone https://github.com/NicholasHamm/TeamPerformanceTracker 
cd TeamPerformanceTracker

`mvn clean package`

`mvn spring-boot:run`

Or

Run from inside Eclipse/Intellij as a Springboot Application

##### Application Properties
The application should be accessible from http://localhost:8082 by default

##### MySQL
Once MySQL is running, when the application should automatically create a new schema named 'tptdb', consisting of 3 tables: users, training_sessions, player_performance

##### Seed data users (username/password)
* Admin: admin / admin
* Coach: coach1 / coach1
* Player: player1 / player1

##### Run Tests
- Unit: 	

		`mvn test`

- API (Karate):	

		`mvn verify`
		`mvn -Papi-tests verify`
		
- UI (Selenium):		

		`mvn -Pui-tests verify`

The project is part of a Cross-Modular Assessment between Web Technologies and Continuous Build &amp; Delivery.