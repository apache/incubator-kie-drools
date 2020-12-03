import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

class JenkinsfileDeploy extends JenkinsPipelineSpecification {

    def Jenkinsfile = null

    void setup() {
        Jenkinsfile = loadPipelineScriptForTest('Jenkinsfile.deploy')
    }

    def '[Jenkinsfile.deploy] isRelease param true' () {
				setup:
        Jenkinsfile.getBinding().setVariable('params', ['RELEASE' : true])
				when:
        def output = Jenkinsfile.isRelease()
				then:
        output
    }

    def '[Jenkinsfile.deploy] isRelease param false' () {
				setup:
        Jenkinsfile.getBinding().setVariable('params', ['RELEASE' : false])
				when:
        def output = Jenkinsfile.isRelease()
				then:
        !output
    }

    def '[Jenkinsfile.deploy] getGitAuthor with param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['GIT_AUTHOR' : 'AUTHOR'])
        when:
        def output = Jenkinsfile.getGitAuthor()
        then:
        output == 'AUTHOR'
    }

    def '[Jenkinsfile.deploy] getBuildBranch with param' () {
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

    def '[Jenkinsfile.deploy] getBotBranch with version param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('env', ['BOT_BRANCH_HASH' : 'HASH'])
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : 'VERSION'])
        when:
        def output = Jenkinsfile.getBotBranch()
        then:
        output == 'VERSION-HASH'
    }

    def '[Jenkinsfile.deploy] getBotAuthor with param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['GIT_AUTHOR_BOT' : 'AUTHOR_BOT'])
        when:
        def output = Jenkinsfile.getBotAuthor()
        then:
        output == 'AUTHOR_BOT'
    }

    def '[Jenkinsfile.deploy] getBotAuthorCredsID with param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['BOT_CREDENTIALS_ID' : 'CREDS_BOT_ID'])
        when:
        def output = Jenkinsfile.getBotAuthorCredsID()
        then:
        output == 'CREDS_BOT_ID'
    }

}
