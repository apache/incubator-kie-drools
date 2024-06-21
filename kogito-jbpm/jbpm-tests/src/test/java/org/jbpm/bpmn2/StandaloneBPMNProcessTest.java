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
package org.jbpm.bpmn2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jbpm.bpmn2.adhoc.AdHocSubProcessAutoCompleteExpressionModel;
import org.jbpm.bpmn2.adhoc.AdHocSubProcessAutoCompleteExpressionProcess;
import org.jbpm.bpmn2.adhoc.AdHocSubProcessAutoCompleteModel;
import org.jbpm.bpmn2.adhoc.AdHocSubProcessAutoCompleteProcess;
import org.jbpm.bpmn2.adhoc.AdHocSubProcessEmptyCompleteExpressionProcess;
import org.jbpm.bpmn2.adhoc.AdHocTerminateEndEventModel;
import org.jbpm.bpmn2.adhoc.AdHocTerminateEndEventProcess;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventMessageModel;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventMessageProcess;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.instance.impl.humantask.InternalHumanTaskWorkItem;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.ProcessCompletedCountDownProcessEventListener;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.Application;
import org.kie.kogito.event.impl.MessageProducer;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.w3c.dom.Document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StandaloneBPMNProcessTest extends JbpmBpmn2TestCase {

    @Test
    public void testMinimalProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-MinimalProcess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMinimalProcessWithGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-MinimalProcessWithGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("MinimalWithGraphical");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMinimalProcessWithDIGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-MinimalProcessWithDIGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("MinimalWithDIGraphical");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testCompositeProcessWithDIGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-CompositeProcessWithDIGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompositeWithDIGraphical");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testScriptTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/activity/BPMN2-ScriptTask.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("ScriptTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testDataObject() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/data/BPMN2-DataObject.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("DataObject", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/data/BPMN2-Evaluation.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess2() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/data/BPMN2-Evaluation2.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation2", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess3() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/data/BPMN2-Evaluation3.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "john2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation3", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testUserTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/activity/BPMN2-UserTask.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        String varId = "s";
        String varValue = "initialValue";
        params.put(varId, varValue);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testLane() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-Lane.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("Lane");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        Map<String, Object> results = new HashMap<>();
        ((InternalHumanTaskWorkItem) workItem).setActualOwner("mary");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), results);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("SwimlaneActorId")).isEqualTo("mary");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testExclusiveSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-ExclusiveSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "First");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess("ExclusiveSplit", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testExclusiveSplitDefault() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitDefault.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "NotFirst");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess("ExclusiveSplitDefault", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testInclusiveSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-InclusiveSplit.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess("InclusiveSplit", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testInclusiveSplitDefault() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitDefault.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", -5);
        KogitoProcessInstance processInstance = kruntime.startProcess("InclusiveSplitDefault", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    @Disabled
    public void testExclusiveSplitXPath() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitXPath.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(
                        "<myDocument><chapter1>BlaBla</chapter1><chapter2>MoreBlaBla</chapter2></myDocument>".getBytes()));
        Map<String, Object> params = new HashMap<>();
        params.put("x", document);
        params.put("y", "SomeString");
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEventBasedSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testEventBasedSplitBefore() throws Exception {
        // signaling before the split is reached should have no effect
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testEventBasedSplitAfter() throws Exception {
        // signaling the other alternative after one has been selected should
        // have no effect
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // No
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
    }

    @Test
    @Timeout(10)
    public void testEventBasedSplit2() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(2);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit2.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit2");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);

        // Timer
        processInstance = kruntime.startProcess("EventBasedSplit2");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        countDownListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Disabled("process does not complete")
    public void testEventBasedSplit3() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit3.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        // Condition
        processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.getKieSession().insert(jack);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testEventBasedSplit4() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit4.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit4");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Message-YesMessage", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // No
        processInstance = kruntime.startProcess("EventBasedSplit4");
        kruntime.signalEvent("Message-NoMessage", "NoValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit5.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit5");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit5");
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
    }

    @Test
    public void testCallActivity() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivity.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("CallActivity", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y")).isEqualTo("new value");
    }

    @Test
    public void testSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/subprocess/BPMN2-SubProcess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("SubProcess");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/flow/BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2");
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceLoopCharacteristicsProcess", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-ErrorBoundaryEventInterrupting.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("ErrorBoundaryEventInterrupting");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEvent() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventDuration.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getProcessEventManager().addEventListener(processEventListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventDuration");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        processEventListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventInterrupting() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventInterrupting.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getProcessEventManager().addEventListener(processEventListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventInterrupting");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        processEventListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Disabled("Process does not complete.")
    public void testAdHocSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AdHocSubProcess.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("AdHocSubProcess");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNull();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getKieSession().fireAllRules();

        kruntime.signalEvent("Hello2", null, processInstance.getStringId());
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testAdHocSubProcessAutoComplete() throws Exception {
        // this autocomplete when we detect
        // getActivityInstanceAttribute("numberOfActiveInstances") == 0
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<AdHocSubProcessAutoCompleteModel> definition = AdHocSubProcessAutoCompleteProcess.newProcess(app);

        org.kie.kogito.process.ProcessInstance<AdHocSubProcessAutoCompleteModel> instance = definition.createInstance(definition.createModel());
        instance.start();
        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        // adhoc we need to trigger the human task
        instance.triggerNode("_2-1");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAdHocSubProcessAutoCompleteExpression() throws Exception {
        // this autocomplete when we detect
        // getActivityInstanceAttribute("numberOfActiveInstances") == 0
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<AdHocSubProcessAutoCompleteExpressionModel> definition = AdHocSubProcessAutoCompleteExpressionProcess.newProcess(app);
        AdHocSubProcessAutoCompleteExpressionModel model = definition.createModel();
        model.setCounter(3);
        org.kie.kogito.process.ProcessInstance<AdHocSubProcessAutoCompleteExpressionModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        // adhoc we need to trigger the human task
        instance.triggerNode("_2-1");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", 0));

        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAdHocTerminateEndEvent() throws Exception {
        // this autocomplete when we detect
        // terminate end event within adhoc process the adhoc should finish
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<AdHocTerminateEndEventModel> definition = AdHocTerminateEndEventProcess.newProcess(app);
        AdHocTerminateEndEventModel model = definition.createModel();
        model.setComplete(false);
        org.kie.kogito.process.ProcessInstance<AdHocTerminateEndEventModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        // adhoc we need to trigger the human task
        instance.triggerNode("_560E157E-3173-4CFD-9CC6-26676D8B0A02");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAdHocSubProcessEmptyCompleteExpression() throws Exception {
        try {
            Application app = ProcessTestHelper.newApplication();
            AdHocSubProcessEmptyCompleteExpressionProcess.newProcess(app);
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            // there should be build errors
        }
    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignal.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignal");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testIntermediateCatchEventMessage() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventMessage.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventMessage");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerDuration.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getProcessEventManager().addEventListener(processEventListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerDuration");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        processEventListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Disabled("process does not complete")
    public void testIntermediateCatchEventCondition() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventCondition.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        kruntime.getKieSession().insert(person);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testErrorEndEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/event/BPMN2-ErrorEndEvent.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("ErrorEndEvent");
        assertProcessInstanceAborted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testSendTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/task/BPMN2-SendTask.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("SendTask", params);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testReceiveTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/task/BPMN2-ReceiveTask.bpmn2");

        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("ReceiveTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
        assertProcessInstanceCompleted(((KogitoProcessInstance) processInstance).getStringId(), kruntime);
    }

    @Test
    @Disabled("bpmn does not compile")
    public void testConditionalStart() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ConditionalStart.bpmn2");

        Person person = new Person();
        person.setName("jack");
        kruntime.getKieSession().insert(person);
        kruntime.getKieSession().fireAllRules();
        person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);
        kruntime.getKieSession().fireAllRules();
    }

    @Test
    @Timeout(1000)
    public void testTimerStart() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 5);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/start/BPMN2-TimerStart.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

        });

        assertThat(list).isEmpty();
        countDownListener.waitTillCompleted();
        assertThat(list).hasSize(5);
    }

    @Test
    public void testSignalStart() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/start/BPMN2-SignalStart.bpmn2");
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }
        });
        kruntime.signalEvent("MySignal", "NewValue");
        assertThat(list).hasSize(1);
    }

    @Test
    public void testSignalEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/event/BPMN2-SignalEndEvent.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        kruntime.startProcess("SignalEndEvent", params);
    }

    @Test
    public void testMessageStart() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/start/BPMN2-MessageStart.bpmn2");
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }
        });
        kruntime.signalEvent("Message-HelloMessage", "NewValue");
        assertThat(list).hasSize(1);
    }

    @Test
    public void testMessageIntermediateThrow() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Send Task", new SendTaskHandler());
        IntermediateThrowEventMessageProcess definition = (IntermediateThrowEventMessageProcess) IntermediateThrowEventMessageProcess.newProcess(app);
        StringBuilder builder = new StringBuilder();
        definition.setProducer__2(new MessageProducer<String>() {
            @Override
            public void produce(KogitoProcessInstance pi, String eventData) {
                builder.append(eventData);
            }
        });
        IntermediateThrowEventMessageModel model = definition.createModel();
        model.setX("MyValue");

        org.kie.kogito.process.ProcessInstance<IntermediateThrowEventMessageModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(builder.toString()).isEqualTo("MyValue");
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignal.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventSignal", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testNoneIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventNone.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventNone");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testXXEProcessVulnerability() throws Exception {
        Resource processResource = ResourceFactory.newClassPathResource("xxe-protection/BPMN2-XXE-Process.bpmn2");

        File dtdFile = new File("src/test/resources/xxe-protection/external.dtd");
        assertThat(dtdFile).exists();

        String dtdContent = new String(Files.readAllBytes(dtdFile.toPath()));
        dtdContent = dtdContent.replaceAll("@@PATH@@", dtdFile.getParentFile().getAbsolutePath());

        Files.write(dtdFile.toPath(), dtdContent.getBytes("UTF-8"));

        byte[] data = Files.readAllBytes(Paths.get(this.getClass().getResource("/xxe-protection/BPMN2-XXE-Process.bpmn2").getPath()));
        String processAsString = new String(data, "UTF-8");
        // replace place holders with actual paths
        File testFiles = new File("src/test/resources/xxe-protection");

        assertThat(testFiles).exists();

        String path = testFiles.getAbsolutePath();
        processAsString = processAsString.replaceAll("@@PATH@@", path);

        Resource resource = ResourceFactory.newReaderResource(new StringReader(processAsString));
        resource.setSourcePath(processResource.getSourcePath());
        resource.setTargetPath(processResource.getTargetPath());

        kruntime = createKogitoProcessRuntime(resource);
        KogitoProcessInstance processInstance = kruntime.startProcess("async-examples.bp1");

        String var1 = getProcessVarValue(processInstance, "testScript1");
        String var2 = getProcessVarValue(processInstance, "testScript2");

        assertThat(var1).isNull();
        assertThat(var2).isNull();
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testVariableRefInIntermediateThrowEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-WorkingMessageModel.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task", new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task", new DoNothingWorkItemHandler());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("messageContent", "some text");
        KogitoProcessInstance processInstance = kruntime.startProcess("workingMessageModel", parameters);

        assertThat(processInstance).isNotNull();
    }

}
