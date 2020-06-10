@Library('jenkins-pipeline-shared-libraries')_

def changeAuthor = env.ghprbPullAuthorLogin ?: CHANGE_AUTHOR
def changeBranch = env.ghprbSourceBranch ?: CHANGE_BRANCH
def changeTarget = env.ghprbTargetBranch ?: CHANGE_TARGET

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem16g'
    }
    tools {
        maven 'kie-maven-3.6.2'
        jdk 'kie-jdk11'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 120, unit: 'MINUTES')
    }
    environment {
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
    }
    stages {
        stage('build sh script') {
            steps {
                script {
                    mailer.buildLogScriptPR()
                }
            }
        }
        stage('Build kogito-runtimes') {
            steps {
                dir("kogito-runtimes") {
                    script {
                        githubscm.checkoutIfExists('kogito-runtimes', changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
                        maven.runMavenWithSubmarineSettings('clean install -Prun-code-coverage', false)
                        /*
                           The analysis must happen before the other stages as these clone different projects into a root
                           directory of kogito-runtimes and are by mistake incorporated in a test coverage report.
                         */
                        maven.runMavenWithSubmarineSettings('-e -nsu validate -Psonarcloud-analysis', false)
                    }
                }
            }
        }
        stage('Build kogito-apps') {
            steps {
                dir("kogito-apps") {
                    script {
                        githubscm.checkoutIfExists('kogito-apps', changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
                        maven.runMavenWithSubmarineSettings('clean install', false)
                    }
                }
            }
        }
        stage('Build kogito-examples') {
            steps {
                dir("kogito-examples") {
                    script {
                        githubscm.checkoutIfExists('kogito-examples', changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
                        maven.runMavenWithSubmarineSettings('clean install', false)
                    }
                }
                // Use a separate dir for persistence to not overwrite the test results
                dir("kogito-examples-persistence") {
                    script {
                        githubscm.checkoutIfExists('kogito-examples', changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
                        // Don't run with tests so far, see: https://github.com/quarkusio/quarkus/issues/6885
                        maven.runMavenWithSubmarineSettings('clean install -Ppersistence', true)
                    }
                }
            }
        }
    }
    post {
        always {
            sh '$WORKSPACE/trace.sh'
            junit '**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml'
        }
        failure {
            script {
                mailer.sendEmail_failedPR()
            }
            cleanWs()
        }
        unstable {
            script {
                mailer.sendEmail_unstablePR()
            }
            cleanWs()
        }
        fixed {
            script {
                mailer.sendEmail_fixedPR()
            }
            cleanWs()
        }
        success {
            cleanWs()
        }
    }
}
