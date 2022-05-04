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
    return [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'kogito-runtimes',
                primary: true,
                env : [
                    // Sonarcloud analysis only on main branch
                    // As we have only Community edition
                    DISABLE_SONARCLOUD: !Utils.isMainBranch(this),
                ]
            ], [
                id: 'kogito-apps',
                dependsOn: 'kogito-runtimes',
                repository: 'kogito-apps',
                env : [
                    ADDITIONAL_TIMEOUT: jobFolder.isNative() || jobFolder.isMandrel() ? '360' : '210',
                ]
            ], [
                id: 'kogito-examples',
                dependsOn: 'kogito-runtimes',
                repository: 'kogito-examples'
            ]
        ],
    ]
}

// PR checks
KogitoJobUtils.createAllEnvsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig(jobFolder) }
setupDeployJob(Folder.PULLREQUEST_RUNTIMES_BDD)

// Nightly jobs
setupSonarCloudJob()
setupDeployJob(Folder.NIGHTLY)
setupNativeJob()
setupQuarkusJob(Folder.NIGHTLY_QUARKUS_MAIN)
setupQuarkusJob(Folder.NIGHTLY_QUARKUS_BRANCH)
setupMandrelJob()

// Release jobs
setupDeployJob(Folder.RELEASE)
setupPromoteJob(Folder.RELEASE)

// Tools job
KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-runtimes', [
  modules: [ 'kogito-dependencies-bom', 'kogito-build-parent', 'kogito-quarkus-bom', 'kogito-build-no-bom-parent' ],
  compare_deps_remote_poms: [ 'io.quarkus:quarkus-bom' ],
  properties: [ 'version.io.quarkus', 'version.io.quarkus.quarkus-test-maven' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupQuarkusJob(Folder quarkusFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, "kogito-ecosystem", quarkusFolder, "${jenkins_path}/Jenkinsfile.quarkus", 'Kogito Runtimes Quarkus Snapshot')
    jobParams.triggers = [ cron : 'H 4 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', "${quarkusFolder.environment.toName()} check")
        }
    }
}

void setupSonarCloudJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-runtimes', Folder.NIGHTLY_SONARCLOUD, "${jenkins_path}/Jenkinsfile.sonarcloud", 'Kogito Runtimes Daily Sonar')
    jobParams.triggers = [ cron : 'H 20 * * 1-5' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', 'Sonarcloud check')
        }
    }
}

void setupNativeJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-runtimes', Folder.NIGHTLY_NATIVE, "${jenkins_path}/Jenkinsfile.native", 'Kogito Runtimes Native Testing')
    jobParams.triggers = [ cron : 'H 6 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
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

void setupMandrelJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-runtimes', Folder.NIGHTLY_MANDREL, "${jenkins_path}/Jenkinsfile.native", 'Kogito Runtimes Mandrel Testing')
    jobParams.triggers = [ cron : 'H 8 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')

            stringParam('NATIVE_BUILDER_IMAGE', Utils.getMandrelEnvironmentBuilderImage(this), 'Which native builder image to use ?')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', 'Mandrel check')
        }
    }
}

void setupDeployJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-runtimes-deploy', jobFolder, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Runtimes Deploy')
    if (jobFolder.isPullRequest()) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobFolder.isPullRequest()) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            // Build&test information
            booleanParam('SKIP_TESTS', false, 'Skip tests')

            // Release information
            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Set the project version')
            stringParam('DROOLS_VERSION', '', 'Drools version to set')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('REPO_NAME', 'kogito-runtimes')

            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")

            if (jobFolder.isPullRequest()) {
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
                if (jobFolder.isRelease()) {
                    env('NEXUS_RELEASE_URL', "${MAVEN_NEXUS_RELEASE_URL}")
                    env('NEXUS_RELEASE_REPOSITORY_ID', "${MAVEN_NEXUS_RELEASE_REPOSITORY}")
                    env('NEXUS_STAGING_PROFILE_ID', "${MAVEN_NEXUS_STAGING_PROFILE_ID}")
                    env('NEXUS_BUILD_PROMOTION_PROFILE_ID', "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}")
                }
            }
        }
    }
}

void setupPromoteJob(Folder jobFolder) {
    KogitoJobTemplate.createPipelineJob(this, KogitoJobUtils.getBasicJobParams(this, 'kogito-runtimes-promote', jobFolder, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Runtimes Promote'))?.with {
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

        environmentVariables {
            env('REPO_NAME', 'kogito-runtimes')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

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
