pipeline {
    agent any
    environment {
        TESTCONTAINERS_HOST_OVERRIDE = 'tcp://docker:2376'
        GITHUB_CREDENTIALS_ID = 'recipe_management_system_credentials'
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
