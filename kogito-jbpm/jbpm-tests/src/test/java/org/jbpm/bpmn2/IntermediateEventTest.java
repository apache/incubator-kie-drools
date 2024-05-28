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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbpm.bpmn2.activity.BoundarySignalEventOnTaskWithTransformationModel;
import org.jbpm.bpmn2.activity.BoundarySignalEventOnTaskWithTransformationProcess;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventSignalModel;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventSignalProcess;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.test.RequirePersistence;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventListener;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.ProcessCompletedCountDownProcessEventListener;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.NamedDataType;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jbpm.workflow.instance.node.TimerNodeInstance.TIMER_TRIGGERED_EVENT;

public class IntermediateEventTest extends JbpmBpmn2TestCase {

    private KogitoProcessEventListener LOGGING_EVENT_LISTENER = new DefaultKogitoProcessEventListener() {

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("After node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("After node triggered {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("Before node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("Before node triggered {}", event.getNodeInstance().getNodeName());
        }

    };

    /*
     * TESTS!
     */

    @Test
    public void testSignalBoundaryEvent() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-BoundarySignalEventOnTask.bpmn",
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignal.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundarySignalEventOnTask");

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("MySignal");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "signal")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));

        KogitoProcessInstance processInstance2 = kruntime.startProcess("IntermediateThrowEventSignal");
        assertProcessInstanceFinished(processInstance2, kruntime);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testSignalBoundaryNonEffectiveEvent() throws Exception {
        final String signal = "signalTest";
        final AtomicBoolean eventAfterNodeLeftTriggered = new AtomicBoolean(false);
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-BoundaryEventWithNonEffectiveSignal.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // BoundaryEventNodeInstance
                if (signal.equals(event.getNodeInstance().getNodeName())) {
                    eventAfterNodeLeftTriggered.set(true);
                }
            }
        });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundaryEventWithNonEffectiveSignal");

        // outer human work
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);

        kruntime.signalEvent(signal, signal);

        // inner human task
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertThat(eventAfterNodeLeftTriggered).isTrue();
    }

    @Test
    public void testSignalBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundarySignalEventOnTask.bpmn");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundarySignalEventOnTask");

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("MySignal", "workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions)
                .extracting("nodeId").contains("BoundaryEvent_2", "UserTask_1");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "signal")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));
        assertThat(eventDescriptions)
                .filteredOn("eventType", "signal")
                .hasSize(1)
                .extracting("nodeInstanceId").containsOnlyNulls();

        assertThat(eventDescriptions)
                .filteredOn("eventType", "workItem")
                .hasSize(1)
                .extracting("nodeInstanceId").doesNotContainNull();

        kruntime.signalEvent("MySignal", "value");
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testSignalBoundaryEventOnTaskWithSignalName() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BoundarySignalWithNameEventOnTaskbpmn2.bpmn");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundarySignalOnTask");
        kruntime.signalEvent("MySignal", "value");
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testSignalBoundaryEventOnTaskComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundarySignalEventOnTask.bpmn");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalEventOnTask");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        kruntime.signalEvent("MySignal", "value");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testSignalBoundaryEventInterrupting() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-SignalBoundaryEventInterrupting.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime
                .startProcess("SignalBoundaryEventInterrupting");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("MyMessage", "workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "signal")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));

        kruntime.signalEvent("MyMessage", null);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testSignalIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignal.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "IntermediateThrowEventSignal", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testSignalBetweenProcesses() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchSignalSingle.bpmn2",
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignal.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchSignalSingle");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);

        KogitoProcessInstance processInstance2 = kruntime.startProcess("IntermediateThrowEventSignal");
        assertProcessInstanceFinished(processInstance2, kruntime);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testEventBasedSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("EventBasedSplit");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("Yes", "No");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal");
        assertThat(eventDescriptions)
                .extracting("dataType").hasOnlyElementsOfType(NamedDataType.class).extracting("dataType").hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .extracting("nodeInstanceId").doesNotContainNull();

        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testEventBasedSplitBefore() throws Exception {
        // signaling before the split is reached should have no effect
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);

    }

    @Test
    public void testEventBasedSplitAfter() throws Exception {
        // signaling the other alternative after one has been selected should
        // have no effect
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // No
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());

    }

    @Test
    public void testEventBasedSplit2() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(2);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit2.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("EventBasedSplit2");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("Yes", TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "timer");
        assertThat(eventDescriptions).filteredOn(i -> i.getDataType() != null)
                .extracting("dataType").hasOnlyElementsOfType(NamedDataType.class).extracting("dataType").hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "timer")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Delay"));

        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

        // Timer
        processInstance = kruntime.startProcess("EventBasedSplit2");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testEventBasedSplit3() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit3.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("EventBasedSplit3");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("Yes");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "conditional");
        assertThat(eventDescriptions).filteredOn(i -> i.getDataType() != null)
                .extracting("dataType").hasOnlyElementsOfType(NamedDataType.class).extracting("dataType").hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        // Condition
        processInstance = kruntime.startProcess("EventBasedSplit3");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.getKieSession().insert(jack);

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testEventBasedSplit4() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit4.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("EventBasedSplit4");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("Message-YesMessage", "Message-NoMessage");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("message", "message");
        assertThat(eventDescriptions)
                .extracting("dataType").hasOnlyElementsOfType(NamedDataType.class).extracting("dataType").hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("Message-YesMessage", "YesValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // No
        processInstance = kruntime.startProcess("EventBasedSplit4");
        kruntime.signalEvent("Message-NoMessage", "NoValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit5.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit5");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
        assertProcessInstanceFinished(processInstance, kruntime);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit5");
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        assertProcessInstanceFinished(processInstance, kruntime);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");

    }

    @Test
    public void testEventBasedSplitWithSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-ExclusiveEventBasedGatewayInSubprocess.bpmn2");

        // Stop
        KogitoProcessInstance processInstance = kruntime.startProcess("ExclusiveEventBasedGatewayInSubprocess");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("StopSignal", "", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

        // Continue and Stop
        processInstance = kruntime.startProcess("ExclusiveEventBasedGatewayInSubprocess");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("ContinueSignal", "", processInstance.getStringId());

        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("StopSignal", "", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testEventSubprocessSignal() throws Exception {
        String[] nodes = {
                "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "sub-script", "end-sub"
        };
        runTestEventSubprocessSignal("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignal.bpmn2", "EventSubprocessSignal", nodes);
    }

    @Test
    public void testEventSubprocessSignalNested() throws Exception {
        String[] nodes = {
                "Start",
                "Sub Process",
                "Sub Start",
                "Sub Sub Process",
                "Sub Sub Start",
                "Sub Sub User Task",
                "Sub Sub Sub Process",
                "start-sub",
                "sub-script",
                "end-sub",
                "Sub Sub End",
                "Sub End",
                "End"
        };
        runTestEventSubprocessSignal("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalNested.bpmn2", "EventSubprocessSignalNested", nodes);
    }

    public void runTestEventSubprocessSignal(String processFile, String[] completedNodes) throws Exception {
        runTestEventSubprocessSignal(processFile, processFile, completedNodes);
    }

    public void runTestEventSubprocessSignal(String processFile, String processId, String[] completedNodes) throws Exception {
        kruntime = createKogitoProcessRuntime(processFile);
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("sub-script")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };

        kruntime.getProcessEventManager().addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess(processId);
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("MySignal", "workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        kruntime.signalEvent("MySignal", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), completedNodes);
        assertThat(executednodes).hasSize(4);

    }

    @Test
    public void testEventSubprocessSignalWithStateNode() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalWithStateNode.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("User Task 2")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };

        kruntime.getProcessEventManager().addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("EventSubprocessSignalWithStateNode");
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        KogitoWorkItem workItemTopProcess = workItemHandler.getWorkItem();

        kruntime.signalEvent("MySignal", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertThat(workItemTopProcess).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemTopProcess.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "User Task 2", "end-sub");
        assertThat(executednodes).hasSize(4);

    }

    @Test
    public void testEventSubprocessSignalInterrupting() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalInterrupting.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessSignalInterrupting");
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);

        kruntime.signalEvent("MySignal", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessMessage() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventSubprocessMessage.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };

        kruntime.getProcessEventManager().addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EventSubprocessMessage");
        assertProcessInstanceActive(processInstance);
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("Message-HelloMessage", "workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        kruntime.getProcessEventManager().addEventListener(listener);

        kruntime.signalEvent("Message-HelloMessage", null, processInstance.getStringId());
        kruntime.signalEvent("Message-HelloMessage", null);
        kruntime.signalEvent("Message-HelloMessage", null);
        kruntime.signalEvent("Message-HelloMessage", null);
        kruntime.getProcessInstance(processInstance.getStringId());
        kruntime.getProcessInstance(processInstance.getStringId());
        kruntime.getProcessInstance(processInstance.getStringId());
        kruntime.getProcessInstance(processInstance.getStringId());
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes).hasSize(4);

    }

    @Test
    public void testEventSubprocessTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Script Task 1", 1);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessTimer.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("workItemCompleted", TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions)
                .extracting("eventType").contains("workItem", "timer");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "timer")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Delay"));
        countDownListener.waitTillCompleted();

        eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(1)
                .extracting("event").contains("workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("workItem");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");

    }

    @Test
    @RequirePersistence
    public void testEventSubprocessTimerCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Script Task 1", 4);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessTimerCycle.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessTimerCycle");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("workItemCompleted", TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions)
                .extracting("eventType").contains("workItem", "timer");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "timer")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Period"));

        countDownListener.waitTillCompleted();

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "start-sub", "Script Task 1", "end-sub");

    }

    @Test
    public void testEventSubprocessConditional() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventSubprocessConditional.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-EventSubprocessConditional");
        assertProcessInstanceActive(processInstance);

        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessMessageWithLocalVars() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubProcessWithLocalVariables.bpmn2");
        final Set<String> variablevalues = new HashSet<String>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                @SuppressWarnings("unchecked")
                Map<String, String> variable = (Map<String, String>) event.getNodeInstance().getVariable("richiesta");
                if (variable != null) {
                    variablevalues.addAll(variable.keySet());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubProcessWithLocalVariables");
        assertProcessInstanceActive(processInstance);

        Map<String, String> data = new HashMap<>();
        kruntime.signalEvent("Message-MAIL", data, processInstance.getStringId());
        countDownListener.waitTillCompleted();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance).isNull();
        assertThat(variablevalues).hasSize(2).contains("SCRIPT1", "SCRIPT2");
    }

    @Test
    public void testMessageIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventMessage.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "IntermediateThrowEventMessage", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testMessageIntermediateThrowVerifyWorkItemData() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventMessage.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventMessage", params);
        assertProcessInstanceCompleted(processInstance);

        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull().isInstanceOf(KogitoWorkItem.class);

        String nodeInstanceId = ((InternalKogitoWorkItem) workItem).getNodeInstanceStringId();
        WorkflowElementIdentifier nodeId = ((InternalKogitoWorkItem) workItem).getNodeId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeInstanceId).isNotNull();
    }

    @Test
    public void testMessageIntermediateThrowVerifyWorkItemDataDeploymentId() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventMessage.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventMessage", params);
        assertProcessInstanceCompleted(processInstance);

        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull().isInstanceOf(KogitoWorkItem.class);

        String nodeInstanceId = ((InternalKogitoWorkItem) workItem).getNodeInstanceStringId();
        WorkflowElementIdentifier nodeId = ((InternalKogitoWorkItem) workItem).getNodeId();
        String deploymentId = ((InternalKogitoWorkItem) workItem).getDeploymentId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeInstanceId).isNotNull();
        assertThat(deploymentId).isNull();
    }

    @Test
    public void testMessageBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryMessageEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryMessageEventOnTask");
        kruntime.signalEvent("Message-HelloMessage", "message data");
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testMessageBoundaryEventOnTaskComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryMessageEventOnTask.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryMessageEventOnTask");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        kruntime.signalEvent("Message-HelloMessage", "message data");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "User Task2", "End1");

    }

    @Test
    public void testTimerBoundaryEventDuration() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventDuration.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventDuration");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("workItemCompleted", TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions)
                .extracting("eventType").contains("workItem", "timer");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "timer")
                .hasSize(1)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Period"));

        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testTimerBoundaryEventDurationISO() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventDurationISO.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventDurationISO");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testTimerBoundaryEventDateISO() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventDateISO.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        HashMap<String, Object> params = new HashMap<>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventDateISO", params);
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testTimerBoundaryEventCycle1() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventCycle1.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventCycle1");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testTimerBoundaryEventCycle2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 3);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventCycle2.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventCycle2");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());

    }

    @Test
    @RequirePersistence(false)
    public void testTimerBoundaryEventCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 2);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventCycleISO.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventCycleISO");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());
    }

    @Test
    public void testTimerBoundaryEventInterrupting() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventInterrupting.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventInterrupting");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        logger.debug("Firing timer");

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTask() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventInterruptingOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventInterruptingOnTask");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        logger.debug("Firing timer");

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTaskCancelTimer() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-TimerBoundaryEventInterruptingOnTaskCancelTimer.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEventInterruptingOnTaskCancelTimer");
        assertProcessInstanceActive(processInstance);

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoWorkItem workItem = handler.getWorkItem();
        if (workItem != null) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        }

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignal.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignal");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testIntermediateCatchEventMessage() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventMessage.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventMessage");
        assertProcessInstanceActive(processInstance); // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventMessageWithRef() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventMessageWithRef.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventMessageWithRef");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventTimerDuration() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerDuration");
        assertProcessInstanceActive(processInstance);

        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testIntermediateCatchEventTimerDateISO() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerDateISO.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        HashMap<String, Object> params = new HashMap<>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerDateISO", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventTimerDurationISO() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerDurationISO.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerDurationISO");
        assertProcessInstanceActive(processInstance);
        // now wait for 1.5 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testIntermediateCatchEventTimerCycle1() throws Exception {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycle1.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycle1");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testIntermediateCatchEventTimerCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 5);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycleISO.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycleISO");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());

    }

    @Test
    public void testIntermediateCatchEventTimerCycle2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycle2.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycle2");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());

    }

    @Test
    public void testIntermediateCatchEventCondition() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventCondition.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventCondition");
        assertProcessInstanceActive(processInstance);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        kruntime.getKieSession().insert(person);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventConditionFilterByProcessInstance()
            throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventConditionFilterByProcessInstance.bpmn2");

        Map<String, Object> params1 = new HashMap<>();
        params1.put("personId", Long.valueOf(1L));
        Person person1 = new Person();
        person1.setId(1L);
        KogitoProcessInstance pi1 = kruntime
                .createProcessInstance(
                        "IntermediateCatchEventConditionFilterByProcessInstance",
                        params1);
        String pi1id = pi1.getStringId();

        kruntime.getKieSession().insert(pi1);
        FactHandle personHandle1 = kruntime.getKieSession().insert(person1);

        kruntime.startProcessInstance(pi1.getStringId());

        Map<String, Object> params2 = new HashMap<>();
        params2.put("personId", Long.valueOf(2L));
        Person person2 = new Person();
        person2.setId(2L);

        KogitoProcessInstance pi2 = kruntime
                .createProcessInstance(
                        "IntermediateCatchEventConditionFilterByProcessInstance",
                        params2);
        String pi2id = pi2.getStringId();

        kruntime.getKieSession().insert(pi2);
        FactHandle personHandle2 = kruntime.getKieSession().insert(person2);

        kruntime.startProcessInstance(pi2.getStringId());

        person1.setName("John");
        kruntime.getKieSession().update(personHandle1, person1);

        // First process should be completed
        assertThat(kruntime.getProcessInstance(pi1id)).isNull();
        // Second process should NOT be completed
        assertThat(kruntime.getProcessInstance(pi2id)).isNotNull();

    }

    @Test
    @RequirePersistence(false)
    public void testIntermediateCatchEventTimerCycleWithError()
            throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        Map<String, Object> params = new HashMap<>();
        params.put("x", 0);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycleWithError", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        Integer xValue = (Integer) ((KogitoWorkflowProcessInstance) processInstance).getVariable("x");
        assertThat(xValue).isEqualTo(3);

        kruntime.abortProcessInstance(processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    @RequirePersistence
    public void testIntermediateCatchEventTimerCycleWithErrorWithPersistence() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycleWithError");
        assertProcessInstanceActive(processInstance);

        final String piId = processInstance.getStringId();
        kruntime.getKieSession().execute((ExecutableCommand<Void>) context -> {
            KogitoWorkflowProcessInstance processInstance1 = (KogitoWorkflowProcessInstance) kruntime.getProcessInstance(piId);
            processInstance1.setVariable("x", 0);
            return null;
        });

        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        Integer xValue = kruntime.getKieSession().execute((ExecutableCommand<Integer>) context -> {
            KogitoWorkflowProcessInstance processInstance2 = (KogitoWorkflowProcessInstance) kruntime.getProcessInstance(piId);
            return (Integer) processInstance2.getVariable("x");

        });
        assertThat(xValue).isEqualTo(2);
        kruntime.abortProcessInstance(processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testNoneIntermediateThrow() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventNone.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "IntermediateThrowEventNone");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testLinkIntermediateEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateLinkEvent.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateLinkEvent");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testLinkEventCompositeProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-LinkEventCompositeProcess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("LinkEventCompositeProcess");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testConditionalBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryConditionalEventOnTask");

        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryConditionalEventOnTask");

        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        Person person = new Person();
        person.setName("john");
        // as the node that boundary event is attached to has been completed insert will not have any effect
        kruntime.getKieSession().insert(person);
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "User Task2", "End1");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskActiveOnStartup()
            throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryConditionalEventOnTask");
        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceCompleted(processInstance);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventInterrupting() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-ConditionalBoundaryEventInterrupting.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("ConditionalBoundaryEventInterrupting");
        assertProcessInstanceActive(processInstance);

        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "Hello",
                "StartSubProcess", "Task", "BoundaryEvent", "Goodbye",
                "EndProcess");

    }

    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");
        StandaloneBPMNProcessTest.runTestSignallingExceptionServiceTask(kruntime);
    }

    @Test
    public void testSignalBoundaryEventOnSubprocessTakingDifferentPaths() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-SignalBoundaryOnSubProcess.bpmn");

        KogitoProcessInstance processInstance = kruntime.startProcess("SignalBoundaryOnSubProcess");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("continue", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

        processInstance = kruntime.startProcess("SignalBoundaryOnSubProcess");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("forward", null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testIntermediateCatchEventSameSignalOnTwokruntimes() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignal.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignal");

        KogitoProcessRuntime kruntime2 = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventSignal2.bpmn2");
        kruntime2.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance2 = kruntime2.startProcess("IntermediateCatchEventSignal2");

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue");
        assertProcessInstanceFinished(processInstance, kruntime);
        assertProcessInstanceActive(processInstance2);

        // now signal the other one
        kruntime2.signalEvent("MyMessage", "SomeValue");
        assertProcessInstanceFinished(processInstance2, kruntime2);

        kruntime2.getKieSession().dispose(); // kruntime's session is disposed in the @AfterEach method
    }

    @Test
    public void testIntermediateCatchEventNoIncommingConnection() throws Exception {
        try {
            kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventNoIncommingConnection.bpmn2");

        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotNull();
            assertThat(e.getMessage()).contains("has no incoming connection");
        }

    }

    @Test
    public void testSignalBoundaryEventOnMultiInstanceSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-MultiInstanceSubprocessWithBoundarySignal.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<>();
        List<String> approvers = new ArrayList<>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceSubprocessWithBoundarySignal", params);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(2);

        kruntime.signalEvent("Outside", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testSignalBoundaryEventNoInteruptOnMultiInstanceSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-MultiInstanceSubprocessWithBoundarySignalNoInterupting.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<>();
        List<String> approvers = new ArrayList<>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceSubprocessWithBoundarySignalNoInterupting", params);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(2);

        kruntime.signalEvent("Outside", null, processInstance.getStringId());

        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        for (KogitoWorkItem wi : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testErrorBoundaryEventOnMultiInstanceSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-MultiInstanceSubprocessWithBoundaryError.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<>();
        List<String> approvers = new ArrayList<>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceSubprocessWithBoundaryError", params);
        assertProcessInstanceActive(processInstance);
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
                .hasSize(3)
                .extracting("event").contains("workItemCompleted", "Inside", "Error-_D83CFC28-3322-4ABC-A12D-83476B08C7E8-MyError");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("workItem", "signal");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions)
                .filteredOn("eventType", "signal")
                .hasSize(2)
                .extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(2);

        kruntime.signalEvent("Inside", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventSignalAndBoundarySignalEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryEventWithSignals.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryEventWithSignals");
        assertProcessInstanceActive(processInstance);

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        // now signal process instance
        kruntime.signalEvent("moveon", "", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);

        KogitoWorkItem wi = handler.getWorkItem();
        assertThat(wi).isNotNull();

        // signal boundary event on user task
        kruntime.signalEvent("moveon", "", processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testSignalIntermediateThrowEventWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn",
                "BPMN2-IntermediateThrowEventSignalWithTransformation.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", "john");
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");

        KogitoProcessInstance processInstance2 = kruntime.startProcess("SignalIntermediateEvent", params);
        assertProcessInstanceFinished(processInstance2, kruntime);

        assertProcessInstanceFinished(processInstance, kruntime);

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isEqualTo("JOHN");
    }

    @Test
    public void testSignalBoundaryEventWithTransformation() throws Exception {
        Application application = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<BoundarySignalEventOnTaskWithTransformationModel> processBoundary = BoundarySignalEventOnTaskWithTransformationProcess.newProcess(application);
        org.kie.kogito.process.Process<IntermediateThrowEventSignalModel> processIntermediate = IntermediateThrowEventSignalProcess.newProcess(application);

        ProcessInstance<BoundarySignalEventOnTaskWithTransformationModel> instanceBoundary = processBoundary.createInstance(processBoundary.createModel());
        instanceBoundary.start();
        IntermediateThrowEventSignalModel modelIntermediate = processIntermediate.createModel();
        modelIntermediate.setX("john");
        ProcessInstance<IntermediateThrowEventSignalModel> instanceIntermediate = processIntermediate.createInstance(modelIntermediate);
        instanceIntermediate.start();
        assertThat(instanceIntermediate).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(instanceBoundary).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(instanceBoundary.variables().getX()).isEqualTo("JOHN");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testMessageIntermediateThrowWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateThrowEventMessageWithTransformation.bpmn2");
        final StringBuffer messageContent = new StringBuffer();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler() {

                    @Override
                    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                        // collect message content for verification
                        messageContent.append(workItem.getParameter("Message"));
                        super.executeWorkItem(workItem, manager);
                    }

                });
        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        assertThat(messageContent).hasToString("MYVALUE");

    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testIntermediateCatchEventSignalWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventSignalWithTransformation.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");
        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull().isEqualTo("SOMEVALUE");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testIntermediateCatchEventMessageWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventMessageWithTransformation.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull().isEqualTo("SOMEVALUE");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testEventSubprocessSignalWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EventSubprocessSignalWithTransformation.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("MySignal", "john", processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "end-sub");

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull().isEqualTo("JOHN");
    }

    @Test
    public void testMultipleMessageSignalSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-MultipleMessageSignalSubprocess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("MultipleMessageSignalSubprocess");
        logger.debug("Parent Process ID: " + processInstance.getStringId());

        kruntime.signalEvent("Message-Message 1", "Test", processInstance.getStringId());
        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        kruntime.signalEvent("Message-Message 1", "Test", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testIntermediateCatchEventSignalWithRef() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignalWithRef.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignalWithRef");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("Signal1", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testTimerMultipleInstances() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopBoundaryTimer.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceLoopBoundaryTimer");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(3);

        for (KogitoWorkItem wi : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testIntermediateTimerParallelGateway() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener1 = new NodeLeftCountDownProcessEventListener("Timer1", 1);
        NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Timer2", 1);
        NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Timer3", 1);
        ProcessCompletedCountDownProcessEventListener countDownProcessEventListener = new ProcessCompletedCountDownProcessEventListener();
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-IntermediateTimerParallelGateway.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener1);
        kruntime.getProcessEventManager().addEventListener(countDownListener2);
        kruntime.getProcessEventManager().addEventListener(countDownListener3);
        kruntime.getProcessEventManager().addEventListener(countDownProcessEventListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateTimerParallelGateway");
        assertProcessInstanceActive(processInstance);

        countDownListener1.waitTillCompleted();
        countDownListener2.waitTillCompleted();
        countDownListener3.waitTillCompleted();
        countDownProcessEventListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);

    }

    @Test
    public void testIntermediateTimerEventMI() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("After timer", 3);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/timer/BPMN2-IntermediateTimerEventMI.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateTimerEventMI");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        kruntime.abortProcessInstance(processInstance.getStringId());

        assertProcessInstanceAborted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testThrowIntermediateSignalWithScope() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventScope.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<>();

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventScope", params);
        KogitoProcessInstance processInstance2 = kruntime.startProcess("IntermediateThrowEventScope", params);

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        assertNodeActive(processInstance.getStringId(), kruntime, "Complete work", "Wait");
        assertNodeActive(processInstance2.getStringId(), kruntime, "Complete work", "Wait");

        List<KogitoWorkItem> items = handler.getWorkItems();

        KogitoWorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<>();
        result.put("_output", "sending event");

        kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), result);

        assertProcessInstanceCompleted(processInstance);
        assertProcessInstanceActive(processInstance2);
        assertNodeActive(processInstance2.getStringId(), kruntime, "Complete work", "Wait");

        wi = items.get(1);
        kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), result);
        assertProcessInstanceCompleted(processInstance2);

    }

    @Test
    public void testThrowEndSignalWithScope() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EndThrowEventScope.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<>();

        KogitoProcessInstance processInstance = kruntime.startProcess("EndThrowEventScope", params);
        KogitoProcessInstance processInstance2 = kruntime.startProcess("EndThrowEventScope", params);

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        assertNodeActive(processInstance.getStringId(), kruntime, "Complete work", "Wait");
        assertNodeActive(processInstance2.getStringId(), kruntime, "Complete work", "Wait");

        List<KogitoWorkItem> items = handler.getWorkItems();

        KogitoWorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<>();
        result.put("_output", "sending event");

        kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), result);

        assertProcessInstanceCompleted(processInstance);
        assertProcessInstanceActive(processInstance2);
        assertNodeActive(processInstance2.getStringId(), kruntime, "Complete work", "Wait");

        wi = items.get(1);
        kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), result);
        assertProcessInstanceCompleted(processInstance2);

    }

    @Test
    public void testThrowIntermediateSignalWithExternalScope() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventExternalScope.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        KogitoWorkItemHandler externalHandler = new KogitoWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                String signal = (String) workItem.getParameter("Signal");
                kruntime.signalEvent(signal, null);

                manager.completeWorkItem(workItem.getStringId(), null);

            }

            @Override
            public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
            }
        };

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("External Send Task", externalHandler);
        Map<String, Object> params = new HashMap<>();

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventExternalScope", params);

        assertProcessInstanceActive(processInstance);

        assertNodeActive(processInstance.getStringId(), kruntime, "Complete work", "Wait");

        List<KogitoWorkItem> items = handler.getWorkItems();
        assertThat(items).hasSize(1);
        KogitoWorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<>();
        result.put("_output", "sending event");

        kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), result);

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testIntermediateCatchEventSignalWithVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("signalName", signalVar);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignalWithVariable", parameters);
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent(signalVar, "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testSignalIntermediateThrowWithVariable() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignalWithVariable.bpmn2",
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        // create catch process instance
        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("signalName", signalVar);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignalWithVariable", parameters);
        assertProcessInstanceActive(processInstance);

        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        params.put("signalName", signalVar);
        KogitoProcessInstance processInstanceThrow = kruntime.startProcess("IntermediateThrowEventSignalWithVariable", params);
        assertThat(processInstanceThrow.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);

        // catch process instance should now be completed
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInvalidDateTimerBoundary() throws Exception {
        try {
            createKogitoProcessRuntime("timer/BPMN2-TimerBoundaryEventDateInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Could not parse date 'abcdef'");
        }
    }

    @Test
    public void testInvalidDurationTimerBoundary() throws Exception {
        try {
            createKogitoProcessRuntime("timer/BPMN2-TimerBoundaryEventDurationInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Could not parse delay 'abcdef'");
        }
    }

    @Test
    public void testInvalidCycleTimerBoundary() throws Exception {
        try {
            createKogitoProcessRuntime("timer/BPMN2-TimerBoundaryEventCycleInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Could not parse delay 'abcdef'");
        }
    }

    @Test
    public void testIntermediateCatchEventConditionSetVariableAfter() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventConditionSetVariableAfter.bpmn2");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventConditionSetVariableAfter");
        assertProcessInstanceActive(processInstance);

        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        Collection<? extends Object> processInstances = kruntime.getKieSession().getObjects(object -> {
            if (object instanceof KogitoProcessInstance) {
                return true;
            }
            return false;
        });
        assertThat(processInstances).isNotNull().hasSize(1);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        kruntime.getKieSession().insert(person);
        assertProcessInstanceFinished(processInstance, kruntime);

        processInstances = kruntime.getKieSession().getObjects(object -> {
            if (object instanceof KogitoProcessInstance) {
                return true;
            }
            return false;
        });
        assertThat(processInstances).isNotNull().isEmpty();
    }

    @Test
    public void testIntermediateCatchEventConditionRemovePIAfter() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventCondition.bpmn2");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventCondition");
        assertProcessInstanceActive(processInstance);

        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        Collection<? extends Object> processInstances = kruntime.getKieSession().getObjects(object -> {
            if (object instanceof KogitoProcessInstance) {
                return true;
            }
            return false;
        });
        assertThat(processInstances).isNotNull().hasSize(1);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        kruntime.getKieSession().insert(person);
        assertProcessInstanceFinished(processInstance, kruntime);

        processInstances = kruntime.getKieSession().getObjects(object -> {
            if (object instanceof KogitoProcessInstance) {
                return true;
            }
            return false;
        });
        assertThat(processInstances).isNotNull().isEmpty();
    }

    @Test
    public void testEventBasedSplitWithCronTimerAndSignal() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");
        try {
            NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Request photos of order in use", 1);
            NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Request an online review", 1);
            NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Send a thank you card", 1);
            NodeLeftCountDownProcessEventListener countDownListener4 = new NodeLeftCountDownProcessEventListener("Request an online review", 1);
            kruntime = createKogitoProcessRuntime("timer/BPMN2-CronTimerWithEventBasedGateway.bpmn2");

            TestWorkItemHandler handler = new TestWorkItemHandler();
            kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
            kruntime.getProcessEventManager().addEventListener(countDownListener);
            kruntime.getProcessEventManager().addEventListener(countDownListener2);
            kruntime.getProcessEventManager().addEventListener(countDownListener3);
            kruntime.getProcessEventManager().addEventListener(countDownListener4);

            KogitoProcessInstance processInstance = kruntime.startProcess("timerWithEventBasedGateway");
            assertProcessInstanceActive(processInstance.getStringId(), kruntime);

            countDownListener.waitTillCompleted();
            logger.debug("First timer triggered");
            countDownListener2.waitTillCompleted();
            logger.debug("Second timer triggered");
            countDownListener3.waitTillCompleted();
            logger.debug("Third timer triggered");
            countDownListener4.waitTillCompleted();
            logger.debug("Fourth timer triggered");

            List<KogitoWorkItem> wi = handler.getWorkItems();
            assertThat(wi).isNotNull();
            assertThat(wi).hasSize(3);

            kruntime.abortProcessInstance(processInstance.getStringId());
        } finally {
            // clear property only as the only relevant value is when it's set to true
            System.clearProperty("jbpm.enable.multi.con");
        }
    }

    @Test
    public void testEventSubprocessWithEmbeddedSignals() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessErrorSignalEmbedded.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessErrorSignalEmbedded");

        assertProcessInstanceActive(processInstance.getStringId(), kruntime);
        assertProcessInstanceActive(processInstance);
        kruntime.signalEvent("signal1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        kruntime.signalEvent("signal2", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        kruntime.signalEvent("signal3", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testEventSubprocessWithExpression() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalExpression.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "signalling");
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessSignalExpression", params);

        assertProcessInstanceActive(processInstance.getStringId(), kruntime);
        assertProcessInstanceActive(processInstance);
        kruntime.signalEvent("signalling", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testConditionalProcessFactInsertedBefore() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventConditionPI.bpmn2", "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignal.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Person person0 = new Person("john");
        kruntime.getKieSession().insert(person0);

        Map<String, Object> params0 = new HashMap<>();
        params0.put("name", "john");
        KogitoProcessInstance pi0 = kruntime.startProcess("IntermediateCatchEventSignal", params0);
        kruntime.getKieSession().insert(pi0);

        Person person = new Person("Jack");
        kruntime.getKieSession().insert(person);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "Poul");
        KogitoProcessInstance pi = kruntime.startProcess("IntermediateCatchEventConditionPI", params);
        kruntime.getKieSession().insert(pi);
        pi = kruntime.getProcessInstance(pi.getStringId());
        assertThat(pi).isNotNull();

        Person person2 = new Person("Poul");
        kruntime.getKieSession().insert(person2);

        pi = kruntime.getProcessInstance(pi.getStringId());
        assertThat(pi).isNull();

    }

    @Test
    public void testBoundarySignalEventOnSubprocessWithVariableResolution() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-SubprocessWithSignalEndEventAndSignalBoundaryEvent.bpmn2");
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);

        Map<String, Object> params = new HashMap<>();
        params.put("document-ref", "signalling");
        params.put("message", "hello");
        KogitoProcessInstance processInstance = kruntime.startProcess("SubprocessWithSignalEndEventAndSignalBoundaryEvent", params);

        assertNodeTriggered(processInstance.getStringId(), "sysout from boundary", "end2");
        assertNotNodeTriggered(processInstance.getStringId(), "end1");

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testSignalEndWithData() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignalWithData.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventSignalWithData", params);

        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("mysignal", null, processInstance.getStringId());

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testDynamicCatchEventSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        final List<String> instances = new ArrayList<>();

        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

        });

        KogitoProcessInstance processInstance = kruntime.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        assertThat(instances).hasSize(4);

        // remove the parent process instance
        instances.remove(processInstance.getStringId());

        for (String id : instances) {
            KogitoProcessInstance child = kruntime.getProcessInstance(id);
            assertProcessInstanceActive(child);
        }

        // now complete user task to signal all child instances to stop
        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        for (String id : instances) {
            assertThat(kruntime.getProcessInstance(id)).as("Child process instance has not been finished.").isNull();
        }
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testDynamicCatchEventSignalWithVariableUpdated() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        final List<String> instances = new ArrayList<>();

        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

        });

        KogitoProcessInstance processInstance = kruntime.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        assertThat(instances).hasSize(4);

        // remove the parent process instance
        instances.remove(processInstance.getStringId());

        for (String id : instances) {
            KogitoProcessInstance child = kruntime.getProcessInstance(id);
            assertProcessInstanceActive(child);
        }

        // change one child process instance variable (fatherId) to something else then original fatherId
        String changeProcessInstanceId = instances.remove(0);
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("fatherId", "999");
        kruntime.getKieSession().execute(new KogitoSetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));

        // now complete user task to signal all child instances to stop
        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        for (String id : instances) {
            assertThat(kruntime.getProcessInstance(id)).as("Child process instance has not been finished.").isNull();
        }

        KogitoProcessInstance updatedChild = kruntime.getProcessInstance(changeProcessInstanceId);
        assertProcessInstanceActive(updatedChild);

        kruntime.signalEvent("stopChild:999", null, changeProcessInstanceId);
        assertProcessInstanceFinished(updatedChild, kruntime);
    }

    @RequirePersistence
    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testDynamicCatchEventSignalWithVariableUpdatedBroadcastSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        final List<String> instances = new ArrayList<>();

        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

        });

        KogitoProcessInstance processInstance = kruntime.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        assertThat(instances).hasSize(4);

        // remove the parent process instance
        instances.remove(processInstance.getStringId());

        for (String id : instances) {
            KogitoProcessInstance child = kruntime.getProcessInstance(id);
            assertProcessInstanceActive(child);
        }

        // change one child process instance variable (fatherId) to something else then original fatherId
        String changeProcessInstanceId = instances.remove(0);
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("fatherId", "999");
        kruntime.getKieSession().execute(new KogitoSetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));

        // now complete user task to signal all child instances to stop
        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        for (String id : instances) {
            assertThat(kruntime.getProcessInstance(id)).as("Child process instance has not been finished.").isNull();
        }

        KogitoProcessInstance updatedChild = kruntime.getProcessInstance(changeProcessInstanceId);
        assertProcessInstanceActive(updatedChild);

        kruntime.signalEvent("stopChild:999", null, updatedChild.getStringId());
        assertProcessInstanceFinished(updatedChild, kruntime);
    }
}
