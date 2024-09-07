pipeline {
    agent any
    environment {
        TESTCONTAINERS_HOST_OVERRIDE = 'tcp://docker:2376'
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
            script {
                withCredentials([string(credentialsId: 'e81a4652-c590-4ace-85ba-97d9173cd80c', variable: 'GITHUB_TOKEN')]) {
                    echo "Using GitHub Token: ${env.GITHUB_TOKEN}"  // Debug line
                    githubNotify credentialsId: env.GITHUB_TOKEN, context: 'Jenkins', status: 'SUCCESS'
                }
                echo 'Build was successful!'
            }
        }
        failure {
            script {
                withCredentials([string(credentialsId: 'e81a4652-c590-4ace-85ba-97d9173cd80c', variable: 'GITHUB_TOKEN')]) {
                    echo "Using GitHub Token: ${env.GITHUB_TOKEN}"  // Debug line
                    githubNotify credentialsId: env.GITHUB_TOKEN, context: 'Jenkins', status: 'FAILURE'
                }
                echo 'Build failed!'
            }
        }
    }
}
