/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
