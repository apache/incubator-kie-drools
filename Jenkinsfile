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
        timestamps()
        timeout(time: getTimeoutValue(), unit: 'MINUTES')
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

MavenCommand getMavenCommand(String directory, boolean addQuarkusVersion=true, boolean canNative = false) {
    mvnCmd = new MavenCommand(this, ['-fae'])
                .withSettingsXmlId('kogito_release_settings')
                .withSnapshotsDisabledInSettings()
                .withProperty('java.net.preferIPv4Stack', true)
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

boolean isDownstreamJob() {
    return env['DOWNSTREAM_BUILD'] && env['DOWNSTREAM_BUILD'].toBoolean()
}

String getUpstreamTriggerProject() {
    return env['UPSTREAM_TRIGGER_PROJECT']
}

boolean isNormalPRCheck() {
    return !(isDownstreamJob() || getQuarkusBranch() || isNative())
}

Integer getTimeoutValue() {
    return isNative() ? 600 : 240
}
