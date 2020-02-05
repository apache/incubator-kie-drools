@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label 'kogito-static || kie-rhel7'
    }
    tools {
        maven 'kie-maven-3.5.4'
        jdk 'kie-jdk1.8'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 90, unit: 'MINUTES')
    }
    environment {
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
        MAVEN_OPTS = '-Xms512m -Xmx3g'
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'printenv'
            }
        }
        stage('Build kogito-bom') {
            steps {
                dir("kogito-bom") {
                    script {
                        githubscm.checkoutIfExists('kogito-bom', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET")
                        maven.runMavenWithSubmarineSettings('clean install', true)
                    }
                }
            }
        }
        stage('Build kogito-runtimes') {
            steps {
                script {
                    maven.runMavenWithSubmarineSettings('clean install -Prun-code-coverage', false)
                }
            }
        }
        stage('Build kogito-cloud') {
            steps {
                dir("kogito-cloud") {
                    script {
                        githubscm.checkoutIfExists('kogito-cloud', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET")
                        maven.runMavenWithSubmarineSettings('clean install', false)
                    }
                }
            }
        }
        stage('Build kogito-examples') {
            steps {
                dir("kogito-examples") {
                    script {
                        githubscm.checkoutIfExists('kogito-examples', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET")
                        maven.runMavenWithSubmarineSettings('clean install', false)
                    }
                }
                // Use a separate dir for persistence to not overwrite the test results
                dir("kogito-examples-persistence") {
                    script {
                        githubscm.checkoutIfExists('kogito-examples', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET")
                        // Don't run with tests so far, see: https://github.com/quarkusio/quarkus/issues/6885
                        maven.runMavenWithSubmarineSettings('clean install -Ppersistence', true)
                    }
                }
            }
        }
        stage('Analyze kogito-runtimes') {
            steps {
                script {
                    maven.runMavenWithSubmarineSettings('-e -nsu generate-resources -Psonarcloud-analysis', false)
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
            junit '**/target/surefire-reports/**/*.xml'
            cleanWs()
        }
    }
}
