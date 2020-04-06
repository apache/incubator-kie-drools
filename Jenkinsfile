@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem24g && !master'
    }
    tools {
        maven 'kie-maven-3.5.4'
        jdk 'kie-jdk1.8'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 600, unit: 'MINUTES')
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'printenv'

            }
        }
        stage('Build projects') {
            steps {
                dir("droolsjbpm-build-bootstrap") {
                    script {
                        def changeAuthor = env.CHANGE_AUTHOR ?: env.ghprbPullAuthorLogin
                        def changeBranch = env.CHANGE_BRANCH ?: env.ghprbSourceBranch
                        def changeTarget = env.CHANGE_TARGET ?: env.ghprbTargetBranch

                        githubscm.checkoutIfExists('droolsjbpm-build-bootstrap', "${changeAuthor}", "${changeBranch}", 'kiegroup', "${changeTarget}")
                        
                        def file =  (JOB_NAME =~ /\/[a-z,A-Z\-]*\.downstream\.production/).find() ? 'downstream.production.stages' :
                                    (JOB_NAME =~ /\/[a-z,A-Z\-]*\.downstream/).find() ? 'downstream.stages' :
                                    'upstream.stages'
                        println "Loading ${file} file..."
                        load("${file}")
                    }
                }
            }
        }        
    }
    post {
        unstable {
            script {
                mailer.sendEmailFailure()
            }
        }
        failure {
            script {
                mailer.sendEmailFailure()
            }
        }
        always {
            //junit '**/target/surefire-reports/**/*.xml'
            cleanWs()
        }
    }
}
