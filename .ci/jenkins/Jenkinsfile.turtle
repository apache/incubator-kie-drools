/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

@Library('jenkins-pipeline-shared-libraries')_

import org.kie.jenkins.MavenCommand

optaplannerRepo = 'incubator-kie-optaplanner'

pipeline {
    agent {
        docker { 
            image env.AGENT_DOCKER_BUILDER_IMAGE
            args env.AGENT_DOCKER_BUILDER_ARGS
            label util.avoidFaultyNodes()
        }
    }
    options {
        timestamps()
        timeout(time: 3, unit: 'DAYS') // Turtle tests take ~2 days to complete.
        disableConcurrentBuilds(abortPrevious: true)
    }
    environment {
        // Contains the email address of the team's Zulip channel.
        OPTAPLANNER_CI_EMAIL_TO = credentials("${JENKINS_EMAIL_CREDS_ID}")
    }
    stages {
        stage('Initialize') {
            steps {
                script {
                    checkoutRepo(optaplannerRepo)
                }
            }
        }
        stage('Build OptaPlanner with turtle tests') {
            steps {
                script {
                    // Use the same settings.xml as for the nightly builds, including a maven mirror.
                    configFileProvider([configFile(fileId: env.MAVEN_SETTINGS_CONFIG_FILE_ID, variable: 'MAVEN_SETTINGS_FILE')]){
                        new MavenCommand(this)
                            .inDirectory(optaplannerRepo)
                            .withOptions(['-U', '-e', '-fae', '-ntp'])
                            .withProperty('full')
                            .withProperty('runTurtleTests', true)
                            .withProperty('maven.test.failure.ignore', true)
                            .withProperty('constraintStreamImplType', getConstraintStreamImplType())
                            .withSettingsXmlFile(MAVEN_SETTINGS_FILE)
                            .run('clean install')
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                junit testResults: '**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml', allowEmptyResults: true
                util.archiveConsoleLog()
            }
        }
        success {
            sendSuccessNotification()
        }
        unsuccessful {
            sendErrorNotification()
        }
        cleanup {
            cleanWs()
        }
    }
}

void checkoutRepo(String repo, String dirName=repo) {
    dir(dirName) {
        checkout(githubscm.resolveRepository(repo, getGitAuthor(), getBuildBranch(), false))
    }
}

String getBuildBranch() {
    return params.BUILD_BRANCH_NAME
}

String getGitAuthor() {
    return params.GIT_AUTHOR
}

String getConstraintStreamImplType() {
    return env.CONSTRAINT_STREAM_IMPL_TYPE
}

void sendErrorNotification() {
    mailer.sendMarkdownTestSummaryNotification("CI failures", [env.OPTAPLANNER_CI_EMAIL_TO], getNotificationAdditionalInfo())
}

void sendSuccessNotification() {
    mailer.sendMarkdownTestSummaryNotification("CI success", [env.OPTAPLANNER_CI_EMAIL_TO], getNotificationAdditionalInfo())
}

String getNotificationAdditionalInfo() {
    return """
    **[${getBuildBranch()}] Optaplanner - Turtle tests**
    **${getConstraintStreamImplType()}**
    """
}
