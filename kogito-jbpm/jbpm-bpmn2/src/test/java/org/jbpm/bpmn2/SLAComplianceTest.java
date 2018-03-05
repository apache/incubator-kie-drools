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

package org.jbpm.bpmn2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkflowProcessInstance;

@RunWith(Parameterized.class)
public class SLAComplianceTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };
    

    private KieSession ksession;    

    public SLAComplianceTest(boolean persistence) throws Exception {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            abortProcessInstances(ksession);
            ksession.dispose();
            ksession = null;
        }
    }

    @Test
    public void testSLAonProcessViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLA.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue("SLA was not violated while it is expected", slaViolated);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonProcessMet() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLA.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);        
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
                
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_MET, slaCompliance);
        
        ksession.dispose();
    }
    
    
    @Test
    public void testSLAonUserTaskViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLAOnTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue("SLA was not violated while it is expected", slaViolated);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());
        
        NodeInstance userTaskNode = active.iterator().next();
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);
        
        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonUserTaskMet() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLAOnTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);        
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
                
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());
        
        NodeInstance userTaskNode = active.iterator().next();
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);
        
        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode);
        assertEquals(ProcessInstance.SLA_MET, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonProcessViolatedExternalTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLA.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        ksession.getEnvironment().set("SLATimerMode", "false");
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
 
        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertFalse("SLA should not violated by timer", slaViolated);
        
        // simulate external tracking of sla
        ksession.signalEvent("slaViolation", null, processInstance.getId());
        
        slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue("SLA was not violated while it is expected", slaViolated);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonUserTaskViolatedExternalTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLAOnTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        ksession.getEnvironment().set("SLATimerMode", "false");
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertFalse("SLA should not violated by timer", slaViolated);
        

        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());
        
        NodeInstance userTaskNode = active.iterator().next();
        
        // simulate external tracking of sla
        ksession.signalEvent("slaViolation:" + userTaskNode.getId(), null, processInstance.getId());
        
        slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue("SLA was not violated while it is expected", slaViolated);
        
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);
        
        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonProcessViolatedWithExpression() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLAExpr.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "3s");
        
        ProcessInstance processInstance = ksession.startProcess("UserTask", parameters);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue("SLA was not violated while it is expected", slaViolated);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonProcessViolatedNoTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLA.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        ksession.getEnvironment().set("SLATimerMode", "false");
        
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
 
        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertFalse("SLA should not violated by timer", slaViolated);
        
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_PENDING, slaCompliance);
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    /*
     * Helper methods
     */
    
    private int getSLAComplianceForProcessInstance(ProcessInstance processInstance) {
        int slaCompliance = -1;
        if (sessionPersistence) {
            ProcessInstanceLog log = logService.findProcessInstance(processInstance.getId());
            if (log != null) {
                slaCompliance = log.getSlaCompliance();
            }
        } else {
            slaCompliance = ((org.jbpm.process.instance.ProcessInstance)processInstance).getSlaCompliance();
        }
        return slaCompliance;
    }
    
    private int getSLAComplianceForNodeInstance(long processInstanceId, org.jbpm.workflow.instance.NodeInstance nodeInstance) {
        int slaCompliance = -1;
        if (sessionPersistence) {
            List<NodeInstanceLog> logs = logService.findNodeInstances(processInstanceId);
            if (logs != null) {
                
                for (NodeInstanceLog log : logs) {
                    if (log.getType() == NodeInstanceLog.TYPE_EXIT && log.getNodeInstanceId().equals(String.valueOf(nodeInstance.getId()))) {
                        slaCompliance = log.getSlaCompliance();
                        break;
                    }
                }
            }
        } else {
            slaCompliance = nodeInstance.getSlaCompliance();
        }
        return slaCompliance;
    }
}