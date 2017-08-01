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

package org.jbpm.kie.services.impl.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.test.KModuleDeploymentServiceTest;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.kie.test.util.CountDownListenerFactory;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.jbpm.services.api.admin.ProcessNode;
import org.jbpm.services.api.admin.TimerInstance;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class ProcessInstanceAdminServiceImplTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);
    protected static final String ADMIN_ARTIFACT_ID = "test-admin";
    protected static final String ADMIN_GROUP_ID = "org.jbpm.test";
    protected static final String ADMIN_VERSION_V1 = "1.0.0";
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private KModuleDeploymentUnit deploymentUnit;
    private Long processInstanceId = null;
    
    protected ProcessInstanceAdminService processAdminService;

    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        
        // version 1 of kjar
        ReleaseId releaseId = ks.newReleaseId(ADMIN_GROUP_ID, ADMIN_ARTIFACT_ID, ADMIN_VERSION_V1);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
        processes.add("repo/processes/errors/BPMN2-BrokenScriptTask.bpmn2");
        processes.add("repo/processes/errors/BPMN2-UserTaskWithRollback.bpmn2");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/admin", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);

        processAdminService = new ProcessInstanceAdminServiceImpl();
        ((ProcessInstanceAdminServiceImpl) processAdminService).setProcessService(processService);
        ((ProcessInstanceAdminServiceImpl) processAdminService).setRuntimeDataService(runtimeDataService);
        ((ProcessInstanceAdminServiceImpl) processAdminService).setCommandService(new TransactionalCommandService(emf));
        ((ProcessInstanceAdminServiceImpl) processAdminService).setIdentityProvider(identityProvider);
        
        // now let's deploy to runtime both kjars
        deploymentUnit = new KModuleDeploymentUnit(ADMIN_GROUP_ID, ADMIN_ARTIFACT_ID, ADMIN_VERSION_V1);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (processInstanceId != null) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);
                
                ProcessInstance pi = processService.getProcessInstance(processInstanceId);      
                assertNull(pi);
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it might already be completed/aborted
            }
        }
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }
        close();
        CountDownListenerFactory.clear();
    }
    
    
    public void setProcessAdminService(ProcessInstanceAdminService processAdminService) {
        this.processAdminService = processAdminService;
    }

    @Test
    public void testGetNodes() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        Collection<ProcessNode> processNodes = processAdminService.getProcessNodes(processInstanceId);
        assertNotNull(processNodes);
        assertEquals(8, processNodes.size());
        
        Map<String, String> mappedNodes = processNodes.stream().collect(Collectors.toMap(ProcessNode::getNodeName, ProcessNode::getNodeType));
        assertEquals("StartNode", mappedNodes.get("Start"));
        assertEquals("HumanTaskNode", mappedNodes.get("Write a Document"));
        assertEquals("Split", mappedNodes.get("Review and Translate"));
        assertEquals("HumanTaskNode", mappedNodes.get("Translate Document"));
        assertEquals("HumanTaskNode", mappedNodes.get("Review Document"));
        assertEquals("Join", mappedNodes.get("Reviewed and Translated"));
        assertEquals("ActionNode", mappedNodes.get("Report"));
        assertEquals("EndNode", mappedNodes.get("End"));
    }
    
    @Test
    public void testCancelAndTriger() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        Collection<NodeInstanceDesc> activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc active = activeNodes.iterator().next();       
        assertEquals("Write a Document", active.getName());
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
        
        processAdminService.cancelNodeInstance(processInstanceId, active.getId());
        
        activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(0, activeNodes.size());
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        
        Collection<ProcessNode> processNodes = processAdminService.getProcessNodes(processInstanceId);
        ProcessNode writeDocNode = processNodes.stream().filter(pn -> pn.getNodeName().equals(active.getName())).findFirst().orElse(null);
        
        processAdminService.triggerNode(processInstanceId, writeDocNode.getNodeId());
        activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testRetriggerNodeInstance() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        Collection<NodeInstanceDesc> activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc active = activeNodes.iterator().next();       
        assertEquals("Write a Document", active.getName());
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        
        processAdminService.retriggerNodeInstance(processInstanceId, active.getId());
                
        activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc activeRetriggered = activeNodes.iterator().next();        
        assertFalse(active.getId().longValue() == activeRetriggered.getId().longValue());
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
        TaskSummary taskRetriggered = tasks.get(0);
        
        assertFalse(task.getId().longValue() == taskRetriggered.getId().longValue());
    }
    
    @Test
    public void testCancelAndTrigerAnotherNode() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        Collection<NodeInstanceDesc> activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc active = activeNodes.iterator().next();       
        assertEquals("Write a Document", active.getName());
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
        
        processAdminService.cancelNodeInstance(processInstanceId, active.getId());
        
        activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(0, activeNodes.size());
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        
        Collection<ProcessNode> processNodes = processAdminService.getProcessNodes(processInstanceId);
        ProcessNode writeDocNode = processNodes.stream().filter(pn -> pn.getNodeName().equals("Report")).findFirst().orElse(null);
        
        processAdminService.triggerNode(processInstanceId, writeDocNode.getNodeId());
        activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(0, activeNodes.size());
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState().intValue());
        
        processInstanceId = null;
    }
    
    @Test
    public void testTrigerLastActionNode() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        Collection<NodeInstanceDesc> activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc active = activeNodes.iterator().next();       
        assertEquals("Write a Document", active.getName());
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
                
        Collection<ProcessNode> processNodes = processAdminService.getProcessNodes(processInstanceId);
        ProcessNode writeDocNode = processNodes.stream().filter(pn -> pn.getNodeName().equals("Report")).findFirst().orElse(null);
        
        processAdminService.triggerNode(processInstanceId, writeDocNode.getNodeId());
        activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(0, activeNodes.size());
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState().intValue());
        
        processInstanceId = null;
    }
    
    @Test(timeout=10000)
    public void testUpdateTimer() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "IntermediateCatchEvent");
        assertNotNull(processInstanceId);
        long scheduleTime = System.currentTimeMillis();
        
        Collection<NodeInstanceDesc> activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc active = activeNodes.iterator().next();       
        assertEquals("timer", active.getName());
        
        Collection<TimerInstance> timers = processAdminService.getTimerInstances(processInstanceId);
        assertNotNull(timers);
        assertEquals(1, timers.size());
        
        TimerInstance timer = timers.iterator().next();
        assertNotNull(timer.getActivationTime());
        assertNotNull(timer.getDelay());
        assertNotNull(timer.getNextFireTime());
        assertNotNull(timer.getProcessInstanceId());
        assertNotNull(timer.getSessionId());
        assertNotNull(timer.getTimerId());
        assertNotNull(timer.getTimerName());
        // thread sleep to test the different in the time timer spent after upgrade
        // not to wait for any job to be done
        Thread.sleep(1000);
        
        processAdminService.updateTimer(processInstanceId, timer.getTimerId(), 3, 0, 0);
        
        CountDownListenerFactory.getExisting("processAdminService").waitTillCompleted();
        long fireTime = System.currentTimeMillis();        
        long expirationTime = fireTime - scheduleTime;
        //since the update of timer was including time already spent (thread sleep above) then it must wait less than 4 secs
        assertTrue(expirationTime < 4000);
       
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState().intValue());
        
        processInstanceId = null;
    }
    
    
    @Test(timeout=10000)
    public void testUpdateTimerRelative() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "IntermediateCatchEvent");
        assertNotNull(processInstanceId);
        long scheduleTime = System.currentTimeMillis();
        
        Collection<NodeInstanceDesc> activeNodes = processAdminService.getActiveNodeInstances(processInstanceId);
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        
        NodeInstanceDesc active = activeNodes.iterator().next();       
        assertEquals("timer", active.getName());
        
        Collection<TimerInstance> timers = processAdminService.getTimerInstances(processInstanceId);
        assertNotNull(timers);
        assertEquals(1, timers.size());
        
        TimerInstance timer = timers.iterator().next();
        assertNotNull(timer.getActivationTime());
        assertNotNull(timer.getDelay());
        assertNotNull(timer.getNextFireTime());
        assertNotNull(timer.getProcessInstanceId());
        assertNotNull(timer.getSessionId());
        assertNotNull(timer.getTimerId());
        assertNotNull(timer.getTimerName());
        // thread sleep to test the different in the time timer spent after upgrade
        // not to wait for any job to be done
        Thread.sleep(1000);
        
        processAdminService.updateTimerRelative(processInstanceId, timer.getTimerId(), 3, 0, 0);
        
        CountDownListenerFactory.getExisting("processAdminService").waitTillCompleted();
        long fireTime = System.currentTimeMillis();
        //since the update of timer was relative (to current time) then it must wait at least 3 secs
        long expirationTime = fireTime - scheduleTime;
        assertTrue(expirationTime > 3000);
       
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(processInstanceId);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState().intValue());
        
        processInstanceId = null;
    }
    
    @Test
    public void testErrorHandlingOnScriptTask() {
        
        try {
            processService.startProcess(deploymentUnit.getIdentifier(), "BrokenScriptTask");
        } catch (Exception e) {
            // expected as this is broken script process
        }
        
        List<ExecutionError> errors = processAdminService.getErrors(true, new QueryContext());
        assertNotNull(errors);
        assertEquals(1, errors.size());
        
        ExecutionError error = errors.get(0);
        assertNotNull(error);
        assertFalse(error.isAcknowledged());
        
        processAdminService.acknowledgeError(error.getErrorId());
        
        errors = processAdminService.getErrors(true, new QueryContext());
        assertNotNull(errors);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);
        assertTrue(error.isAcknowledged());
    }

    /*
     * Helper methods 
     */
    @Override
    protected List<ObjectModel> getProcessListeners() {
        List<ObjectModel> listeners = super.getProcessListeners();
        
        listeners.add(new ObjectModel("mvel", "org.jbpm.kie.test.util.CountDownListenerFactory.get(\"processAdminService\", \"timer\", 1)"));
        
        return listeners;
    }
    
    protected boolean createDescriptor() {
        return true;
    }
}
