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
        timeout(time: 600, unit: 'MINUTES')
    }
    environment {
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
    }
    stages {
        stage('Initialize') {
            steps {
                script {
                    mailer.buildLogScriptPR()

                    checkoutRepo('kogito-runtimes')
                    checkoutRepo('kogito-runtimes', 'integration-tests')
                    checkoutOptaplannerRepo()
                    checkoutRepo('kogito-apps')
                    checkoutRepo('kogito-examples')
                    checkoutRepo('kogito-examples', 'kogito-examples-persistence')
                    checkoutRepo('kogito-examples', 'kogito-examples-events')
                }
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
                    mvnCmd = getMavenCommand('kogito-runtimes', true, true)
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
        stage('Analyze Runtimes by SonarCloud') {
            when {
                expression { isNormalPRCheck() }
            }
            steps {
                script {
                    getMavenCommand('kogito-runtimes')
                            .withOptions(['-e', '-nsu'])
                            .withProfiles(['sonarcloud-analysis'])
                            .run('validate')
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
        stage('Check Runtimes integration-tests with persistence') {
            steps {
                script {
                    getMavenCommand('integration-tests', true, true)
                        .withProfiles(['persistence'])
                        .run('clean verify')
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
        stage('Build OptaPlanner') {
            steps {
                script {
                    // Skip unnecessary plugins to save time.
                    getMavenCommand('optaplanner', true, true)
                        .withProperty('enforcer.skip')
                        .withProperty('formatter.skip')
                        .withProperty('impsort.skip')
                        .withProperty('revapi.skip')
                        .run('clean install')
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
        stage('Build Apps') {
            steps {
                script {
                    getMavenCommand('kogito-apps', true, true)
                        .run('clean install')
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
        stage('Build Examples') {
            steps {
                script {
                    getMavenCommand('kogito-examples', true, true)
                        .run('clean install')
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
        stage('Check Examples with persistence') {
            steps {
                script {
                    getMavenCommand('kogito-examples-persistence', true, true)
                        .withProfiles(['persistence'])
                        .run('clean verify')
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
        stage('Check Examples with events') {
            steps {
                script {
                    getMavenCommand('kogito-examples-events', true, true)
                        .withProfiles(['events'])
                        .run('clean verify')
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
    }
    post {
        always {
            script {
                sh '$WORKSPACE/trace.sh'
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
                .withSnapshotsDisabledInSettings()
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

String getQuarkusBranch() {
    return env['QUARKUS_BRANCH']
}

boolean isNative() {
    return env['NATIVE'] && env['NATIVE'].toBoolean()
}

boolean isNormalPRCheck() {
    return !(getQuarkusBranch() || isNative())
}
