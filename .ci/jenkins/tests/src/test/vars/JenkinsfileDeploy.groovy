import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

class JenkinsfileDeploy extends JenkinsPipelineSpecification {

    def Jenkinsfile = null

    void setup() {
        Jenkinsfile = loadPipelineScriptForTest('Jenkinsfile.deploy')
    }

    def '[Jenkinsfile.deploy] isRelease env true' () {
        setup:
        Jenkinsfile.getBinding().setVariable('env', ['RELEASE' : "true"])
        when:
        def output = Jenkinsfile.isRelease()
        then:
        output
    }

    def '[Jenkinsfile.deploy] isRelease env false' () {
        setup:
        Jenkinsfile.getBinding().setVariable('env', ['RELEASE' : "false"])
        when:
        def output = Jenkinsfile.isRelease()
        then:
        !output
    }

    def '[Jenkinsfile.deploy] getGitAuthor' () {
        setup:
        Jenkinsfile.getBinding().setVariable('GIT_AUTHOR', 'AUTHOR')
        when:
        def output = Jenkinsfile.getGitAuthor()
        then:
        output == 'AUTHOR'
    }

    def '[Jenkinsfile.deploy] getBuildBranch' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['BUILD_BRANCH_NAME' : 'BRANCH'])
        when:
        def output = Jenkinsfile.getBuildBranch()
        then:
        output == 'BRANCH'
    }

    def '[Jenkinsfile.deploy] getProjectVersion with param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : 'VERSION'])
        when:
        def output = Jenkinsfile.getProjectVersion()
        then:
        output == 'VERSION'
    }

    def '[Jenkinsfile.deploy] getPRBranch with pr branch param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['KOGITO_PR_BRANCH' : 'PR_BRANCH'])
        when:
        def output = Jenkinsfile.getPRBranch()
        then:
        output == 'PR_BRANCH'
    }

}
