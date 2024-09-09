pipeline {
    agent any
    environment {
        TESTCONTAINERS_HOST_OVERRIDE = 'tcp://docker:2376'
        GITHUB_CREDENTIALS_ID = 'recipe_management_system_credentials'
        DOCKER_CREDENTIALS_ID = 'dockerhub'
    }
    stages {
        stage('Build') {
            steps {
                script {
                    sh 'docker compose -f docker-compose-jenkins.yml up recipe_management_system_build --build --abort-on-container-exit'
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    sh 'docker compose -f docker-compose-jenkins.yml up recipe_management_system_test --build --abort-on-container-exit'
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('', env.DOCKER_CREDENTIALS_ID) {
                        def appImage = docker.build('joaopdrgoncalves/recipe_management_system:latest')
                        appImage.push()
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    sh 'docker compose -f docker-compose-prod.yml down && docker compose -f docker-compose-prod.yml up --build -d'

                    // Perform a health check with retries

                    def retryCount = 0
                    def maxRetries = 5
                    def isHealthy = false
                    def checkStatus
                    def curlCommand = 'curl -s -o /dev/null -w "%{http_code}" http://your-app-url/health'

                    while (retryCount < maxRetries && !isHealthy) {
                        try {
                            checkStatus = sh(script: curlCommand, returnStdout: true).trim()
                            if (checkStatus == '200') {
                                isHealthy = true
                            } else {
                                retryCount++
                                echo "Health check failed with status code ${checkStatus}. Retrying..."
                            }
                        } catch (Exception e) {
                            retryCount++
                            echo "Health check failed with error: ${e.message}. Retrying..."
                        }
                        sleep(time: 30, unit: 'SECONDS')
                    }

                    if (!isHealthy) {
                        error "Deployment failed! Health check returned status code ${checkStatus} after ${maxRetries} retries"
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                sh 'docker compose -f docker-compose-jenkins.yml down -v'
            }
        }
        success {
            githubNotify credentialsId: env.GITHUB_CREDENTIALS_ID, context: 'Jenkins', status: 'SUCCESS', description: 'Build succeeded'
        }
        failure {
            githubNotify credentialsId: env.GITHUB_CREDENTIALS_ID, context: 'Jenkins', status: 'FAILURE', description: 'Build failed'
        }
    }
}
