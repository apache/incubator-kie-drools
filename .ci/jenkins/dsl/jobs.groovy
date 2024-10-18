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
import org.kie.jenkins.jobdsl.utils.PrintUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

boolean isMainStream() {
    return Utils.getStream(this) == 'main'
}

Closure setup4AMCronTriggerJobParamsGetter = { script ->
    def jobParams = JobParamsUtils.DEFAULT_PARAMS_GETTER(script)
    jobParams.triggers = [ cron: 'H 4 * * *' ]
    return jobParams
}

Map getMultijobPRConfig(JenkinsFolder jobFolder) {
    String defaultBuildMvnOptsCurrent = jobFolder.getDefaultEnvVarValue('BUILD_MVN_OPTS_CURRENT') ?: ''
    def jobConfig = [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'kogito-runtimes',
                primary: true,
                env : [
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    ENABLE_SONARCLOUD: EnvUtils.isDefaultEnvironment(this, jobFolder.getEnvironmentName()) && Utils.isMainBranch(this),
                    BUILD_MVN_OPTS_CURRENT: "${defaultBuildMvnOptsCurrent} ${getRuntimesBuildMvnOptions(jobFolder).join(' ')}",
                ]
            ], [
                id: 'kogito-apps',
                dependsOn: 'kogito-runtimes',
                repository: 'incubator-kie-kogito-apps',
            ], [
                id: 'kogito-quarkus-examples',
                repository: 'incubator-kie-kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-quarkus-examples/',
                ],
            ], [
                id: 'kogito-springboot-examples',
                repository: 'incubator-kie-kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-springboot-examples/',
                ],
            ], [
                id: 'serverless-workflow-examples',
                repository: 'incubator-kie-kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'serverless-workflow-examples/',
                ],
            ]
        ],
    ]

    return jobConfig
}

List getRuntimesBuildMvnOptions(JenkinsFolder jobFolder) {
    List mvnOpts = []
    // No parallel build for native
    mvnOpts += EnvUtils.hasEnvironmentId(this, jobFolder.getEnvironmentName(), 'native') ? [] : ['-T 1C']
    if (isMainStream() && !jobFolder.getEnvironmentName()) {
        // Validate formatting only for default env
        mvnOpts += ['-Dvalidate-formatting']
    }
    return mvnOpts
}

// PR checks
Utils.isMainBranch(this) && KogitoJobTemplate.createPullRequestMultibranchPipelineJob(this, "${jenkins_path}/Jenkinsfile")

// Init branch
createSetupBranchJob()

// Nightly jobs
Closure setupSonarProjectKeyEnv = { Closure paramsGetter ->
    return { script ->
        def jobParams = paramsGetter(script)
        jobParams.env.put('SONAR_PROJECT_KEY', 'apache_incubator-kie-kogito-runtimes')
        return jobParams
    }
}

Closure nightlyJobParamsGetter = isMainStream() ? JobParamsUtils.DEFAULT_PARAMS_GETTER : setup4AMCronTriggerJobParamsGetter
KogitoJobUtils.createNightlyBuildChainBuildAndDeployJobForCurrentRepo(this, '', true)
setupSpecificBuildChainNightlyJob('sonarcloud', setupSonarProjectKeyEnv(nightlyJobParamsGetter))
setupSpecificBuildChainNightlyJob('native', nightlyJobParamsGetter)

// Release jobs
setupReleaseDeployJob()
setupReleasePromoteJob()

// Weekly deploy job
setupWeeklyDeployJob()

// Tools job
if (isMainStream()) {
    KogitoJobUtils.createQuarkusVersionUpdateToolsJobForCurrentRepo(this, [
        modules: [ 'kogito-dependencies-bom', 'kogito-build-parent', 'kogito-quarkus-bom', 'kogito-build-no-bom-parent' ],
        compare_deps_remote_poms: [ 'io.quarkus:quarkus-bom' ],
        properties: [ 'version.io.quarkus' ],
    ])
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

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-runtimes', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Runtimes Setup branch')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('KOGITO_VERSION', '', 'Kogito version to set.')
            stringParam('DROOLS_VERSION', '', 'Drools version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupReleaseDeployJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-runtimes-deploy', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Runtimes Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.RELEASE.name),
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: Utils.getMavenArtifactsUploadRepositoryUrl(this, JobType.RELEASE.name),
        MAVEN_REPO_CREDS_ID: Utils.getMavenArtifactsUploadRepositoryCredentialsId(this, JobType.RELEASE.name),

        RELEASE_GPG_SIGN_KEY_CREDS_ID: Utils.getReleaseGpgSignKeyCredentialsId(this),
        RELEASE_GPG_SIGN_PASSPHRASE_CREDS_ID: Utils.getReleaseGpgSignPassphraseCredentialsId(this)
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Build&test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')

            // Release information
            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Set the project version')
            stringParam('DROOLS_VERSION', '', 'Drools version to set')
            stringParam('KOGITO_PR_BRANCH', '', 'PR branch name')
            stringParam('GIT_TAG_NAME', '', 'Git tag to create. i.e.: 10.0.0-rc1')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupReleasePromoteJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-runtimes-promote', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Runtimes Promote')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.RELEASE.name),
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file.')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Give the project version.')
            stringParam('DROOLS_VERSION', '', 'Override `deployment.properties`. Drools version to set')

            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupWeeklyDeployJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-runtimes.weekly-deploy', JobType.OTHER, "${jenkins_path}/Jenkinsfile.weekly.deploy", 'Kogito Runtimes Weekly Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: Utils.getMavenSettingsConfigFileId(this, JobType.NIGHTLY.name),
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: Utils.getMavenArtifactsUploadRepositoryUrl(this, JobType.NIGHTLY.name),
        MAVEN_REPO_CREDS_ID: Utils.getMavenArtifactsUploadRepositoryCredentialsId(this, JobType.NIGHTLY.name),
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Build&test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')

            stringParam('GIT_CHECKOUT_DATETIME', '', 'Git checkout date and time - (Y-m-d H:i)')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
