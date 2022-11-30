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

///////////////////////////////////////////////////////////////////////////////////////////
// Whole Optaplanner project jobs
///////////////////////////////////////////////////////////////////////////////////////////

jenkins_path_project = "${jenkins_path}/project"

// Init branch
createProjectSetupBranchJob()

// Nightly jobs
setupProjectNightlyJob()

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
        [ 'rsynek', 'triceo']
)

void setupProjectDroolsJob(String droolsBranch) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-drools-snapshot', Folder.NIGHTLY_ECOSYSTEM, "${jenkins_path_project}/Jenkinsfile.drools", 'Optaplanner testing against Drools snapshot')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.triggers = [ cron : 'H 2 * * *' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: 'Drools snapshot check',
        DROOLS_BRANCH: droolsBranch,
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
    }
}

void createProjectSetupBranchJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, '0-setup-branch', Folder.SETUP_BRANCH, "${jenkins_path_project}/Jenkinsfile.setup-branch", 'Optaplanner Project Setup Branch')
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('OPTAPLANNER_VERSION', '', 'OptaPlanner version')
        }
    }
}

void setupProjectNightlyJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-nightly', Folder.NIGHTLY, "${jenkins_path_project}/Jenkinsfile.nightly", 'Optaplanner Nightly')
    jobParams.triggers = [cron : '@midnight']
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        ARTIFACTS_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            booleanParam('SKIP_TESTS', false, 'Skip all tests')
        }
    }
}

void setupProjectReleaseJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-release', Folder.RELEASE, "${jenkins_path_project}/Jenkinsfile.release", 'Optaplanner Release')
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_BRANCH_NAME: "${GIT_BRANCH}",
        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        DEFAULT_STAGING_REPOSITORY: "${MAVEN_NEXUS_STAGING_PROFILE_URL}",
        ARTIFACTS_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('RESTORE_FROM_PREVIOUS_JOB', '', 'URL to a previous stopped release job which needs to be continued')

            stringParam('OPTAPLANNER_VERSION', '', 'Project version of OptaPlanner and its examples to release as Major.minor.micro')
            stringParam('OPTAPLANNER_RELEASE_BRANCH', '', '(optional) Use to override the release branch name deduced from the OPTAPLANNER_VERSION')

            booleanParam('SKIP_TESTS', false, 'Skip all tests')
        }
    }
}

void setupProjectPostReleaseJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-post-release', Folder.RELEASE, "${jenkins_path_project}/Jenkinsfile.post-release", 'Optaplanner Post Release')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
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

///////////////////////////////////////////////////////////////////////////////////////////
// Optaplanner repository only project jobs
///////////////////////////////////////////////////////////////////////////////////////////

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
    if (jobFolder.isNative() || jobFolder.isMandrel()  || jobFolder.isMandrelLTS()) { // Optaweb should not be built in native.
        jobConfig.jobs.retainAll { !it.id.startsWith('optaweb') }
    }
    return jobConfig
}

// Optaplanner PR checks
KogitoJobUtils.createAllEnvsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig(jobFolder) }
setupDeployJob(Folder.PULLREQUEST_RUNTIMES_BDD)

// Setup branch branch
createSetupBranchJob()

// Nightly jobs
setupDeployJob(Folder.NIGHTLY)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_NATIVE)

setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_QUARKUS_MAIN)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_QUARKUS_BRANCH)

setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_MANDREL)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_MANDREL_LTS)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_QUARKUS_LTS)

// Release jobs
setupDeployJob(Folder.RELEASE)
setupPromoteJob(Folder.RELEASE)

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

void setupSpecificBuildChainNightlyJob(Folder specificNightlyFolder) {
    String envName = specificNightlyFolder.environment.toName()
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, specificNightlyFolder, true, envName)
}

void createSetupBranchJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner', Folder.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'OptaPlanner Setup Branch')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'optaplanner',
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

            stringParam('OPTAPLANNER_VERSION', '', 'OptaPlanner version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupDeployJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-deploy', jobFolder, "${jenkins_path}/Jenkinsfile.deploy", 'Optaplanner Deploy')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
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

            OPERATOR_IMAGE_NAME: 'optaplanner-operator',
            MAX_REGISTRY_RETRIES: 3,
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

            if (jobFolder.isPullRequest()) {
                stringParam('PR_TARGET_BRANCH', '', 'What is the target branch of the PR?')
            }

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')

            //Build branch name for quickstarts
            stringParam('QUICKSTARTS_BUILD_BRANCH_NAME', Utils.isMainBranch(this) ? 'development' : "${GIT_BRANCH}", 'Base branch for quickstarts. Set if you are not on a multibranch pipeline.')

            stringParam('OPERATOR_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images.')
            stringParam('OPERATOR_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Image registry credentials.')
            stringParam('OPERATOR_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Operator image namespace to use to deploy image.')
            stringParam('OPERATOR_IMAGE_TAG', '', 'Image tag to use to deploy the operator image. OptaPlanner project version if not set.')
        }
    }
}

void setupPromoteJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaplanner-promote', jobFolder, "${jenkins_path}/Jenkinsfile.promote", 'Optaplanner Promote')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
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

            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')

            stringParam('OPERATOR_IMAGE_REGISTRY', "${CLOUD_IMAGE_REGISTRY}", 'Image registry to use to deploy images.')
            stringParam('OPERATOR_IMAGE_REGISTRY_CREDENTIALS', "${CLOUD_IMAGE_REGISTRY_CREDENTIALS}", 'Image registry credentials.')
            stringParam('OPERATOR_IMAGE_NAMESPACE', "${CLOUD_IMAGE_NAMESPACE}", 'Operator image namespace to use to deploy image.')
        }
    }
}

void setupOptaPlannerTurtleTestsJob(String constraintStreamImplType) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, "optaplanner-turtle-tests-${constraintStreamImplType}", Folder.OTHER, "${jenkins_path}/Jenkinsfile.turtle",
            "Run OptaPlanner turtle tests with CS-${constraintStreamImplType} on a weekly basis.")
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
            CONSTRAINT_STREAM_IMPL_TYPE: "${constraintStreamImplType}",
            JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}"
    ])
    jobParams.triggers = [ cron : 'H H * * 5' ] // Run every Friday.
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Git author or an organization.')
        }
    }
}
