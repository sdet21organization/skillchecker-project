pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests with Reports') {
            parallel {
                stage('API Tests') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            bat 'mvn clean test -f api-tests/pom.xml -Dmaven.test.failure.ignore=true'
                        }
                    }
                }
                stage('UI Tests') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            bat 'mvn clean test -f ui-tests/pom.xml -Dmaven.test.failure.ignore=true'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml'

            allure includeProperties: false,
                   jdk: '',
                   results: [
                       [path: 'api-tests/target/allure-results'],
                       [path: 'ui-tests/target/allure-results']
                   ]

            echo 'Reports should be available now'
        }
        success {
            echo 'Build completed successfully'
        }
        failure {
            echo 'Build failed, but reports were still generated'
        }
    }
}