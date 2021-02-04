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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SLAComplianceTest extends JbpmBpmn2TestCase {

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
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue(slaViolated, "SLA was not violated while it is expected");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonProcessMet() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLA.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);        
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
                
        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
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
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue(slaViolated, "SLA was not violated while it is expected");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);

        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());

        NodeInstance userTaskNode = active.iterator().next();

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 0);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);
        
        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 1);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonUserTaskMet() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSLAOnTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);        
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
                
        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());
        
        NodeInstance userTaskNode = active.iterator().next();

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 0);
        // Whereas in memory it is already met
        assertEquals(ProcessInstance.SLA_MET, slaCompliance);
    

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 1);
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
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        ksession.getEnvironment().set("SLATimerMode", "false");
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
 
        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertFalse(slaViolated, "SLA should not violated by timer");

        // simulate external tracking of sla
        kruntime.signalEvent("slaViolation", null, processInstance.getStringId());
        
        slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue(slaViolated, "SLA was not violated while it is expected");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
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
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        ksession.getEnvironment().set("SLATimerMode", "false");

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));

        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertFalse(slaViolated, "SLA should not violated by timer");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());

        NodeInstance userTaskNode = active.iterator().next();

        // simulate external tracking of sla
        kruntime.signalEvent("slaViolation:" + (( KogitoNodeInstance ) userTaskNode).getStringId(), null, processInstance.getStringId());

        slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue(slaViolated, "SLA was not violated while it is expected");

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 0);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 1);
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
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "3s");
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask", parameters);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertTrue(slaViolated, "SLA was not violated while it is expected");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
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
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.addEventListener(listener);
        ksession.getEnvironment().set("SLATimerMode", "false");
        
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        
 
        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertFalse(slaViolated, "SLA should not violated by timer");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_PENDING, slaCompliance);

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);        
        
        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonCatchEventViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignalWithSLAOnEvent.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        ksession.addEventListener(listener);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertTrue(slaViolated, "SLA should be violated by timer");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());
        
        NodeInstance eventNode = active.iterator().next();

        kruntime.signalEvent("MyMessage", null, processInstance.getStringId());
        
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) eventNode, 0);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) eventNode, 1);
        assertEquals(ProcessInstance.SLA_VIOLATED, slaCompliance);
        
        ksession.dispose();
    }
    
    @Test
    public void testSLAonCatchEventNotViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }
            
        };
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignalWithSLAOnEvent.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        ksession.addEventListener(listener);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Collection<NodeInstance> active = ((WorkflowProcessInstance)processInstance).getNodeInstances();
        assertEquals(1, active.size());
        
        NodeInstance eventNode = active.iterator().next();

        kruntime.signalEvent("MyMessage", null, processInstance.getStringId());
        
        assertProcessInstanceFinished(processInstance, ksession);        
        
        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertEquals(ProcessInstance.SLA_NA, slaCompliance);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) eventNode, 1);
        assertEquals(ProcessInstance.SLA_MET, slaCompliance);
        

        boolean slaViolated = latch.await(3, TimeUnit.SECONDS);
        assertFalse(slaViolated, "SLA should not violated by timer");

        ksession.dispose();
    }
    
    /*
     * Helper methods
     */
    
    private int getSLAComplianceForProcessInstance(ProcessInstance processInstance) {
        int slaCompliance = ((org.jbpm.process.instance.ProcessInstance)processInstance).getSlaCompliance();
        
        return slaCompliance;
    }
    
    private int getSLAComplianceForNodeInstance(String processInstanceId, org.jbpm.workflow.instance.NodeInstance nodeInstance, int logType) {
        int slaCompliance = nodeInstance.getSlaCompliance();
        
        return slaCompliance;
    }
}
