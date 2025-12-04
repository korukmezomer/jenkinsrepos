pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'otomasyonogrenci-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        POSTGRES_HOST = 'postgres'
        POSTGRES_PORT = '5432'
        POSTGRES_DB = 'otomasyonogrenci'
        POSTGRES_USER = 'postgres'
        POSTGRES_PASSWORD = 'postgres'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                sh '''
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                sh '''
                    ./gradlew test --tests "*Test" --tests "*RepositoryTest" --tests "*ServiceTest"
                '''
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo 'Running integration tests...'
                sh '''
                    ./gradlew test --tests "*IntegrationTest" --tests "*ControllerIntegrationTest"
                '''
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                echo 'Generating code coverage report...'
                sh '''
                    ./gradlew jacocoTestReport || true
                '''
            }
            post {
                always {
                    publishHTML([
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ])
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh '''
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                '''
            }
        }

        stage('Docker Test') {
            steps {
                echo 'Testing Docker image...'
                sh '''
                    docker run -d --name test-container \
                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} \
                        -e SPRING_DATASOURCE_USERNAME=${POSTGRES_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD} \
                        -e SERVER_PORT=8082 \
                        -p 8082:8082 \
                        ${DOCKER_IMAGE}:${DOCKER_TAG} || true
                    
                    sleep 30
                    
                    # Health check
                    curl -f http://localhost:8082/actuator/health || exit 1
                    
                    # Cleanup
                    docker stop test-container || true
                    docker rm test-container || true
                '''
            }
        }

        stage('Deploy') {
            when {
                branch 'main' || branch 'master'
            }
            steps {
                echo 'Deploying application...'
                sh '''
                    # Stop and remove existing container
                    docker stop ${DOCKER_IMAGE} || true
                    docker rm ${DOCKER_IMAGE} || true
                    
                    # Run new container
                    docker run -d --name ${DOCKER_IMAGE} \
                        --network otomasyon-network \
                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} \
                        -e SPRING_DATASOURCE_USERNAME=${POSTGRES_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD} \
                        -e SERVER_PORT=8082 \
                        -p 8082:8082 \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            emailext (
                subject: "✅ Build Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "Build completed successfully.\n\nJob: ${env.JOB_NAME}\nBuild Number: ${env.BUILD_NUMBER}\nBranch: ${env.BRANCH_NAME}\n\nView build: ${env.BUILD_URL}",
                to: "${env.CHANGE_AUTHOR_EMAIL}"
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext (
                subject: "❌ Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "Build failed.\n\nJob: ${env.JOB_NAME}\nBuild Number: ${env.BUILD_NUMBER}\nBranch: ${env.BRANCH_NAME}\n\nView build: ${env.BUILD_URL}",
                to: "${env.CHANGE_AUTHOR_EMAIL}"
            )
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}

