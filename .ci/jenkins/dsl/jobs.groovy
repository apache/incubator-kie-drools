/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/kiegroup/kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/kiegroup/kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.JenkinsFolder
import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

Map getMultijobPRConfig(JenkinsFolder jobFolder) {
    def jobConfig = [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'drools',
                primary: true,
                env : [
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    DISABLE_SONARCLOUD: !Utils.isMainBranch(this),
                ]
            ], [
                id: 'kogito-runtimes',
                dependsOn: 'drools',
                repository: 'kogito-runtimes'
            ], [
                id: 'kogito-apps',
                repository: 'kogito-apps',
                dependsOn: 'kogito-runtimes',
            ], [
                id: 'kogito-quarkus-examples',
                repository: 'kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-quarkus-examples/',
                ],
            ], [
                id: 'kogito-springboot-examples',
                repository: 'kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-springboot-examples/',
                ],
            ], [
                id: 'serverless-workflow-examples',
                repository: 'kogito-examples',
                dependsOn: 'kogito-apps',
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'serverless-workflow-examples/',
                ],
            ]
        ]
    ]
    return jobConfig
}

// PR checks
KogitoJobUtils.createAllEnvironmentsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig(jobFolder) }

// Init branch
createSetupBranchJob()

// Nightly jobs
KogitoJobUtils.createNightlyBuildChainBuildAndDeployJobForCurrentRepo(this, '', true)

// Environment nightlies
setupSpecificBuildChainNightlyJob('native')
setupSpecificBuildChainNightlyJob('quarkus-main')
setupSpecificBuildChainNightlyJob('quarkus-branch')
setupSpecificBuildChainNightlyJob('mandrel')
setupSpecificBuildChainNightlyJob('mandrel-lts')
setupSpecificBuildChainNightlyJob('quarkus-lts')

// Jobs with integration branch
setupSpecificNightlyJob('quarkus-main', true)
setupSpecificNightlyJob('quarkus-lts', true)

// Release jobs
setupDeployJob(JobType.RELEASE)
setupPromoteJob(JobType.RELEASE)
setupPostReleaseJob()

KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'drools', [
  modules: [ 'drools-build-parent' ],
  compare_deps_remote_poms: [ 'io.quarkus:quarkus-bom' ],
  properties: [ 'version.io.quarkus' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupSpecificNightlyJob(String envName, boolean useIntegrationBranch = false) {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, "drools${useIntegrationBranch ? '-integration-branch' : ''}", JobType.NIGHTLY, envName, "${jenkins_path}/Jenkinsfile.specific_nightly", "Drools Nightly ${envName}")
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.triggers = [ cron : '@midnight' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: "${envName} check",
        USE_INTEGRATION_BRANCH : useIntegrationBranch,
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            stringParam('GIT_AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}", 'Set the Git author creds id')
        }
    }
}

void setupSpecificBuildChainNightlyJob(String envName) {
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, envName, true)
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'drools', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Drools Setup branch')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'drools',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('DROOLS_VERSION', '', 'Drools version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupDeployJob(JobType jobType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'drools-deploy', jobType, "${jenkins_path}/Jenkinsfile.deploy", 'Drools Deploy')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'drools',
        PROPERTIES_FILE_NAME: 'deployment.properties',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    if (jobType == JobType.RELEASE) {
        jobParams.env.putAll([
            NEXUS_RELEASE_URL: "${MAVEN_NEXUS_RELEASE_URL}",
            NEXUS_RELEASE_REPOSITORY_ID: "${MAVEN_NEXUS_RELEASE_REPOSITORY}",
            NEXUS_STAGING_PROFILE_ID: "${MAVEN_NEXUS_STAGING_PROFILE_ID}",
            NEXUS_BUILD_PROMOTION_PROFILE_ID: "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}",
        ])
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupPromoteJob(JobType jobType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'drools-promote', jobType, "${jenkins_path}/Jenkinsfile.promote", 'Drools Promote')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'drools',
        PROPERTIES_FILE_NAME: 'deployment.properties',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

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
void setupPostReleaseJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'drools-post-release', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.post-release", 'Drools Post Release')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
            JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
            GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
            AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
            MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
            MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('PROJECT_VERSION', '', 'Recent release version of drools.')
            booleanParam('SEND_NOTIFICATION', true, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
