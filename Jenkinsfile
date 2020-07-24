@Library('jenkins-pipeline-shared-libraries')_

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
        timeout(time: 120, unit: 'MINUTES')
    }
    environment {
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
    }
    stages {
        stage('Initialize') {
            steps {
                sh "export XAUTHORITY=$HOME/.Xauthority"
                sh "chmod 600 $HOME/.vnc/passwd"
                
                checkoutRepo("kogito-runtimes")
                checkoutRepo("kogito-apps")
                checkoutRepo("kogito-examples")
                checkoutRepo("kogito-examples", "kogito-examples-persistence")
                checkoutRepo("kogito-examples", "kogito-examples-events")
            }
        }
        stage('Build kogito-runtimes') {
            steps {
                mavenCleanInstall("kogito-runtimes")
            }
        }
        stage('Build kogito-apps') {
            steps {
                mavenCleanInstall("kogito-apps", false, ["run-code-coverage"])
                runMaven("validate", "kogito-apps", false, ["sonarcloud-analysis"], "-e -nsu")
            }
        }
        stage('Build kogito-examples') {
            steps {
                mavenCleanInstall("kogito-examples")
            }
        }
        stage('Build kogito-examples with persistence') {
            steps {
                mavenCleanInstall("kogito-examples-persistence", false, ["persistence"])
            }
        }
        stage('Build kogito-examples with events') {
            steps {
                mavenCleanInstall("kogito-examples-events", false, ["events"])
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

void mavenCleanInstall(String directory, boolean skipTests = false, List profiles = [], String extraArgs = "") {
    runMaven("clean install", directory, skipTests, profiles, extraArgs)
}

void runMaven(String command, String directory, boolean skipTests = false, List profiles = [], String extraArgs = "") {
    mvnCmd = command
    if(profiles.size() > 0){
        mvnCmd += " -P${profiles.join(',')}"
    }
    if(extraArgs != ""){
        mvnCmd += " ${extraArgs}"
    }
    dir(directory) {
        maven.runMavenWithSubmarineSettings(mvnCmd, skipTests)
    }
}