@Library('jenkins-pipeline-shared-libraries')_

def submarineBomScmCustom = null
def submarineExamplesScmCustom = null

pipeline {
    agent {
        label 'kie-rhel7'
    }
    tools {
        maven 'kie-maven-3.5.4'
        jdk 'kie-jdk1.8'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'printenv'
                script {
                    try {
                        submarineBomScmCustom = githubscm.resolveRepository('submarine-bom', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", true)
                    } catch (Exception ex) {
                        echo "Branch $CHANGE_BRANCH from repository submarine-bom not found in $CHANGE_AUTHOR organisation."
                    }

                    try {
                        submarineExamplesScmCustom = githubscm.resolveRepository('submarine-examples', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", true)
                    } catch (Exception ex) {
                        echo "Branch $CHANGE_BRANCH from repository submarine-examples not found in $CHANGE_AUTHOR organisation."
                    }
                }
            }
        }
        stage('Build submarine-bom') {
            steps {
                timeout(15) {
                    dir("submarine-bom") {
                        script {
                            if (submarineBomScmCustom != null) {
                                checkout submarineBomScmCustom
                            } else {
                                checkout(githubscm.resolveRepository('submarine-bom', 'kiegroup', "$CHANGE_TARGET", false))
                            }
                        }
                        sh 'mvn clean install -DskipTests'
                    }
                }
            }
        }
        stage('Build submarine-runtimes') {
            steps {
                timeout(30) {
                    sh 'mvn clean install'
                }
            }
        }
        stage('Build submarine-examples') {
            steps {
                timeout(30) {
                    dir("submarine-examples") {
                        script {
                            if (submarineExamplesScmCustom != null) {
                                checkout submarineExamplesScmCustom
                            } else {
                                checkout(githubscm.resolveRepository('submarine-examples', 'kiegroup', "$CHANGE_TARGET", false))
                            }
                        }
                        sh 'mvn clean install'
                    }
                }
            }
        }
        stage('Publish test results') {
            steps {
                junit '**/target/surefire-reports/**/*.xml'
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
            cleanWs()
        }
    }
}