pipeline {
    agent { label 'aws' }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    echo "Building and testing project with Maven..."
                    mvn clean test -f api-tests/pom.xml; mvn clean test -f ui-tests/pom.xml
                '''
            }
        }
    }

    post {
        success {
            echo 'Build and tests passed successfully!'
        }
        failure {
            echo 'Build or tests failed.'
        }
        always {
            echo 'Pipeline finished.'
        }
    }
}