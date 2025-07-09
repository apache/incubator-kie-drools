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

/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/apache/incubator-kie-kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/apache/incubator-kie-kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.JenkinsFolder
import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.EnvUtils
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

///////////////////////////////////////////////////////////////////////////////////////////
// Whole Optaplanner project jobs
///////////////////////////////////////////////////////////////////////////////////////////

jenkins_path_project = "${jenkins_path}/project"

// Init branch
createProjectSetupBranchJob()

// Nightly jobs
setupProjectNightlyJob()

// Weekly jobs
setupProjectWeeklyJob()

// Release jobs
setupProjectReleaseJob()
setupProjectPostReleaseJob()

if (Utils.isMainBranch(this)) {
    setupProjectDroolsJob('main')
}

// Tools
KogitoJobUtils.createQuarkusPlatformUpdateToolsJob(this, 'optaplanner')
KogitoJobUtils.createMainQuarkusUpdateToolsJob(this,
        [ 'optaplanner', 'optaplanner-quickstarts' ],
        [ 'radtriste', 'lucamolteni']
)

void setupProjectDroolsJob(String droolsBranch) {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'optaplanner-drools-snapshot', JobType.NIGHTLY, 'ecosystem', "${jenkins_path_project}/Jenkinsfile.drools", 'Optaplanner testing against Drools snapshot')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.triggers = [ cron : 'H 2 * * *' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: 'Drools snapshot check',
        DROOLS_BRANCH: droolsBranch,
        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
    }
}

void createProjectSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, '0-setup-branch', JobType.SETUP_BRANCH, "${jenkins_path_project}/Jenkinsfile.setup-branch", 'Optaplanner Project Setup Branch')
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}",
        OPTAPLANNER_LATEST_STREAM: "${GIT_MAIN_BRANCH}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('OPTAPLANNER_VERSION', '', 'OptaPlanner version')
            booleanParam('DEPLOY', true, 'Deploy artifacts')
        }
    }
}

void setupProjectNightlyJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'optaplanner-nightly', JobType.NIGHTLY, "${jenkins_path_project}/Jenkinsfile.nightly", 'Optaplanner Nightly')
    jobParams.triggers = [cron : '@midnight']
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),
        ARTIFACTS_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        OPTAPLANNER_LATEST_STREAM: "${GIT_MAIN_BRANCH}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            booleanParam('SKIP_TESTS', false, 'Skip all tests')
        }
    }
}

void setupProjectWeeklyJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, '0-weekly', JobType.OTHER, "${jenkins_path_project}/Jenkinsfile.weekly", 'Optaplanner Weekly')
    jobParams.triggers = [cron : '0 5 * * 0']
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            booleanParam('SKIP_TESTS', false, 'Skip all tests')
        }
    }
}

void setupProjectReleaseJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'optaplanner-release', JobType.RELEASE, "${jenkins_path_project}/Jenkinsfile.release", 'Optaplanner Release')
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('RESTORE_FROM_PREVIOUS_JOB', '', 'URL to a previous stopped release job which needs to be continued')

            stringParam('RELEASE_VERSION', '', 'Project version of OptaPlanner and its examples to release as Major.minor.micro')

            stringParam('GIT_TAG_NAME', '', 'Git tag to create. i.e.: 10.0.0-rc1')

            booleanParam('SKIP_TESTS', false, 'Skip all tests')
        }
    }
}

void setupProjectPostReleaseJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'optaplanner-post-release', JobType.RELEASE, "${jenkins_path_project}/Jenkinsfile.post-release", 'Optaplanner Post Release')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.RELEASE.name),
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",

        GITHUB_CLI_VERSION: '0.11.1',
        OPTAPLANNER_LATEST_STREAM: "${GIT_MAIN_BRANCH}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('PROJECT_VERSION', '', 'Project version.')
            stringParam('GIT_TAG', '', 'Git tag to use')

            booleanParam('SEND_NOTIFICATION', true, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

///////////////////////////////////////////////////////////////////////////////////////////
// Optaplanner repository only project jobs
///////////////////////////////////////////////////////////////////////////////////////////

Map getMultijobPRConfig(JenkinsFolder jobFolder) {
    def jobConfig = [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'optaplanner',
                primary: true,
                env : [
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    ENABLE_SONARCLOUD: EnvUtils.isDefaultEnvironment(this, jobFolder.getEnvironmentName()) && Utils.isMainBranch(this),
                    SONARCLOUD_ANALYSIS_MVN_OPTS: '-Dsonar.projectKey=apache_incubator-kie-optaplanner',
                ]
            ], [
                id: 'optaplanner-quickstarts',
                repository: 'incubator-kie-optaplanner-quickstarts',
                env : [
                    BUILD_MVN_OPTS_CURRENT: '-Dfull',
                    OPTAPLANNER_BUILD_MVN_OPTS_UPSTREAM: '-Dfull',
                    MIGRATE_TO_9: Utils.isMainBranch(this)
                ]
            ]
        ]
    ]
    return jobConfig
}

// Optaplanner PR checks
Utils.isMainBranch(this) && KogitoJobTemplate.createPullRequestMultibranchPipelineJob(this, "${jenkins_path}/Jenkinsfile")

// Setup branch branch
createSetupBranchJob()

// Nightly jobs
setupDeployJob(JobType.NIGHTLY)
setupSpecificBuildChainNightlyJob('native')

// Release jobs
setupDeployJob(JobType.RELEASE)
setupPromoteJob()

// Weekly deploy job
setupWeeklyDeployJob()

if (Utils.isMainBranch(this)) {
    setupOptaPlannerTurtleTestsJob('drools')
    setupOptaPlannerTurtleTestsJob('bavet')
}

// Tools folder
KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'optaplanner', [
  modules: [ 'optaplanner-build-parent' ],
  compare_deps_remote_poms: [ 'io.quarkus:quarkus-bom' ],
  properties: [ 'version.io.quarkus' ],
])

KogitoJobUtils.createVersionUpdateToolsJob(this, 'optaplanner', 'Drools', [
  modules: [ 'optaplanner-build-parent' ],
  properties: [ 'version.org.drools' ],
])

void setupSpecificBuildChainNightlyJob(String envName) {
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, envName, true)
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'optaplanner', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'OptaPlanner Setup Branch')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}",
        OPTAPLANNER_LATEST_STREAM: "${GIT_MAIN_BRANCH}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('OPTAPLANNER_VERSION', '', 'OptaPlanner version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupDeployJob(JobType jobType, String envName = '') {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'optaplanner-deploy', jobType, envName, "${jenkins_path}/Jenkinsfile.deploy", 'Optaplanner Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    if (jobType == JobType.PULL_REQUEST) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, jobType.name),
        OPTAPLANNER_LATEST_STREAM: "${GIT_MAIN_BRANCH}",
        DISABLE_DEPLOY: Utils.isDeployDisabled(this),

        RELEASE_GPG_SIGN_KEY_CREDS_ID: Utils.getReleaseGpgSignKeyCredentialsId(this),
        RELEASE_GPG_SIGN_PASSPHRASE_CREDS_ID: Utils.getReleaseGpgSignPassphraseCredentialsId(this)
    ])
    if (jobType == JobType.PULL_REQUEST) {
        jobParams.env.putAll([
            MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
            MAVEN_DEPLOY_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
            MAVEN_REPO_CREDS_ID: "${MAVEN_PR_CHECKS_REPOSITORY_CREDS_ID}",
        ])
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobType == JobType.PULL_REQUEST) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            booleanParam('SKIP_TESTS', false, 'Skip tests')
            booleanParam('SKIP_INTEGRATION_TESTS',  false, 'Skip long integration tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            stringParam('DROOLS_VERSION', '', '(optional) Drools version to be set to the project before releasing the artifacts.')

            if (jobType == JobType.PULL_REQUEST) {
                stringParam('PR_TARGET_BRANCH', '', 'What is the target branch of the PR?')
            }

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')

            //Build branch name for quickstarts
            stringParam('QUICKSTARTS_BUILD_BRANCH_NAME', Utils.isMainBranch(this) ? 'development' : "${GIT_BRANCH}", 'Base branch for quickstarts. Set if you are not on a multibranch pipeline.')

            stringParam('OPERATOR_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images.')
            stringParam('OPERATOR_IMAGE_REGISTRY_USER_CREDENTIALS_ID', "${CLOUD_IMAGE_REGISTRY_USER_CREDENTIALS_ID}", 'Image registry user credentials id.')
            stringParam('OPERATOR_IMAGE_REGISTRY_TOKEN_CREDENTIALS_ID', "${CLOUD_IMAGE_REGISTRY_TOKEN_CREDENTIALS_ID}", 'Image registry token credentials id.')
            stringParam('OPERATOR_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Operator image namespace to use to deploy image.')
            stringParam('OPERATOR_IMAGE_TAG', '', 'Image tag to use to deploy the operator image. OptaPlanner project version if not set.')

            stringParam('GIT_TAG_NAME', '', 'Optional if not RELEASE. Tag to be created in the repository')
        }
    }
}

void setupPromoteJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'optaplanner-promote', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.promote", 'Optaplanner Promote')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.RELEASE.name),
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",

        PROPERTIES_FILE_NAME: 'deployment.properties',
        GITHUB_CLI_VERSION: '0.11.1',
        OPTAPLANNER_LATEST_STREAM: "${GIT_MAIN_BRANCH}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file. If base parameters are defined, they will override the `deployment.properties` information')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')

            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')

            stringParam('OPERATOR_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images.')
            stringParam('OPERATOR_IMAGE_REGISTRY_USER_CREDENTIALS_ID', "${CLOUD_IMAGE_REGISTRY_USER_CREDENTIALS_ID}", 'Image registry user credentials id.')
            stringParam('OPERATOR_IMAGE_REGISTRY_TOKEN_CREDENTIALS_ID', "${CLOUD_IMAGE_REGISTRY_TOKEN_CREDENTIALS_ID}", 'Image registry token credentials id.')
            stringParam('OPERATOR_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Operator image namespace to use to deploy image.')
        }
    }
}

void setupOptaPlannerTurtleTestsJob(String constraintStreamImplType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, "optaplanner-turtle-tests-${constraintStreamImplType}", JobType.OTHER, "${jenkins_path}/Jenkinsfile.turtle",
            "Run OptaPlanner turtle tests with CS-${constraintStreamImplType} on a weekly basis.")
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
            CONSTRAINT_STREAM_IMPL_TYPE: "${constraintStreamImplType}",
            JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
            MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),
    ])
    jobParams.triggers = [ cron : 'H H * * 5' ] // Run every Friday.
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Git author or an organization.')
        }
    }
}

void setupWeeklyDeployJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'optaplanner.weekly-deploy', JobType.OTHER, "${jenkins_path}/Jenkinsfile.weekly.deploy", 'Optaplanner Weekly Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: Utils.getMavenArtifactsUploadRepositoryUrl(this, JobType.NIGHTLY.name),
        MAVEN_REPO_CREDS_ID: Utils.getMavenArtifactsUploadRepositoryCredentialsId(this, JobType.NIGHTLY.name),

        DISABLE_DEPLOY: Utils.isDeployDisabled(this),
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            stringParam('QUICKSTARTS_BUILD_BRANCH_NAME', Utils.isMainBranch(this) ? 'development' : "${GIT_BRANCH}", 'Base branch for quickstarts.')

            stringParam('GIT_CHECKOUT_DATETIME', '', 'Git checkout date and time - (Y-m-d H:i)')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
