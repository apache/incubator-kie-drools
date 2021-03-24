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
        stage('Build quarkus') {
            when {
                expression { return getQuarkusBranch() }
            }
            steps {
                script {
                    checkoutQuarkusRepo()
                    getMavenCommand('quarkus', false)
                        .withProperty('quickly')
                        .run('clean install')
                }
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
                    getMavenCommand('optaplanner')
                        .withProperty('quickly')
                        .run('clean install')
                }
            }
        }
        stage('Build Apps') {
            steps {
                script {
                    mvnCmd = getMavenCommand('kogito-apps', true, true)
                    if (isNormalPRCheck()) {
                        mvnCmd.withProperty('validate-formatting')
                            .withProfiles(['run-code-coverage'])
                    }
                    mvnCmd.run('clean install')
                }
            }
            post {
                cleanup {
                    script {
                        cleanContainers()
                    }
                }
            }
        }
        stage('Analyze Apps by SonarCloud') {
            when {
                expression { isNormalPRCheck() }
            }
            steps {
                script {
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
            script {
                archiveArtifacts artifacts: '**/target/*-runner.jar, **/target/*-runner', fingerprint: true
                junit '**/**/junit.xml'
                junit '**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml'
            }
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
        cleanup {
            script {
                util.cleanNode('docker')
            }
        }
    }
}

void checkoutRepo(String repo, String dirName=repo) {
    dir(dirName) {
        githubscm.checkoutIfExists(repo, changeAuthor, changeBranch, 'kiegroup', changeTarget, true)
    }
}

void checkoutQuarkusRepo() {
    dir('quarkus') {
        checkout(githubscm.resolveRepository('quarkus', 'quarkusio', getQuarkusBranch(), false))
    }
}

void checkoutOptaplannerRepo() {
    String targetBranch = changeTarget
    String [] versionSplit = targetBranch.split("\\.")
    if (versionSplit.length == 3
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

MavenCommand getMavenCommand(String directory, boolean addQuarkusVersion=true, boolean canNative = false) {
    mvnCmd = new MavenCommand(this, ['-fae'])
                .withSettingsXmlId('kogito_release_settings')
                // add timestamp to Maven logs
                .withOptions(['-Dorg.slf4j.simpleLogger.showDateTime=true', '-Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss,SSS'])
                .inDirectory(directory)
    if (addQuarkusVersion && getQuarkusBranch()) {
        mvnCmd.withProperty('version.io.quarkus', '999-SNAPSHOT')
    }
    if (canNative && isNative()) {
        mvnCmd.withProfiles(['native'])
            .withProperty('quarkus.native.container-build', true)
            .withProperty('quarkus.native.container-runtime', 'docker')
            .withProperty('quarkus.profile', 'native') // Added due to https://github.com/quarkusio/quarkus/issues/13341
    }
    return mvnCmd
}

void cleanContainers() {
    cloud.cleanContainersAndImages('docker')
}

boolean isNative() {
    return env['NATIVE'] && env['NATIVE'].toBoolean()
}

String getQuarkusBranch() {
    return env['QUARKUS_BRANCH']
}

boolean isNormalPRCheck() {
    return !(getQuarkusBranch() || isNative())
}
