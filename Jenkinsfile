pipeline {
    agent any

    environment {
        DOCKER_HOST = 'tcp://host.docker.internal:2375'
            DOCKER_API_VERSION = "1.43"
    }

    stages {
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew test'
                }
            }
            post {
                always {
                    junit testResults: '**/test-results/test/*.xml', allowEmptyResults: true
                    recordCoverage tools: [[parser: 'JACOCO', pattern: '**/jacocoTestReport.xml']]
                }
            }
        }

        stage('Build Frontend') {
            steps {
                nodejs('24.11.1') {
                    dir('frontend') {
                        sh 'npm install'
                        sh 'npm run lint:html'
                    }
                }
            }
        }

        stage('Sonar Backend') {
            steps {
                withCredentials([string(credentialsId: 'Sonarqube-Backend', variable: 'TOKEN')]) {
                    dir('backend') {
                        sh './gradlew sonar -Dsonar.projectKey=gallomor-Backend -Dsonar.projectName="gallomor-Backend" -Dsonar.host.url=http://sonarqube:9000 -Dsonar.token=$TOKEN'
                    }
                }
            }
        }

        stage('Sonar Frontend') {
            steps {
                withCredentials([string(credentialsId: 'Sonarqube-Frontend', variable: 'TOKEN')]) {
                    nodejs('24.11.1') {
                        dir('frontend') {
                            sh 'npx sonar-scanner -Dsonar.host.url=http://sonarqube:9000 -Dsonar.projectKey=gallomor-Frontend -Dsonar.projectName="gallomor-Frontend" -Dsonar.token=$TOKEN'
                        }
                    }
                }
            }
        }

        stage('Docker') {
            steps {
                sh '''
                    export DOCKER_HOST=tcp://host.docker.internal:2375
                    docker build -t gallomor/devopsdemo .
                '''
            }
        }
    }
}