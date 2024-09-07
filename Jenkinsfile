pipeline {
    agent any
    environment {
        TESTCONTAINERS_HOST_OVERRIDE = 'tcp://docker:2376'
        GITHUB_CREDENTIALS_ID = '6ec04141-5a47-4eca-b934-d3c6ec8105ef'
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
            githubNotify credentialsId: env.GITHUB_CREDENTIALS_ID, context: 'Jenkins', status: 'SUCCESS'
            echo 'Build was successful!'
        }
        failure {
            githubNotify credentialsId: env.GITHUB_CREDENTIALS_ID, context: 'Jenkins', status: 'FAILURE'
            echo 'Build failed!'
        }
    }
}
