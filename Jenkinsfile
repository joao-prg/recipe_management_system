pipeline {
    agent any
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
            //githubNotify context: 'Jenkins', status: 'SUCCESS'
            echo 'Build was successful!'
        }
        failure {
            //githubNotify context: 'Jenkins', status: 'FAILURE'
            echo 'Build failed!'
        }
    }
}
