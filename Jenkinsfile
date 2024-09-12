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
                    withCredentials([
                        sshUserPrivateKey(credentialsId: 'REMOTE_SERVER_SSH_KEY', keyFileVariable: 'REMOTE_SERVER_SSH_KEY'),
                        usernamePassword(credentialsId: 'ADMIN_CREDENTIALS_ID', usernameVariable: 'ADMIN_EMAIL', passwordVariable: 'ADMIN_PASSWORD'),
                        usernamePassword(credentialsId: 'DB_CREDENTIALS_ID', usernameVariable: 'POSTGRES_USER', passwordVariable: 'POSTGRES_PASSWORD'),
                        string(credentialsId: 'REMOTE_SERVER_USER', variable: 'REMOTE_SERVER_USER'),
                        string(credentialsId: 'REMOTE_SERVER_IP', variable: 'REMOTE_SERVER_IP')
                      ]
                    ) {
                        sh '''
                            export REMOTE_SERVER_USER=${REMOTE_SERVER_USER}
                            export REMOTE_SERVER_IP=${REMOTE_SERVER_IP}
                            export ADMIN_EMAIL=${ADMIN_EMAIL}
                            export ADMIN_PASSWORD=${ADMIN_PASSWORD}
                            export POSTGRES_USER=${POSTGRES_USER}
                            export POSTGRES_PASSWORD=${POSTGRES_PASSWORD}

                            ssh -o StrictHostKeyChecking=no -i ${REMOTE_SERVER_SSH_KEY} $REMOTE_SERVER_USER@$REMOTE_SERVER_IP << 'EOF'
                            set -e
                            docker-compose -f /home/$REMOTE_SERVER_USER/recipe_management_system/docker-compose-prod.yml down
                            docker-compose -f /home/$REMOTE_SERVER_USER/recipe_management_system/docker-compose-prod.yml up --build -d
                            EOF
                        '''

                        // Perform a health check with retries

                        def retryCount = 0
                        def maxRetries = 5
                        def isHealthy = false
                        def checkStatus
                        def curlCommand = "curl -s -o /dev/null -w '%{http_code}' http://${REMOTE_SERVER_IP}:8081/actuator/health"

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
                            error "Deployment failed!"
                        }
                        echo "Deployment succeeded!"
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
