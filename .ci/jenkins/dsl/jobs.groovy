import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.FolderUtils
import org.kie.jenkins.jobdsl.KogitoConstants
import org.kie.jenkins.jobdsl.KogitoJobType
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils
import org.kie.jenkins.jobdsl.VersionUtils

JENKINS_PATH = '.ci/jenkins'

def getDefaultJobParams(String repoName = 'optaplanner') {
    return KogitoJobTemplate.getDefaultJobParams(this, repoName)
}

Map getMultijobPRConfig(boolean isNative = false) {
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
                    BUILD_MVN_OPTS_CURRENT: "-Poptaplanner-downstream",
                    ADDITIONAL_TIMEOUT: isNative ? '360' : '210',
                ]
            ], [
                id: 'kogito-examples',
                repository: 'kogito-examples',
                env : [
                    BUILD_MVN_OPTS_CURRENT: "-Poptaplanner-downstream"
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
    if (isNative) { // Optawebs should not be used in native.
        jobConfig.jobs.retainAll { !it.id.startsWith('optaweb') }
    }
    return jobConfig
}

def getJobParams(String jobName, String jobFolder, String jenkinsfileName, String jobDescription = '') {
    def jobParams = getDefaultJobParams()
    jobParams.job.name = jobName
    jobParams.job.folder = jobFolder
    jobParams.jenkinsfile = jenkinsfileName
    if (jobDescription) {
        jobParams.job.description = jobDescription
    }
    return jobParams
}

if (Utils.isMainBranch(this)) {
    // For BDD runtimes PR job
    setupDeployJob(FolderUtils.getPullRequestRuntimesBDDFolder(this), KogitoJobType.PR)
}

// Optaplanner PR checks
setupMultijobPrDefaultChecks()
setupMultijobPrNativeChecks()
setupMultijobPrLTSChecks()

// Nightly jobs
setupNativeJob()
setupDeployJob(FolderUtils.getNightlyFolder(this), KogitoJobType.NIGHTLY)
setupPromoteJob(FolderUtils.getNightlyFolder(this), KogitoJobType.NIGHTLY)

// No release directly on main branch
if (!Utils.isMainBranch(this)) {
    setupDeployJob(FolderUtils.getReleaseFolder(this), KogitoJobType.RELEASE)
    setupPromoteJob(FolderUtils.getReleaseFolder(this), KogitoJobType.RELEASE)
    setupPostReleaseJob(FolderUtils.getReleaseFolder(this), KogitoJobType.RELEASE)
}

if (Utils.isMainBranch(this)) {
    setupOptaPlannerTurtleTestsJob()
}

if (Utils.isLTSBranch(this)) {
    setupNativeLTSJob()
}

// Tools folder
KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'optaplanner', 'OptaPlanner', [
  modules: [ 'optaplanner-build-parent' ],
  properties: [ 'version.io.quarkus' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupMultijobPrDefaultChecks() {
    KogitoJobTemplate.createMultijobPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrNativeChecks() {
    def multijobConfig = getMultijobPRConfig(true)
    multijobConfig.jobs.find { it.id == 'kogito-apps' }.env.BUILD_MVN_OPTS_CURRENT = "-Poptaplanner-downstream,native ${KogitoConstants.DEFAULT_NATIVE_CONTAINER_PARAMS}"
    multijobConfig.jobs.find { it.id == 'kogito-examples' }.env.BUILD_MVN_OPTS_CURRENT = "-Poptaplanner-downstream-native ${KogitoConstants.DEFAULT_NATIVE_CONTAINER_PARAMS}"
    KogitoJobTemplate.createMultijobNativePRJobs(this, multijobConfig) { return getDefaultJobParams() }
}

void setupMultijobPrLTSChecks() {
    KogitoJobTemplate.createMultijobLTSPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupNativeJob() {
    def jobParams = getJobParams('optaplanner-native', FolderUtils.getNightlyFolder(this), "${JENKINS_PATH}/Jenkinsfile.native", 'Optaplanner Native Testing')
    jobParams.triggers = [ cron : 'H 6 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', 'Native check')
        }
    }
}

void setupNativeLTSJob() {
    def jobParams = getJobParams('optaplanner-native-lts', FolderUtils.getNightlyFolder(this), "${JENKINS_PATH}/Jenkinsfile.native", 'Optaplanner Native LTS Testing')
    jobParams.triggers = [ cron : 'H 8 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')

            stringParam('NATIVE_BUILDER_IMAGE', Utils.getLTSNativeBuilderImage(this), 'Which native builder image to use ?')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', 'Native LTS check')
        }
    }
}

void setupDeployJob(String jobFolder, KogitoJobType jobType) {
    def jobParams = getJobParams('optaplanner-deploy', jobFolder, "${JENKINS_PATH}/Jenkinsfile.deploy", 'Optaplanner Deploy')
    if (jobType == KogitoJobType.PR) {
        jobParams.git.branch = '${GIT_BRABUILD_BRANCH_NAMENCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobType == KogitoJobType.PR) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            booleanParam('SKIP_TESTS', false, 'Skip tests')
            booleanParam('SKIP_INTEGRATION_TESTS',  false, 'Skip long integration tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            if (jobType == KogitoJobType.PR) {
                stringParam('PR_TARGET_BRANCH', '', 'What is the target branch of the PR?')
            }

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')

            //Build branch name for quickstarts
            stringParam('QUICKSTARTS_BUILD_BRANCH_NAME', Utils.isMainBranch(this) ? 'development' : "${GIT_BRANCH}", 'Base branch for quickstarts. Set if you are not on a multibranch pipeline.')
        }

        environmentVariables {
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('RELEASE', jobType == KogitoJobType.RELEASE)
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")

            if (jobType == KogitoJobType.PR) {
                env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_PR_CHECKS_REPOSITORY_URL}")
                env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_PR_CHECKS_REPOSITORY_URL}")
                env('MAVEN_REPO_CREDS_ID', "${MAVEN_PR_CHECKS_REPOSITORY_CREDS_ID}")
            } else {
                env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

                env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
                env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
                env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
                env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

                env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
                env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
                if (jobType == KogitoJobType.RELEASE) {
                    env('NEXUS_RELEASE_URL', "${MAVEN_NEXUS_RELEASE_URL}")
                    env('NEXUS_RELEASE_REPOSITORY_ID', "${MAVEN_NEXUS_RELEASE_REPOSITORY}")
                    env('NEXUS_STAGING_PROFILE_ID', "${MAVEN_NEXUS_STAGING_PROFILE_ID}")
                    env('NEXUS_BUILD_PROMOTION_PROFILE_ID', "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}")
                }
            }
        }
    }
}

