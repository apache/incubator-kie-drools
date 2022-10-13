/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndEventTest extends JbpmBpmn2TestCase {

    @Test
    public void testImplicitEndParallel() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ParallelSplit.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testErrorEndEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ErrorEndEvent.bpmn2");
        KogitoProcessInstance processInstance = kruntime
                .startProcess("ErrorEndEvent");
        assertProcessInstanceAborted(processInstance);
        assertEquals("error", ((org.jbpm.process.instance.ProcessInstance) processInstance).getOutcome());

    }

    @Test
    public void testEscalationEndEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationEndEvent.bpmn2");
        KogitoProcessInstance processInstance = kruntime
                .startProcess("EscalationEndEvent");
        assertProcessInstanceAborted(processInstance);

    }

    @Test
    public void testSignalEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SignalEndEvent.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        kruntime.startProcess("SignalEndEvent", params);

    }

    @Test
    public void testMessageEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MessageEndEvent.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testMessageEndVerifyDeploymentId() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MessageEndEvent.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);

        InternalKogitoWorkItem workItem = (InternalKogitoWorkItem) handler.getWorkItem();
        assertNotNull(workItem);

        String nodeInstanceId = workItem.getNodeInstanceStringId();
        long nodeId = workItem.getNodeId();
        String deploymentId = workItem.getDeploymentId();

        assertTrue(nodeId > 0);
        assertNotNull(nodeInstanceId);
        assertNull(deploymentId);

        // now set deployment id as part of kruntime's env
        kruntime.getKieRuntime().getEnvironment().set("deploymentId", "testDeploymentId");

        processInstance = kruntime.startProcess("MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);

        workItem = (InternalKogitoWorkItem) handler.getWorkItem();
        assertNotNull(workItem);

        nodeInstanceId = workItem.getNodeInstanceStringId();
        nodeId = workItem.getNodeId();

        assertTrue(nodeId > 0);
        assertNotNull(nodeInstanceId);
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testOnEntryExitScript() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-OnEntryExitScriptProcess.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", myList);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testOnEntryExitNamespacedScript() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-OnEntryExitNamespacedScriptProcess.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", myList);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testOnEntryExitMixedNamespacedScript() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-OnEntryExitMixedNamespacedScriptProcess.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", myList);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testOnEntryExitScriptDesigner() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-OnEntryExitDesignerScriptProcess.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", myList);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());

    }

    @Test
    public void testTerminateWithinSubprocessEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/BPMN2-SubprocessWithParallelSpitTerminate.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-SubprocessWithParallelSpitTerminate");

        kruntime.signalEvent("signal1", null, processInstance.getStringId());

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testTerminateEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ParallelSpitTerminate.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-ParallelSpitTerminate");

        kruntime.signalEvent("Signal 1", null, processInstance.getStringId());

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testSignalEndWithData() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EndEventSignalWithData.bpmn2");
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("src.simpleEndSignal", params);

        assertProcessInstanceCompleted(processInstance);

    }
}
