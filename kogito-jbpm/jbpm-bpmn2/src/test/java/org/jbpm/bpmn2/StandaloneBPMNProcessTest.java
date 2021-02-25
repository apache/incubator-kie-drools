/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.drools.core.util.IoUtils;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.bpmn2.objects.ExceptionService;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.w3c.dom.Document;

import static org.assertj.core.api.Assertions.assertThat;

public class StandaloneBPMNProcessTest extends JbpmBpmn2TestCase {

    @Test
    public void testMinimalProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMinimalProcessWithGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcessWithGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMinimalProcessWithDIGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcessWithDIGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testCompositeProcessWithDIGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CompositeProcessWithDIGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Composite");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testScriptTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ScriptTask.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("ScriptTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testDataObject() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataObject.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EvaluationProcess.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EvaluationProcess2.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess3() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EvaluationProcess3.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "john2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testUserTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTask.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        Map<String, Object> params = new HashMap<>();
        String varId = "s";
        String varValue = "initialValue";
        params.put(varId, varValue);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testLane() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-Lane.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        Map<String, Object> results = new HashMap<>();
        ((HumanTaskWorkItemImpl) workItem).setActualOwner("mary");
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), results);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("SwimlaneActorId")).isEqualTo("mary");
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testExclusiveSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplit.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "First");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testExclusiveSplitDefault() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitDefault.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "NotFirst");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testInclusiveSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplit.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testInclusiveSplitDefault() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitDefault.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", -5);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    @Disabled
    public void testExclusiveSplitXPath() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitXPath.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
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
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testEventBasedSplitBefore() throws Exception {
        // signaling before the split is reached should have no effect
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testEventBasedSplitAfter() throws Exception {
        // signaling the other alternative after one has been selected should
        // have no effect
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // No
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
    }

    @Test
    @Timeout(10)
    public void testEventBasedSplit2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit2.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Timer
        processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        countDownListener.waitTillCompleted();
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Disabled("process does not complete")
    public void testEventBasedSplit3() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit3.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        // Condition
        processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.getKieSession().insert(jack);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testEventBasedSplit4() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit4.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Message-YesMessage", "YesValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        kruntime.signalEvent("Message-NoMessage", "NoValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventBasedSplit5.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(kruntime);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
    }

    @Test
    public void testCallActivity() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivity.bpmn2", "BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y")).isEqualTo("new value");
    }

    @Test
    public void testSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SubProcess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("SubProcess");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2");
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
        kruntime = createKogitoProcessRuntime("BPMN2-ErrorBoundaryEventInterrupting.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("ErrorBoundaryEvent");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEvent() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        kruntime = createKogitoProcessRuntime("BPMN2-TimerBoundaryEventDuration.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventInterrupting() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        kruntime = createKogitoProcessRuntime("BPMN2-TimerBoundaryEventInterrupting.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Disabled("Process does not complete.")
    public void testAdHocSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AdHocSubProcess.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("AdHocSubProcess");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNull();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getKieSession().fireAllRules();

        kruntime.signalEvent("Hello2", null, processInstance.getStringId());
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Disabled("Process does not complete.")
    public void testAdHocSubProcessAutoComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AdHocSubProcessAutoComplete.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("AdHocSubProcess");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNull();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getKieSession().fireAllRules();
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull().withFailMessage("KogitoWorkItem should not be null.");
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventSignal.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testIntermediateCatchEventMessage() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventMessage.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
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
        kruntime = createKogitoProcessRuntime("BPMN2-ErrorEndEvent.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("ErrorEndEvent");
        assertProcessInstanceAborted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testServiceTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcess.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("ServiceProcess", params);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertThat(processInstance.getVariable("s")).isEqualTo("Hello john!");
    }

    @Test
    public void testSendTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SendTask.bpmn2");

        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("SendTask", params);
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testReceiveTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ReceiveTask.bpmn2");

        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(kruntime);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
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
        kruntime = createKogitoProcessRuntime("BPMN2-TimerStart.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

        });

        assertThat(list.size()).isEqualTo(0);
        countDownListener.waitTillCompleted();
        assertThat(list.size()).isEqualTo(5);
    }

    @Test
    public void testSignalStart() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SignalStart.bpmn2");
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }
        });
        kruntime.signalEvent("MySignal", "NewValue");
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testSignalEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SignalEndEvent.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        kruntime.startProcess("SignalEndEvent", params);
    }

    @Test
    public void testMessageStart() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MessageStart.bpmn2");
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }
        });
        kruntime.signalEvent("Message-HelloMessage", "NewValue");
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testMessageEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MessageEndEvent.bpmn2");
        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("MessageEndEvent", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMessageIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventMessage.bpmn2");
        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("MessageIntermediateEvent", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventSignal.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("SignalIntermediateEvent", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testNoneIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventNone.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("NoneIntermediateEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testErrorSignallingExceptionServiceTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExceptionServiceProcess-ErrorSignalling.bpmn2");

        runTestErrorSignallingExceptionServiceTask(kruntime);
    }

    public static void runTestErrorSignallingExceptionServiceTask(KogitoProcessRuntime kruntime) throws Exception {

        // Setup
        String eventType = "Error-code";
        SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
        signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
        kruntime.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);

        Object[] caughtEventObjectHolder = new Object[1];
        caughtEventObjectHolder[0] = null;
        ExceptionService.setCaughtEventObjectHolder(caughtEventObjectHolder);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        // Start process
        Map<String, Object> params = new HashMap<>();
        String input = "this is my service input";
        params.put("serviceInputItem", input);
        KogitoProcessInstance processInstance = kruntime.startProcess("ServiceProcess", params);

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        // Check that event was passed to Event SubProcess (and grabbed by WorkItemHandler);
        assertThat(caughtEventObjectHolder[0] != null && caughtEventObjectHolder[0] instanceof KogitoWorkItem).isTrue().withFailMessage("Event was not passed to Event Subprocess.");
        workItem = (KogitoWorkItem) caughtEventObjectHolder[0];
        Object throwObj = workItem.getParameter(ExceptionService.exceptionParameterName);
        assertThat(throwObj instanceof Throwable).isTrue().withFailMessage("KogitoWorkItem doesn't contain Throwable.");
        assertThat(((Throwable) throwObj).getMessage().endsWith(input)).isTrue().withFailMessage("Exception message does not match service input.");

        // Complete process
        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance == null || processInstance.getState() == KogitoProcessInstance.STATE_ABORTED).isTrue().withFailMessage("Process instance has not been aborted.");

    }

    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        // dump/reread functionality seems to work for this test 
        // .. but I'm pretty sure that's more coincidence than design (mriet, 2013-03-06)
        kruntime = createKogitoProcessRuntime("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");

        runTestSignallingExceptionServiceTask(kruntime);
    }

    @Test
    public void testXXEProcessVulnerability() throws Exception {
        Resource processResource = ResourceFactory.newClassPathResource("xxe-protection/BPMN2-XXE-Process.bpmn2");

        File dtdFile = new File("src/test/resources/xxe-protection/external.dtd");
        assertThat(dtdFile).exists();

        String dtdContent = IoUtils.readFileAsString(dtdFile);
        dtdContent = dtdContent.replaceAll("@@PATH@@", dtdFile.getParentFile().getAbsolutePath());

        IoUtils.write(dtdFile, dtdContent.getBytes("UTF-8"));

        byte[] data = IoUtils.readBytesFromInputStream(processResource.getInputStream());
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

        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task", new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Service Task", new DoNothingWorkItemHandler());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("messageContent", "some text");
        KogitoProcessInstance processInstance = kruntime.startProcess("workingMessageModel", parameters);

        assertThat(processInstance).isNotNull();
    }

    public static void runTestSignallingExceptionServiceTask(KogitoProcessRuntime kruntime) throws Exception {

        // Setup
        String eventType = "exception-signal";
        SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
        signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
        kruntime.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);

        Object[] caughtEventObjectHolder = new Object[1];
        caughtEventObjectHolder[0] = null;
        ExceptionService.setCaughtEventObjectHolder(caughtEventObjectHolder);

        // Start process
        Map<String, Object> params = new HashMap<>();
        String input = "this is my service input";
        params.put("serviceInputItem", input);
        KogitoProcessInstance processInstance = kruntime.startProcess("ServiceProcess", params);

        // Check that event was passed to Event SubProcess (and grabbed by WorkItemHandler);
        assertThat(caughtEventObjectHolder[0] != null && caughtEventObjectHolder[0] instanceof KogitoWorkItem).isTrue().withFailMessage("Event was not passed to Event Subprocess.");
        KogitoWorkItem workItem = (KogitoWorkItem) caughtEventObjectHolder[0];
        Object throwObj = workItem.getParameter(ExceptionService.exceptionParameterName);
        assertThat(throwObj instanceof Throwable).isTrue().withFailMessage("KogitoWorkItem doesn't contain Throwable.");
        assertThat(((Throwable) throwObj).getMessage().endsWith(input)).isTrue().withFailMessage("Exception message does not match service input.");

        // Complete process
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE).withFailMessage("Process instance is not active.");
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        if (processInstance != null) {
            assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED).withFailMessage("Process instance is not completed.");
        } // otherwise, persistence use => processInstance == null => process is completed
    }

}
