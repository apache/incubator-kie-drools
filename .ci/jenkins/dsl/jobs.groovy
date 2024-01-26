/**
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
import org.kie.jenkins.jobdsl.utils.VersionUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

boolean isMainStream() {
    return Utils.getStream(this) == 'main'
}

Map getMultijobPRConfig(JenkinsFolder jobFolder) {
    String defaultBuildMvnOptsCurrent = jobFolder.getDefaultEnvVarValue('BUILD_MVN_OPTS_CURRENT') ?: ''
    def jobConfig = [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'kogito-apps',
                primary: true,
                env : [
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    ENABLE_SONARCLOUD: EnvUtils.isDefaultEnvironment(this, jobFolder.getEnvironmentName()) && Utils.isMainBranch(this),
                    BUILD_MVN_OPTS_CURRENT: "${defaultBuildMvnOptsCurrent} ${getAppsBuildMvnOptions(jobFolder).join(' ')}",
                ]
            ], [
                id: 'kogito-quarkus-examples',
                repository: 'incubator-kie-kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-quarkus-examples/',
                    BUILD_MVN_OPTS_CURRENT: "${defaultBuildMvnOptsCurrent} ${isNative(jobFolder) ? '-Pkogito-apps-downstream-native' : '-Pkogito-apps-downstream'}"
                ],
            ], [
                id: 'kogito-springboot-examples',
                repository: 'incubator-kie-kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-springboot-examples/',
                    BUILD_MVN_OPTS_CURRENT: "${defaultBuildMvnOptsCurrent} ${isNative(jobFolder) ? '-Pkogito-apps-downstream-native' : '-Pkogito-apps-downstream'}"
                ],
            ], [
                id: 'serverless-workflow-examples',
                repository: 'incubator-kie-kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'serverless-workflow-examples/',
                    BUILD_MVN_OPTS_CURRENT: "${defaultBuildMvnOptsCurrent} ${isNative(jobFolder) ? '-Pkogito-apps-downstream-native' : '-Pkogito-apps-downstream'}"
                ],
            ]
        ]
    ]

    return jobConfig
}

List getAppsBuildMvnOptions(JenkinsFolder jobFolder) {
    List mvnOpts = []
    if (isMainStream() && !jobFolder.getEnvironmentName()) {
        // Validate formatting only for default env
        mvnOpts += ['-Dvalidate-formatting']
    }
    return mvnOpts
}

boolean isNative(JenkinsFolder jobFolder) {
    return EnvUtils.hasEnvironmentId(this, jobFolder.getEnvironmentName(), 'native')
}

// PR checks
Utils.isMainBranch(this) && KogitoJobTemplate.createPullRequestMultibranchPipelineJob(this, "${jenkins_path}/Jenkinsfile")

// Init branch
createSetupBranchJob()

// Nightly jobs
Closure setup4AMCronTriggerJobParamsGetter = { script ->
    def jobParams = JobParamsUtils.DEFAULT_PARAMS_GETTER(script)
    jobParams.triggers = [ cron: 'H 4 * * *' ]
    return jobParams
}

Closure setupAdditionalTimeout = { Closure paramsGetter ->
    return { script ->
        def jobParams = paramsGetter(script)
        jobParams.env.put('ADDITIONAL_TIMEOUT', '480')
        return jobParams
    }
}

Closure setupSonarProjectKeyEnv = { Closure paramsGetter ->
    return { script ->
        def jobParams = paramsGetter(script)
        jobParams.env.put('SONAR_PROJECT_KEY', 'apache_incubator-kie-kogito-apps')
        return jobParams
    }
}

Closure nightlyJobParamsGetter = isMainStream() ? JobParamsUtils.DEFAULT_PARAMS_GETTER : setup4AMCronTriggerJobParamsGetter
KogitoJobUtils.createNightlyBuildChainBuildAndDeployJobForCurrentRepo(this, '', true, setupAdditionalTimeout(JobParamsUtils.DEFAULT_PARAMS_GETTER))
setupSpecificBuildChainNightlyJob('sonarcloud', setupAdditionalTimeout(setupSonarProjectKeyEnv(nightlyJobParamsGetter)))
setupSpecificBuildChainNightlyJob('native', nightlyJobParamsGetter)

// Release jobs
setupReleaseDeployJob()
setupReleasePromoteJob()

// Weekly deploy job
setupWeeklyDeployJob()

// Update Optaplanner tools job
if (isMainStream()) {
    KogitoJobUtils.createVersionUpdateToolsJob(this, 'kogito-apps', 'Optaplanner', [
        modules: [ 'kogito-apps-build-parent' ],
        properties: [ 'version.org.optaplanner' ],
    ])
}

if (Utils.isMainBranch(this)) {
    setupOptaplannerJob(VersionUtils.getProjectTargetBranch('optaplanner', Utils.getGitMainBranch(this), Utils.getRepoName(this)))
}

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupNightlyQuarkusIntegrationJob(String envName, Closure defaultJobParamsGetter = JobParamsUtils.DEFAULT_PARAMS_GETTER) {
    KogitoJobUtils.createNightlyBuildChainIntegrationJob(this, envName, Utils.getRepoName(this), true, defaultJobParamsGetter)
}

void setupSpecificBuildChainNightlyJob(String envName, Closure defaultJobParamsGetter = JobParamsUtils.DEFAULT_PARAMS_GETTER) {
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, envName, true, defaultJobParamsGetter)
}

void setupOptaplannerJob(String optaplannerBranch) {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'kogito-apps-optaplanner-snapshot', JobType.NIGHTLY, 'ecosystem', "${jenkins_path}/Jenkinsfile.optaplanner", 'Kogito Apps Testing against Optaplanner snapshot')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.triggers = [ cron : 'H 6 * * *' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: 'Optaplanner snapshot check',
        OPTAPLANNER_BRANCH: optaplannerBranch,

    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
    }
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-apps', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Apps Init branch')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('KOGITO_VERSION', '', 'Kogito version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupReleaseDeployJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-apps-deploy', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Apps Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_UPLOAD_REPOSITORY_URL}",
        MAVEN_REPO_CREDS_ID: "${MAVEN_ARTIFACTS_UPLOAD_REPOSITORY_CREDS_ID}",

        NEXUS_RELEASE_URL: "${MAVEN_NEXUS_RELEASE_URL}",
        NEXUS_RELEASE_REPOSITORY_ID: "${MAVEN_NEXUS_RELEASE_REPOSITORY}",
        NEXUS_STAGING_PROFILE_ID: "${MAVEN_NEXUS_STAGING_PROFILE_ID}",
        NEXUS_BUILD_PROMOTION_PROFILE_ID: "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('KOGITO_PR_BRANCH', '', 'PR branch name')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupReleasePromoteJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-apps-promote', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Apps Promote')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
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
        }
    }
}

void setupWeeklyDeployJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'drools.weekly-deploy', JobType.OTHER, "${jenkins_path}/Jenkinsfile.weekly.deploy", 'Drools Weekly Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_UPLOAD_REPOSITORY_URL}",
        MAVEN_REPO_CREDS_ID: "${MAVEN_ARTIFACTS_UPLOAD_REPOSITORY_CREDS_ID}",

        DROOLS_STREAM: Utils.getStream(this),
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            stringParam('GIT_CHECKOUT_DATETIME', '', 'Git checkout date and time - (Y-m-d H:i)')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
