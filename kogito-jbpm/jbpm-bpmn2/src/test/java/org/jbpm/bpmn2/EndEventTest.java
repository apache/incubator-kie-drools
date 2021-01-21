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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.kogito.process.workitems.KogitoWorkItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndEventTest extends JbpmBpmn2TestCase {

    @Test
    public void testImplicitEndParallel() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ParallelSplit.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testErrorEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("ErrorEndEvent");
        assertProcessInstanceAborted(processInstance);
        assertEquals("error", ((org.jbpm.process.instance.ProcessInstance)processInstance).getOutcome());
        
    }

    @Test
    public void testEscalationEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("EscalationEndEvent");
        assertProcessInstanceAborted(processInstance);
        
    }

    @Test
    public void testSignalEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ksession.startProcess("SignalEndEvent", params);
        
    }

    @Test
    public void testMessageEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess(
                "MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);
        
    }
    
    @Test
    public void testMessageEndVerifyDeploymentId() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageEndEvent.bpmn2");
        
        TestWorkItemHandler handler = new TestWorkItemHandler();
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);
        
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertTrue(workItem instanceof KogitoWorkItem );
        
        String nodeInstanceId = (( KogitoWorkItem ) workItem).getNodeInstanceStringId();
        long nodeId = (( KogitoWorkItem ) workItem).getNodeId();
        String deploymentId = (( KogitoWorkItem ) workItem).getDeploymentId();
        
        assertNotNull(nodeId);
        assertTrue(nodeId > 0);
        assertNotNull(nodeInstanceId);
        assertNull(deploymentId);
        
        // now set deployment id as part of ksession's env
        ksession.getEnvironment().set("deploymentId", "testDeploymentId");
        
        processInstance = ksession.startProcess("MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);
        
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertTrue(workItem instanceof KogitoWorkItem );
        
        nodeInstanceId = (( KogitoWorkItem ) workItem).getNodeInstanceStringId();
        nodeId = (( KogitoWorkItem ) workItem).getNodeId();
        
        assertNotNull(nodeId);
        assertTrue(nodeId > 0);
        assertNotNull(nodeInstanceId);
    }

    @Test
    public void testOnEntryExitScript() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-OnEntryExitScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }

    @Test
    public void testOnEntryExitNamespacedScript() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-OnEntryExitNamespacedScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }

    @Test
    public void testOnEntryExitMixedNamespacedScript() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-OnEntryExitMixedNamespacedScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }
    
    @Test
    public void testOnEntryExitScriptDesigner() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-OnEntryExitDesignerScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }
    
    @Test
    public void testTerminateWithinSubprocessEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("subprocess/BPMN2-SubprocessWithParallelSpitTerminate.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("BPMN2-SubprocessWithParallelSpitTerminate");
        
        ksession.signalEvent("signal1", null, processInstance.getId());
        
        assertProcessInstanceCompleted(processInstance);
        
    }
    
    @Test
    public void testTerminateEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ParallelSpitTerminate.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("BPMN2-ParallelSpitTerminate");
        
        ksession.signalEvent("Signal 1", null, processInstance.getId());
        
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testSignalEndWithData() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EndEventSignalWithData.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess("src.simpleEndSignal", params);
        
        assertProcessInstanceCompleted(processInstance);
        
    }
}
