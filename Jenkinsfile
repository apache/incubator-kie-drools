@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label 'kie-rhel7&&kie-mem8g'
    }
    tools {
        nodejs "nodejs-11.0.0"
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 90, unit: 'MINUTES')
    }
    environment {
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
    }
    stages {
        stage('Prepare') {
            steps {
                sh "npm install -g yarn --registry=${NPM_REGISTRY_URL}"
                sh "yarn config set registry ${NPM_REGISTRY_URL}"
                sh "export XAUTHORITY=$HOME/.Xauthority"
                sh "chmod 600 $HOME/.vnc/passwd"
            }
        }
        stage('Build kogito-apps') {
            steps {
                dir("kogito-apps") {
                    script {
                        githubscm.checkoutIfExists('kogito-apps', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET")
                        wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
                            sh('yarn run init && yarn build:prod')
                        }
                    }
                }
            }
        }
    }
    post {
        unstable {
            script {
                mailer.sendEmailFailure()
            }
        }
        failure {
            script {
                mailer.sendEmailFailure()
            }
        }
        always {
            junit '**/**/junit.xml'
            cleanWs()
        }
    }
}
