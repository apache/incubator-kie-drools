/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.naming.NamingException;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.remote.ejb.test.TestKjars;
import org.jbpm.remote.ejb.test.client.EJBClient;
import org.jbpm.remote.ejb.test.maven.MavenProject;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.BeforeClass;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.query.QueryContext;

public class EGetProcessInstanceTest extends RemoteEjbTest {

    @BeforeClass
    public static void deployProject() {
        MavenProject kjar = TestKjars.BPMN_BUILD_TEST;
        if (!ejb.isDeployed(kjar.getGav())) {
            ejb.deploy(kjar.getGroupId(), kjar.getArtifactId(), kjar.getVersion(), null, null, SESSION_STRATEGY);
        }
    }

    @Test()
    public void testGetProcessInstanceById() {
        long processInstanceId = ejb.startProcessSimple(ProcessDefinitions.SCRIPT_TASK);

        ProcessInstanceDesc log = ejb.getProcessInstanceById(processInstanceId);

        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getId()).isEqualTo(processInstanceId);
        Assertions.assertThat(log.getProcessId()).isEqualTo(ProcessDefinitions.SCRIPT_TASK);
        Assertions.assertThat(log.getState().intValue()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test()
    public void getProcessInstancesByProcessName() {
        startProcess("org.jboss.qa.bpms.HumanTask", new HashMap<>(), 3);
        startProcess("org.jboss.qa.bpms.HumanTaskWithOwnType", new HashMap<>(), 5);

        List<Integer> states = new ArrayList<>();
        states.add(ProcessInstance.STATE_ACTIVE);
        List<ProcessInstanceDesc> foundList = ejb.getProcessInstancesByProcessName(states, "HumanTask", null, new QueryContext());

        Assertions.assertThat(foundList).hasSize(3);
        for (ProcessInstanceDesc pid : foundList) {
            Assertions.assertThat(pid.getProcessName()).isEqualTo("HumanTask");
            Assertions.assertThat(pid.getProcessId()).isEqualTo("org.jboss.qa.bpms.HumanTask");
        }
    }

    @Test()
    public void getProcessInstancesByDeploymentId() throws NamingException {
        EJBClient ejbClient2 = new EJBClient(TestKjars.BPMN_BUILD_TEST.getGav());

        startProcess("org.jboss.qa.bpms.HumanTask", new HashMap<>(), 3);
        startProcess("org.jboss.qa.bpms.HumanTaskWithOwnType", new HashMap<>(), 5);
        ejbClient2.startProcess("bpmnBuildTest.myProcess");

        List<Integer> states = new ArrayList<>();
        states.add(ProcessInstance.STATE_ACTIVE);
        List<ProcessInstanceDesc> foundList = ejb.getProcessInstancesByDeploymentId(TestKjars.INTEGRATION.getGav(), states, new QueryContext());

        Assertions.assertThat(foundList).hasSize(8);
        for (ProcessInstanceDesc pid : foundList) {
            Assertions.assertThat(pid.getProcessName()).isIn("HumanTask", "HumanTaskWithOwnType");
        }
    }

    @Test()
    public void getProcessInstancesByProcessDefinition() {
        startProcess("org.jboss.qa.bpms.HumanTask", new HashMap<>(), 3);
        startProcess("org.jboss.qa.bpms.HumanTaskWithOwnType", new HashMap<>(), 5);

        // Please note that this list contains all instances (RUNNING/COMPLETED/ABORTED) of the 
        // org.jboss.qa.bpms.HumanTask process definition. To make it contains all our instances
        // we must filter through it first. See enhancement request BZ-1179004 for more detail.
        QueryContext queryContext = new QueryContext(0, Integer.MAX_VALUE, "log.status", true);
        List<ProcessInstanceDesc> allList = ejb.getProcessInstancesByProcessDefinition("org.jboss.qa.bpms.HumanTask", queryContext);

        // Let's find our instances. We expect to find exactly 3 active instances. The exact number
        // is ensured by the RemoteEjbClient.abortAllProcesses() method.
        List<ProcessInstanceDesc> foundList = new ArrayList<>();
        for (ProcessInstanceDesc processInstanceDesc : allList) {
            if (processInstanceDesc.getState() == ProcessInstance.STATE_ACTIVE) {
                foundList.add(processInstanceDesc);
            }
        }

        Assertions.assertThat(foundList).hasSize(3);
        for (ProcessInstanceDesc pid : foundList) {
            Assertions.assertThat(pid.getProcessName()).isIn("HumanTask");
        }
    }

}
