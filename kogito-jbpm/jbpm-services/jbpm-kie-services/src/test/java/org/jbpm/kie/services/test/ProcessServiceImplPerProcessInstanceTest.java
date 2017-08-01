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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.command.runtime.process.GetProcessInstanceCommand;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class ProcessServiceImplPerProcessInstanceTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);

    private static KModuleDeploymentUnit deploymentUnit;

    private static final String PROCESS_ID_HUMAN_TASK = "org.jbpm.writedocument";
    private static final String PROCESS_ID_SIGNAL = "org.jbpm.signal";

    @BeforeClass
    public static void prepareDeploymentUnit() {
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentUnit.setStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
    }

    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/signal.bpmn");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        deploymentService.deploy(deploymentUnit);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        try {
            deploymentService.undeploy(deploymentUnit);
        } catch (Exception e) {
            // do nothing in case of some failed tests to avoid next test to fail as well
        }
        close();
    }

    @Test
    public void testAbortAlreadyAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.abortProcessInstance(processInstanceId);
            fail("Aborting of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testSignalProcessInstanceAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_SIGNAL);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.signalProcessInstance(processInstanceId, "MySignal", null);
            fail("Signalling of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetProcessInstanceAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_SIGNAL);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.getProcessInstance(processInstanceId);
            fail("Getting of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetProcessInstanceWithCorrelationKeyAbortedProcess() {
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_SIGNAL, key);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        ProcessInstance processInstance = processService.getProcessInstance(key);
        assertNull(processInstance);
    }

    @Test
    public void testSetProcessVariableAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.setProcessVariable(processInstanceId, "approval_reviewComment", "updated review comment");
            fail("Setting process variable of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testSetProcessVariablesAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            // and lastly let's update both variables
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("approval_document", "updated document");
            params.put("approval_reviewComment", "final review");

            processService.setProcessVariables(processInstanceId, params);
            fail("Setting process variables of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetProcessInstanceVariableAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.getProcessInstanceVariable(processInstanceId, "approval_document");
            fail("Getting process variable of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetProcessInstanceVariablesAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.getProcessInstanceVariables(processInstanceId);
            fail("Getting process variables of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetAvailableSignalsAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_SIGNAL);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.getAvailableSignals(processInstanceId);
            fail("Getting available signals of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetWorkItemByProcessInstanceAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.getWorkItemByProcessInstance(processInstanceId);
            fail("Getting work items of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testCompleteWorkItemAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);

        List<WorkItem> workItems = processService.getWorkItemByProcessInstance(processInstanceId);
        assertEquals(1, workItems.size());

        processService.abortProcessInstance(processInstanceId);

        try {
            processService.completeWorkItem(workItems.get(0).getId(), null);
            fail("Completing work item of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testAbortWorkItemAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);

        List<WorkItem> workItems = processService.getWorkItemByProcessInstance(processInstanceId);
        assertEquals(1, workItems.size());

        processService.abortProcessInstance(processInstanceId);

        try {
            processService.abortWorkItem(workItems.get(0).getId());
            fail("Aborting work item of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetWorkItemAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);

        List<WorkItem> workItems = processService.getWorkItemByProcessInstance(processInstanceId);
        assertEquals(1, workItems.size());

        processService.abortProcessInstance(processInstanceId);

        try {
            processService.getWorkItem(workItems.get(0).getId());
            fail("Getting work item of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testExecuteCommandOnAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.execute(deploymentUnit.getIdentifier(), new GetProcessInstanceCommand(processInstanceId));
            fail("Executing command on already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testExecuteCommandWithContextOnAbortedProcess() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_HUMAN_TASK);
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(processInstanceId);

        try {
            processService.execute(deploymentUnit.getIdentifier(), ProcessInstanceIdContext.get(processInstanceId), new GetProcessInstanceCommand(processInstanceId));
            fail("Executing command with context on already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }
}
