@Library('jenkins-pipeline-shared-libraries')_

import org.kie.jenkins.MavenCommand

changeAuthor = env.ghprbPullAuthorLogin ?: CHANGE_AUTHOR
changeBranch = env.ghprbSourceBranch ?: CHANGE_BRANCH
changeTarget = env.ghprbTargetBranch ?: CHANGE_TARGET

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem16g'
    }
    tools {
        maven 'kie-maven-3.6.2'
        jdk 'kie-jdk11'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '10', numToKeepStr: '')
        timeout(time: 360, unit: 'MINUTES')
    }
    environment {
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'export XAUTHORITY=$HOME/.Xauthority'
                sh 'chmod 600 $HOME/.vnc/passwd'
                
                checkoutRepo('kogito-runtimes')
                checkoutOptaplannerRepo()
                checkoutRepo('kogito-apps')
                checkoutRepo('kogito-examples')
                checkoutRepo('kogito-examples', 'kogito-examples-persistence')
                checkoutRepo('kogito-examples', 'kogito-examples-events')
            }
        }
        stage('Build Runtimes') {
            steps {
                script {
                    getMavenCommand('kogito-runtimes')
                        .skipTests(true)
                        .withProperty('skipITs', true)
                        .run('clean install')
                }
            }
        }
        stage('Build OptaPlanner') {
            steps {
                script {
                    // Skip unnecessary plugins to save time.
                    getMavenCommand('optaplanner')
                        .withProperty('quickly')
                        .run('clean install')
                }
            }
        }
        stage('Build Apps') {
            steps {
                script {
                    getMavenCommand('kogito-apps')
                        .withProfiles(['run-code-coverage'])
                        .run('clean install')

                    getMavenCommand('kogito-apps')
                        .withOptions(['-e', '-nsu'])
                        .withProfiles(['sonarcloud-analysis'])
                        .run('validate')
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'kogito-apps/management-console/target/*-runner.jar, kogito-apps/data-index/data-index-service/target/*-runner.jar, kogito-apps/jobs-service/target/*-runner.jar', fingerprint: true
            junit '**/**/junit.xml'
            junit '**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml'
            cleanWs()
        }
        failure {
            script {
                mailer.sendEmail_failedPR()
            }
        }
        unstable {
            script {
                mailer.sendEmail_unstablePR()
            }
        }
        fixed {
            script {
                mailer.sendEmail_fixedPR()
            }
        }
    }
}

void checkoutRepo(String repo, String dirName=repo) {
    dir(dirName) {
        githubscm.checkoutIfExists(repo, changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
    }
}

void checkoutOptaplannerRepo() {
    String targetBranch = changeTarget
    String [] versionSplit = targetBranch.split("\\.")
    if(versionSplit.length == 3 
        && versionSplit[0].isNumber()
        && versionSplit[1].isNumber()
       && versionSplit[2] == 'x') {
        targetBranch = "${Integer.parseInt(versionSplit[0]) + 7}.${versionSplit[1]}.x"
    } else {
        echo "Cannot parse changeTarget as release branch so going further with current value: ${changeTarget}"
    }
    dir('optaplanner') {
        githubscm.checkoutIfExists('optaplanner', changeAuthor, changeBranch, 'kiegroup', targetBranch, true)
    }
}

MavenCommand getMavenCommand(String directory){
    return new MavenCommand(this, ['-fae'])
                .withSettingsXmlId('kogito_release_settings')
                .inDirectory(directory)
}