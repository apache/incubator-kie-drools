import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoConstants
import org.kie.jenkins.jobdsl.Utils
import org.kie.jenkins.jobdsl.KogitoJobType

def getDefaultJobParams() {
    return KogitoJobTemplate.getDefaultJobParams(this, 'kogito-runtimes')
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

Map getMultijobPRConfig() {
    return [
        parallel: true,
        jobs : [
            [
                id: 'Runtimes',
                primary: true,
            ], [
                id: 'Optaplanner',
                dependsOn: 'Runtimes',
                repository: 'optaplanner',
                // TODO remove once https://issues.redhat.com/browse/KOGITO-4113 is done 
                // as it will become the default path
                jenkinsfile: '.ci/jenkins/Jenkinsfile',
            ], [
                id: 'Apps',
                dependsOn: 'Optaplanner',
                repository: 'kogito-apps'
            ], [
                id: 'Examples',
                dependsOn: 'Optaplanner',
                repository: 'kogito-examples'
            ]
        ],
        extraEnv : [
            ENABLE_SONARCLOUD: Utils.isMainBranch(this)
        ]
    ]
}

def bddRuntimesPrFolder = "${KogitoConstants.KOGITO_DSL_PULLREQUEST_FOLDER}/${KogitoConstants.KOGITO_DSL_RUNTIMES_BDD_FOLDER}"
def nightlyBranchFolder = "${KogitoConstants.KOGITO_DSL_NIGHTLY_FOLDER}/${JOB_BRANCH_FOLDER}"
def releaseBranchFolder = "${KogitoConstants.KOGITO_DSL_RELEASE_FOLDER}/${JOB_BRANCH_FOLDER}"

if (Utils.isMainBranch(this)) {
    // Old PR checks.
    // To be removed once supported release branches (<= 1.7.x) are no more there.
    setupPrJob()
    // Disabled until Quarkus 2.2 is out
    // Follow-up issue: https://issues.redhat.com/browse/KOGITO-5487
    // setupQuarkusLTSPrJob()
    setupNativePrJob()
    // End of old PR checks

    setupDeployJob(bddRuntimesPrFolder, KogitoJobType.PR)

    // Sonarcloud analysis only on main branch
    // As we have only Community edition
    setupSonarCloudJob(nightlyBranchFolder)
}

// PR checks
setupMultijobPrDefaultChecks()
setupMultijobPrNativeChecks()
// Disabled until Quarkus 2.2 is out
// Follow-up issue: https://issues.redhat.com/browse/KOGITO-5487
// setupMultijobPrLTSChecks()

// Nightly jobs
if (Utils.isMainBranch(this)) {
    setupDroolsJob(nightlyBranchFolder)

    setupQuarkusJob(nightlyBranchFolder, 'main')
    // Disabled until Quarkus 2.2 is out
    // Follow-up issue: https://issues.redhat.com/browse/KOGITO-5487
    // setupQuarkusJob(nightlyBranchFolder, "${QUARKUS_LTS_VERSION}")
}
setupNativeJob(nightlyBranchFolder)
setupDeployJob(nightlyBranchFolder, KogitoJobType.NIGHTLY)
setupPromoteJob(nightlyBranchFolder, KogitoJobType.NIGHTLY)

// No release directly on main branch
if (!Utils.isMainBranch(this)) {
    setupDeployJob(releaseBranchFolder, KogitoJobType.RELEASE)
    setupPromoteJob(releaseBranchFolder, KogitoJobType.RELEASE)
}

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupPrJob() {
    def jobParams = getDefaultJobParams()
    jobParams.pr.run_only_for_branches = ['1.5.x']
    jobParams.env = [ TIMEOUT_VALUE : 240 ]
    KogitoJobTemplate.createPRJob(this, jobParams)
}

void setupQuarkusLTSPrJob() {
    def jobParams = getDefaultJobParams()
    jobParams.pr.run_only_for_branches = ['1.5.x']
    jobParams.env = [ TIMEOUT_VALUE : 240 ]
    KogitoJobTemplate.createQuarkusLTSPRJob(this, jobParams)
}

void setupNativePrJob() {
    def jobParams = getDefaultJobParams()
    jobParams.pr.run_only_for_branches = ['1.5.x']
    jobParams.env = [ TIMEOUT_VALUE : 600 ]
    KogitoJobTemplate.createNativePRJob(this, jobParams)
}

void setupMultijobPrDefaultChecks() {
    KogitoJobTemplate.createMultijobPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrNativeChecks() {
    KogitoJobTemplate.createMultijobNativePRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrLTSChecks() {
    KogitoJobTemplate.createMultijobLTSPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupDroolsJob(String jobFolder) {
    def jobParams = getJobParams('kogito-drools-snapshot', jobFolder, 'Jenkinsfile.drools', 'Kogito Runtimes Drools Snapshot')
    jobParams.triggers = [ cron : 'H 2 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')

            stringParam('DROOLS_VERSION', '', '(optional) If not set, then it will be guessed from drools repository')
            stringParam('DROOLS_REPOSITORY', '', '(optional) In case Drools given version is in a specific repository')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
        }
    }
}

void setupQuarkusJob(String jobFolder, String quarkusBranch) {
    def jobParams = getJobParams("kogito-quarkus-${quarkusBranch}", jobFolder, 'Jenkinsfile.quarkus', 'Kogito Runtimes Quarkus Snapshot')
    jobParams.triggers = [ cron : 'H 4 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('QUARKUS_BRANCH', quarkusBranch)
        }
    }
}

void setupSonarCloudJob(String jobFolder) {
    def jobParams = getJobParams('kogito-runtimes-sonarcloud', jobFolder, 'Jenkinsfile.sonarcloud', 'Kogito Runtimes Daily Sonar')
    jobParams.triggers = [ cron : 'H 20 * * 1-5' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
        }
    }
}

void setupNativeJob(String jobFolder) {
    def jobParams = getJobParams('kogito-native', jobFolder, 'Jenkinsfile.native', 'Kogito Runtimes Native Testing')
    jobParams.triggers = [ cron : 'H 6 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
        }
    }
}

void setupDeployJob(String jobFolder, KogitoJobType jobType) {
    def jobParams = getJobParams('kogito-runtimes-deploy', jobFolder, 'Jenkinsfile.deploy', 'Kogito Runtimes Deploy')
    if (jobType == KogitoJobType.PR) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
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

            // Build&test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')

            // Release information
            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Set the project version')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('REPO_NAME', 'kogito-runtimes')

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
    KogitoJobTemplate.createPipelineJob(this, getJobParams('kogito-runtimes-promote', jobFolder, 'Jenkinsfile.promote', 'Kogito Runtimes Promote')).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file.')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Give the project version.')

            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('REPO_NAME', 'kogito-runtimes')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

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
        }
    }
}
