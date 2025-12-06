pipeline {
    agent any

    tools {
        maven 'Maven 3'
    }

    environment {
        // Укажите ваше имя пользователя и название репозитория
        DOCKER_IMAGE = 'mikhailyuzhakov/spring-to-do'
        DOCKER_CREDS_ID = 'docker-hub-creds'
    }

    stages {
        // Этап 1: Сборка и Тесты (для всех веток и PR)
        stage('Build & Test') {
            steps {
                echo 'Running Maven Build and Tests...'
                sh 'mvn clean install'
            }
        }

        // Этап 2: Сборка и Пуш Docker образа (ТОЛЬКО для master)
        stage('Build & Push Docker Image') {
            when {
                allOf {
                    branch 'master'
                    not { changeRequest() }
                }
            }
            steps {
                script {
                    echo 'Building Docker Image...'
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDS_ID}") {
                        def customImage = docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")

                        echo 'Pushing Docker Image...'
                        customImage.push()
                        customImage.push('latest')
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}