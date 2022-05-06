/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/kiegroup/kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/kiegroup/kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.Folder
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

Map getMultijobPRConfig(Folder jobFolder) {
    def jobConfig = [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'optaplanner',
                primary: true,
                env : [
                    SONARCLOUD_ANALYSIS_MVN_OPTS: '-Dsonar.projectKey=org.optaplanner:optaplanner',
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    DISABLE_SONARCLOUD: !Utils.isMainBranch(this),
                ]
            ], [
                id: 'kogito-apps',
                repository: 'kogito-apps',
                env : [
                    BUILD_MVN_OPTS_CURRENT: jobFolder.isNative() || jobFolder.isMandrel() ? '-Poptaplanner-downstream,native' : '-Poptaplanner-downstream',
                    ADDITIONAL_TIMEOUT: jobFolder.isNative() || jobFolder.isMandrel() ? '360' : '210',
                ]
            ], [
                id: 'kogito-examples',
                repository: 'kogito-examples',
                env : [
                    BUILD_MVN_OPTS_CURRENT: jobFolder.isNative() || jobFolder.isMandrel() ? '-Poptaplanner-downstream-native' : '-Poptaplanner-downstream'
                ]
            ], [
                id: 'optaweb-employee-rostering',
                repository: 'optaweb-employee-rostering'
            ], [
                id: 'optaweb-vehicle-routing',
                repository: 'optaweb-vehicle-routing'
            ], [
                id: 'optaplanner-quickstarts',
                repository: 'optaplanner-quickstarts',
                env : [
                    BUILD_MVN_OPTS_CURRENT: '-Dfull',
                    OPTAPLANNER_BUILD_MVN_OPTS_UPSTREAM: '-Dfull'
                ]
            ]
        ]
    ]
    if (jobFolder.isNative() || jobFolder.isMandrel()) { // Optawebs should not be built in native.
        jobConfig.jobs.retainAll { !it.id.startsWith('optaweb') }
    }
    return jobConfig
}

// Optaplanner PR checks
KogitoJobUtils.createAllEnvsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig(jobFolder) }
setupDeployJob(Folder.PULLREQUEST_RUNTIMES_BDD)

// Nightly jobs
setupNativeJob()
setupDeployJob(Folder.NIGHTLY)
setupMandrelJob()

// Release jobs
setupDeployJob(Folder.RELEASE)
setupPromoteJob(Folder.RELEASE)
setupPostReleaseJob()

if (Utils.isMainBranch(this)) {
    setupOptaPlannerTurtleTestsJob()
}

// Tools folder
KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'optaplanner', [
  modules: [ 'optaplanner-build-parent' ],
  properties: [ 'version.io.quarkus' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupNativeJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner', Folder.NIGHTLY_NATIVE, "${jenkins_path}/Jenkinsfile.native", 'Optaplanner Native Testing')
    jobParams.triggers = [ cron : 'H 6 * * *' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: 'Native check',
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
    }
}

void setupMandrelJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner', Folder.NIGHTLY_MANDREL, "${jenkins_path}/Jenkinsfile.native", 'Optaplanner Mandrel Testing')
    jobParams.triggers = [ cron : 'H 8 * * *' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: 'Mandrel check',
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
    }
}

void setupDeployJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-deploy', jobFolder, "${jenkins_path}/Jenkinsfile.deploy", 'Optaplanner Deploy')
    if (jobFolder.isPullRequest()) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
    ])
    if (jobFolder.isPullRequest()) {
        jobParams.env.putAll([
            MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
            MAVEN_DEPLOY_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
            MAVEN_REPO_CREDS_ID: "${MAVEN_PR_CHECKS_REPOSITORY_CREDS_ID}",
        ])
    } else {
        jobParams.env.putAll([
            GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

            AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
            GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
            GIT_AUTHOR_BOT: "${GIT_BOT_AUTHOR_NAME}",
            BOT_CREDENTIALS_ID: "${GIT_BOT_AUTHOR_CREDENTIALS_ID}",

            MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
            MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        ])
        if (jobFolder.isRelease()) {
            jobParams.env.putAll([
                NEXUS_RELEASE_URL: "${MAVEN_NEXUS_RELEASE_URL}",
                NEXUS_RELEASE_REPOSITORY_ID: "${MAVEN_NEXUS_RELEASE_REPOSITORY}",
                NEXUS_STAGING_PROFILE_ID: "${MAVEN_NEXUS_STAGING_PROFILE_ID}",
                NEXUS_BUILD_PROMOTION_PROFILE_ID: "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}",
            ])
        }
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobFolder.isPullRequest()) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            booleanParam('SKIP_TESTS', false, 'Skip tests')
            booleanParam('SKIP_INTEGRATION_TESTS',  false, 'Skip long integration tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            if (jobFolder.isPullRequest()) {
                stringParam('PR_TARGET_BRANCH', '', 'What is the target branch of the PR?')
            }

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')

            //Build branch name for quickstarts
            stringParam('QUICKSTARTS_BUILD_BRANCH_NAME', Utils.isMainBranch(this) ? 'development' : "${GIT_BRANCH}", 'Base branch for quickstarts. Set if you are not on a multibranch pipeline.')
        }
    }
}

void setupPromoteJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-promote', jobFolder, "${jenkins_path}/Jenkinsfile.promote", 'Optaplanner Promote')
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
        GIT_AUTHOR_BOT: "${GIT_BOT_AUTHOR_NAME}",
        BOT_CREDENTIALS_ID: "${GIT_BOT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",

        PROPERTIES_FILE_NAME: 'deployment.properties',
        GITHUB_CLI_VERSION: '0.11.1',
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file. If base parameters are defined, they will override the `deployment.properties` information')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupPostReleaseJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-post-release', Folder.RELEASE, "${jenkins_path}/Jenkinsfile.post-release", 'Optaplanner Post Release')
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",

        GITHUB_CLI_VERSION: '0.11.1',
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

void setupOptaPlannerTurtleTestsJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-turtle-tests', Folder.OTHER, "${jenkins_path}/Jenkinsfile.turtle",
            'Run OptaPlanner turtle tests on a weekly basis.')
    jobParams.triggers = [ cron : 'H H * * 5' ] // Run every Friday.
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Git author or an organization.')
        }
    }
}
