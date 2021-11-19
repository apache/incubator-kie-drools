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
                id: 'optaplanner',
                dependsOn: 'kogito-runtimes',
                repository: 'optaplanner'
            ], [
                id: 'kogito-apps',
                repository: 'kogito-apps',
                dependsOn: 'optaplanner',
                env : [
                    ADDITIONAL_TIMEOUT: jobFolder.isNative() || jobFolder.isMandrel() ? '360' : '210',
                ]
            ], [
                id: 'kogito-examples',
                repository: 'kogito-examples',
                dependsOn: 'optaplanner',
            ], [
                id: 'optaweb-employee-rostering',
                repository: 'optaweb-employee-rostering',
                dependsOn: 'optaplanner',
            ], [
                id: 'optaweb-vehicle-routing',
                repository: 'optaweb-vehicle-routing',
                dependsOn: 'optaplanner',
            ], [
                id: 'optaplanner-quickstarts',
                repository: 'optaplanner-quickstarts',
                dependsOn: 'optaplanner',
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

// PR checks
KogitoJobUtils.createAllEnvsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig(jobFolder) }

// Nightly jobs
KogitoJobUtils.createAllJobsForArtifactsRepository(this, ['drools', 'kogito'])

// Release jobs
setupDeployJob(Folder.RELEASE)
setupPromoteJob(Folder.RELEASE)

KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'drools', [
  modules: [ 'drools-build-parent' ],
  compare_deps_remote_poms: [ 'io.quarkus:quarkus-bom' ],
  properties: [ 'version.io.quarkus' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupDeployJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'drools-deploy', jobFolder, "${jenkins_path}/Jenkinsfile.deploy", 'Drools Deploy')
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('REPO_NAME', 'drools')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")

            env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")
            env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
            env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")

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

void setupPromoteJob(Folder jobFolder) {
    KogitoJobTemplate.createPipelineJob(this, KogitoJobUtils.getBasicJobParams(this, 'drools-promote', jobFolder, "${jenkins_path}/Jenkinsfile.promote", 'Drools Promote'))?.with {
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

        environmentVariables {
            env('REPO_NAME', 'drools')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")

            env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")
            env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
            env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")

            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")
            env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
            env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
        }
    }
}
