import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification

class JenkinsfilePromote extends JenkinsPipelineSpecification {

    def Jenkinsfile = null

    void setup() {
        Jenkinsfile = loadPipelineScriptForTest('Jenkinsfile.promote')
        Jenkinsfile.getBinding().setVariable('PROPERTIES_FILE_NAME', 'deployment.properties')
        Jenkinsfile.getBinding().setVariable('env', ['REPO_NAME' : 'kogito-runtimes'])
    }

    def '[Jenkinsfile.promote] readDeployProperties: no DEPLOY_BUILD_URL parameter' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['DEPLOY_BUILD_URL' : ''])
        when:
        Jenkinsfile.readDeployProperties()
        then:
        0 * getPipelineMock('sh')('wget artifact/deployment.properties -O deployment.properties')
        0 * getPipelineMock('readProperties').call(['file':'deployment.properties'])
    }

    def '[Jenkinsfile.promote] readDeployProperties: DEPLOY_BUILD_URL parameter' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['DEPLOY_BUILD_URL' : 'https://www.google.ca/'])
        when:
        Jenkinsfile.readDeployProperties()
        then:
        1 * getPipelineMock('sh')('wget https://www.google.ca/artifact/deployment.properties -O deployment.properties')
        1 * getPipelineMock('readProperties').call(['file':'deployment.properties'])
    }

    def '[Jenkinsfile.promote] hasDeployProperty: deployProperties has' () {
        setup:
        Jenkinsfile.getBinding().setVariable('deployProperties', ['foo' : 'bar'])
        when:
        def has = Jenkinsfile.hasDeployProperty('foo')
        then:
        has == true
    }

    def '[Jenkinsfile.promote] hasDeployProperty: deployProperties does not have' () {
        setup:
        Jenkinsfile.getBinding().setVariable('deployProperties', [:])
        when:
        def has = Jenkinsfile.hasDeployProperty('foo')
        then:
        has == false
    }

    def '[Jenkinsfile.promote] getDeployProperty: deployProperties has' () {
        setup:
        Jenkinsfile.getBinding().setVariable('deployProperties', ['foo' : 'bar'])
        when:
        def fooValue = Jenkinsfile.getDeployProperty('foo')
        then:
        fooValue == 'bar'
    }

    def '[Jenkinsfile.promote] getDeployProperty: deployProperties does not have' () {
        setup:
        Jenkinsfile.getBinding().setVariable('deployProperties', [:])
        when:
        def fooValue = Jenkinsfile.getDeployProperty('foo')
        then:
        fooValue == ''
    }

    def '[Jenkinsfile.promote] getParamOrDeployProperty: param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['FOO' : 'BAR'])
        Jenkinsfile.getBinding().setVariable('deployProperties', ['foo' : 'bar'])
        when:
        def fooValue = Jenkinsfile.getParamOrDeployProperty('FOO', 'foo')
        then:
        fooValue == 'BAR'
    }

    def '[Jenkinsfile.promote] getParamOrDeployProperty: deploy property' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['FOO' : ''])
        Jenkinsfile.getBinding().setVariable('deployProperties', ['foo' : 'bar'])
        when:
        def fooValue = Jenkinsfile.getParamOrDeployProperty('FOO', 'foo')
        then:
        fooValue == 'bar'
    }

    //////////////////////////////////////////////////////////////////////////////
    // Getter / Setter
    //////////////////////////////////////////////////////////////////////////////

    def '[Jenkinsfile.promote] isRelease env true' () {
        setup:
        Jenkinsfile.getBinding().setVariable('env', ['RELEASE' : "true"])
        when:
        def value = Jenkinsfile.isRelease()
        then:
        value
    }

    def '[Jenkinsfile.promote] isRelease env false' () {
        setup:
        Jenkinsfile.getBinding().setVariable('env', ['RELEASE' : "false"])
        when:
        def value = Jenkinsfile.isRelease()
        then:
        !value
    }

    def '[Jenkinsfile.promote] getSnapshotVersion' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : '1.0.0'])
        explicitlyMockPipelineVariable('util')
        getPipelineMock('util.getNextVersion')('1.0.0', 'micro') >> { return '1.0.1-SNAPSHOT' }
        when:
        def value = Jenkinsfile.getSnapshotVersion()
        then:
        value == '1.0.1-SNAPSHOT'
    }

    def '[Jenkinsfile.promote] getProjectVersion: PROJECT_VERSION param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : 'PROJECT_VERSION'])
        Jenkinsfile.getBinding().setVariable('deployProperties', ['project.version' : 'project.version'])
        when:
        def value = Jenkinsfile.getProjectVersion()
        then:
        value == 'PROJECT_VERSION'
    }

    def '[Jenkinsfile.promote] getProjectVersion: no PROJECT_VERSION param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : ''])
        Jenkinsfile.getBinding().setVariable('deployProperties', ['project.version' : 'project.version'])
        when:
        def value = Jenkinsfile.getProjectVersion()
        then:
        value == 'project.version'
    }

    def '[Jenkinsfile.promote] getProjectVersion: no value' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : ''])
        Jenkinsfile.getBinding().setVariable('deployProperties', [:])
        when:
        def value = Jenkinsfile.getProjectVersion()
        then:
        value == ''
    }

    def '[Jenkinsfile.promote] getGitTag: GIT_TAG param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['GIT_TAG' : 'tag', 'PROJECT_VERSION' : 'version'])
        when:
        def value = Jenkinsfile.getGitTag()
        then:
        value == 'tag'
    }

    def '[Jenkinsfile.promote] getGitTag: no GIT_TAG param' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['GIT_TAG' : '', 'PROJECT_VERSION' : 'version'])
        when:
        def value = Jenkinsfile.getGitTag()
        then:
        value == 'version'
    }

    def '[Jenkinsfile.promote] getBuildBranch()' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['BUILD_BRANCH_NAME' : 'param branch'])
        when:
        def value = Jenkinsfile.getBuildBranch()
        then:
        value == 'param branch'
    }

    def '[Jenkinsfile.promote] getGitAuthor' () {
        setup:
        Jenkinsfile.getBinding().setVariable('env', ['GIT_AUTHOR' : 'GIT_AUTHOR'])
        when:
        def value = Jenkinsfile.getGitAuthor()
        then:
        value == 'GIT_AUTHOR'
    }

    def '[Jenkinsfile.promote] getDeployPrLink' () {
        setup:
        Jenkinsfile.getBinding().setVariable('deployProperties', ['kogito-runtimes.pr.link' : 'repo.pr.link'])
        when:
        def value = Jenkinsfile.getDeployPrLink()
        then:
        value == 'repo.pr.link'
    }

    def '[Jenkinsfile.promote] getDeployPrLink: no value' () {
        setup:
        Jenkinsfile.getBinding().setVariable('deployProperties', [:])
        when:
        def value = Jenkinsfile.getDeployPrLink()
        then:
        value == ''
    }

    def '[Jenkinsfile.promote] getPipelinePrLink' () {
        setup:
        Jenkinsfile.getBinding().setVariable('pipelineProperties', ['kogito-runtimes.pr.link' : 'repo.pr.link'])
        when:
        def value = Jenkinsfile.getPipelinePrLink()
        then:
        value == 'repo.pr.link'
    }

    def '[Jenkinsfile.promote] getPipelinePrLink: no value' () {
        setup:
        Jenkinsfile.getBinding().setVariable('pipelineProperties', [:])
        when:
        def value = Jenkinsfile.getPipelinePrLink()
        then:
        value == null
    }

    def '[Jenkinsfile.promote] setPipelinePrLink' () {
        setup:
        Jenkinsfile.getBinding().setVariable('pipelineProperties', [:])
        when:
        Jenkinsfile.setPipelinePrLink('value')
        then:
        Jenkinsfile.getPipelinePrLink() == 'value'
    }

    def '[Jenkinsfile.promote] getSnapshotBranch' () {
        setup:
        Jenkinsfile.getBinding().setVariable('params', ['PROJECT_VERSION' : '1.0.0'])
        explicitlyMockPipelineVariable('util')
        getPipelineMock('util.getNextVersion')('1.0.0', 'micro') >> { return '1.0.1-SNAPSHOT' }
        Jenkinsfile.getBinding().setVariable('env', ['BOT_BRANCH_HASH' : 'anything'])
        when:
        def value = Jenkinsfile.getSnapshotBranch()
        then:
        value == '1.0.1-snapshot-anything'
    }

}
