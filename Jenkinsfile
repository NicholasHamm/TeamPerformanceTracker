pipeline {
    agent any

    tools {
        maven 'Maven_3'
    }

    parameters {
        booleanParam(
            name: 'RUN_UI_TESTS',
            defaultValue: true,
            description: 'Run Selenium UI tests'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn -B test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('API Tests (Karate)') {
		    steps {
		        bat 'mvn -B -Papi-tests verify'
		    }
		    post {
		        always {
		            junit allowEmptyResults: true, testResults: 'target/failsafe-reports-api/*.xml'
		            archiveArtifacts artifacts: 'target/karate-reports/**', allowEmptyArchive: true
		        }
		    }
		}

        stage('UI Tests (Selenium)') {
		    when {
		        expression { return params.RUN_UI_TESTS }
		    }
		    steps {
		        bat 'mvn -B -Pui-tests verify'
		    }
		    post {
		        always {
		            junit allowEmptyResults: true, testResults: 'target/failsafe-reports-ui/*.xml'
		            archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
		        }
		    }
		}
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/**/*', fingerprint: true

            publishHTML(target: [
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Code Coverage',
                keepAll: true,
                alwaysLinkToLastBuild: true
            ])

            publishHTML(target: [
                reportDir: 'target/karate-reports',
                reportFiles: 'karate-summary.html',
                reportName: 'Karate Summary',
                keepAll: true,
                alwaysLinkToLastBuild: true
            ])

            publishHTML(target: [
                reportDir: 'target/cucumber-reports',
                reportFiles: 'selenium.html',
                reportName: 'Selenium Cucumber Report',
                keepAll: true,
                alwaysLinkToLastBuild: true
            ])
        }
    }
}