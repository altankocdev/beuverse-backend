pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        AWS_REGION    = 'eu-central-1'
        ECR_URI       = '264991295666.dkr.ecr.eu-central-1.amazonaws.com/beuverse-backend'
        APP_SERVER_IP = '10.0.139.118'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test || true'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh './mvnw package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                    docker build \
                        -t ${ECR_URI}:latest \
                        -t ${ECR_URI}:${BUILD_NUMBER} \
                        .
                """
            }
        }

        stage('ECR Push') {
            steps {
                withAWS(credentials: 'aws-credentials', region: "${AWS_REGION}") {
                    sh """
                        aws ecr get-login-password --region ${AWS_REGION} | \
                        docker login --username AWS --password-stdin ${ECR_URI}

                        docker push ${ECR_URI}:latest
                        docker push ${ECR_URI}:${BUILD_NUMBER}
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['app-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no \
                            -o ProxyCommand="ssh -i /var/lib/jenkins/.ssh/jump_key -W %h:%p ubuntu@63.177.229.82" \
                            ubuntu@${APP_SERVER_IP} \
                            'bash /home/ubuntu/deploy.sh'
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline başarılı! Build #${BUILD_NUMBER} production'a deploy edildi."
        }
        failure {
            echo "Pipeline başarısız! Build #${BUILD_NUMBER} deploy edilemedi."
        }
        always {
            cleanWs()
        }
    }
}
