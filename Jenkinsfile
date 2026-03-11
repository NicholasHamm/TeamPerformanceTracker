pipeline {
    agent any

    tools {
        maven 'Maven_3'
        // jdk 'JDK21'
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

        stage('Build and Unit Tests') {
            steps {
                bat 'mvn -B -V clean test'
            }
        }

        stage('UI Tests (Karate & Selenium)') {
            when {
                expression { return params.RUN_UI_TESTS }
            }
            steps {
                bat 'mvn -B verify -DskipUnitTests=true'
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
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
    	}
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/screenshots/**'
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