void setupPromoteJob(String jobFolder, KogitoJobType jobType) {
    KogitoJobTemplate.createPipelineJob(this, getJobParams('optaplanner-promote', jobFolder, "${JENKINS_PATH}/Jenkinsfile.promote", 'Optaplanner Promote')).with {
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

        environmentVariables {
            env('RELEASE', jobType == KogitoJobType.RELEASE)
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")

            env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

            env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
            env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
            env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
            env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")
            env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
            env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")

            env('PROPERTIES_FILE_NAME', 'deployment.properties')
            env('GITHUB_CLI_VERSION', '0.11.1')
        }
    }
}

void setupPostReleaseJob(String jobFolder, KogitoJobType jobType) {
    KogitoJobTemplate.createPipelineJob(this, getJobParams('optaplanner-post-release', jobFolder, "${JENKINS_PATH}/Jenkinsfile.post-release", 'Optaplanner Post Release')).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('PROJECT_VERSION', '', 'Project version.')
            stringParam('GIT_TAG', '', 'Git tag to use')

            booleanParam('SEND_NOTIFICATION', true, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")

            env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

            env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
            env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")

            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")
            env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")

            env('GITHUB_CLI_VERSION', '0.11.1')
        }
    }
}

void setupOptaPlannerTurtleTestsJob() {
    def jobParams = getJobParams('optaplanner-turtle-tests', FolderUtils.getOtherFolder(this), "${JENKINS_PATH}/Jenkinsfile.turtle",
            'Run OptaPlanner turtle tests on a weekly basis.')
    jobParams.triggers = [ cron : 'H H * * 5' ] // Run every Friday.
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Git author or an organization.')
        }
    }
}
