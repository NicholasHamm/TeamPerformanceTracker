pipeline {
    agent any

    tools {
        maven 'Maven_3'
    }

    parameters {
        booleanParam(
            name: 'RUN_UI_TESTS',
            defaultValue: false,
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
                bat 'mvn -B clean compile'
            }
        }

        stage('Unit Tests (JUnit)') {
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
                bat 'mvn -B test -Dtest=KarateIT'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/karate-reports/**', allowEmptyArchive: true
                }
            }
        }

        stage('UI Tests (Selenium)') {
            when {
                expression { return params.RUN_UI_TESTS }
            }
            steps {
                bat 'mvn -B verify -Pselenium'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
                    archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonar') {
                    bat 'mvn -B sonar:sonar -Dsonar.projectKey=TeamPerformanceTracker -Dsonar.projectName=TeamPerformanceTracker'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'
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
        }
    }
}