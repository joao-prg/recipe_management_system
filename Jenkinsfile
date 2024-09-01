pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                script {
                    sh 'docker-compose -f docker-compose-jenkins.yml up recipe_management_system_build --build'
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    sh 'docker-compose -f docker-compose-jenkins.yml up recipe_management_system_test --build'
                }
            }
        }
    }
    post {
        always {
            script {
                sh 'docker-compose -f docker-compose-jenkins.yml down -v'
            }
        },
        success {
            echo 'Build was successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
