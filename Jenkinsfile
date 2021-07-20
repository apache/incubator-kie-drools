@Library('jenkins-pipeline-shared-libraries')_

import org.kie.jenkins.MavenCommand

changeAuthor = env.ghprbAuthorRepoGitUrl ? util.getGroup(env.ghprbAuthorRepoGitUrl) : (env.ghprbPullAuthorLogin ?: CHANGE_AUTHOR)
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
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'export XAUTHORITY=$HOME/.Xauthority'
                sh 'chmod 600 $HOME/.vnc/passwd'

                checkoutKogitoRepo('kogito-runtimes')
                checkoutOptaplannerRepo()
                checkoutKogitoRepo('kogito-apps')
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
                    } else {
                        mvnCmd.withProperty('skipUI')
                    }
                    mvnCmd.run('clean install')
                }
            }
            post {
                always {
                    script {
                        archiveArtifacts artifacts: '**/target/*-runner.jar,**/target/*-runner', fingerprint: true
                        junit '**/target/surefire-reports/**/*.xml,**/target/failsafe-reports/**/*.xml'
                    }
                }
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

void checkoutKogitoRepo(String repo, String dirName=repo) {
    dir(dirName) {
        githubscm.checkoutIfExists(repo, changeAuthor, changeBranch, 'kiegroup', getKogitoTargetBranch(), true)
    }
}

void checkoutOptaplannerRepo() {
    dir('optaplanner') {
        githubscm.checkoutIfExists('optaplanner', changeAuthor, changeBranch, 'kiegroup', getOptaplannerTargetBranch(), true)
    }
}

void checkoutQuarkusRepo() {
    dir('quarkus') {
        checkout(githubscm.resolveRepository('quarkus', 'quarkusio', getQuarkusBranch(), false))
    }
}

String getKogitoTargetBranch() {
    return getTargetBranch(isUpstreamOptaplannerProject() ? -7 : 0)
}

String getOptaplannerTargetBranch() {
    return getTargetBranch(isUpstreamOptaplannerProject() ? 0 : 7)
}

String getTargetBranch(Integer addToMajor) {
    String targetBranch = changeTarget
    String [] versionSplit = targetBranch.split("\\.")
    if (versionSplit.length == 3
        && versionSplit[0].isNumber()
        && versionSplit[1].isNumber()
        && versionSplit[2] == 'x') {
        targetBranch = "${Integer.parseInt(versionSplit[0]) + addToMajor}.${versionSplit[1]}.x"
    } else {
        echo "Cannot parse changeTarget as release branch so going further with current value: ${changeTarget}"
        }
    return targetBranch
}

MavenCommand getMavenCommand(String directory, boolean addQuarkusVersion=true, boolean canNative = false) {
    mvnCmd = new MavenCommand(this, ['-fae'])
                .withSettingsXmlId('kogito_release_settings')
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

boolean isUpstreamKogitoProject() {
    return getUpstreamTriggerProject() && getUpstreamTriggerProject().startsWith('kogito')
}

boolean isUpstreamOptaplannerProject() {
    return getUpstreamTriggerProject() && getUpstreamTriggerProject().startsWith('opta')
}

Integer getTimeoutValue() {
    return isNative() ? 360 : 180
}
