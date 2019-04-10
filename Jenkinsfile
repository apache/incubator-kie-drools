def submarineBomScm = null

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem8g'
    }
    tools {
        maven 'kie-maven-3.5.4'
        jdk 'kie-jdk1.8'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
    }
    stages {
        stage ('Initialize') {
            steps {
                echo "PATH = ${PATH}"
                echo "M2_HOME = ${M2_HOME}"
                echo "Original branch: $CHANGE_BRANCH"
                echo "Target branch: $CHANGE_TARGET"
                echo "PR author: $CHANGE_AUTHOR_EMAIL"
                script {
                    submarineBomScm = resolveScm(source: github(
                            credentialsId: 'kie-ci',
                            repoOwner: "$CHANGE_AUTHOR",
                            repository: 'submarine-bom',
                            traits: [gitHubBranchDiscovery(1),
                                     [$class: 'org.jenkinsci.plugins.github_branch_source.OriginPullRequestDiscoveryTrait', strategyId: 1],
                                     gitHubForkDiscovery(strategyId: 1, trust: gitHubTrustPermissions())]),
                            ignoreErrors: true,
                            targets: ["$CHANGE_BRANCH"])
                }
            }
        }
        stage ('Build submarine-bom') {
            when {
                not {
                    equals expected: null, actual: submarineBomScm
                }
            }
            steps {
                dir("submarine-bom") {
                    checkout submarineBomScm
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        stage('Build submarine-runtimes') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Build submarine-examples') {
            steps {
                dir("submarine-examples") {
                    checkout(resolveScm(source: github(
                            credentialsId: 'kie-ci',
                            repoOwner: 'kiegroup',
                            repository: 'submarine-examples',
                            traits: [gitHubBranchDiscovery(1),
                                     [$class: 'org.jenkinsci.plugins.github_branch_source.OriginPullRequestDiscoveryTrait', strategyId: 1],
                                     gitHubForkDiscovery(strategyId: 1, trust: gitHubTrustPermissions())]),
                            targets: ["$CHANGE_TARGET"]))
                    sh 'mvn clean install'
                }
            }
        }
    }
    post {
        success {
            junit '**/target/surefire-reports/**/*.xml'
        }
        unstable {
            junit '**/target/surefire-reports/**/*.xml'
            mail to: "$CHANGE_AUTHOR_EMAIL", subject: "Build for PR $BRANCH_NAME failed!", body: "For more details see $BUILD_URL"
        }
        failure {
            mail to: "$CHANGE_AUTHOR_EMAIL", subject: "Build for PR $BRANCH_NAME failed!", body: "For more details see $BUILD_URL"
        }
        cleanup {
            cleanWs()
        }
    }
}