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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.command.runtime.process.KogitoSetProcessInstanceVariablesCommand;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.test.RequirePersistence;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventListener;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.KieBase;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.NamedDataType;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IntermediateEventTest extends JbpmBpmn2TestCase {

    private ProcessEventListener LOGGING_EVENT_LISTENER = new DefaultProcessEventListener() {

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
        KieBase kbase = createKnowledgeBase(
                "BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundarySignalOnTask");
        
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
               
        KogitoProcessInstance processInstance2 = kruntime.startProcess("SignalIntermediateEvent");
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testSignalBoundaryNonEffectiveEvent() throws Exception {
        final String signal = "signalTest";
        final AtomicBoolean eventAfterNodeLeftTriggered = new AtomicBoolean(false);
        KieBase kbase = createKnowledgeBase(
                "BPMN2-BoundaryEventWithNonEffectiveSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        ksession.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // BoundaryEventNodeInstance
                if(signal.equals(event.getNodeInstance().getNodeName())) {
                	eventAfterNodeLeftTriggered.set(true);
                }
            }
        });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundaryEventWithNonEffectiveSignal");

        // outer human work
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        
        kruntime.signalEvent(signal, signal);

        // inner human task
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);

        assertProcessInstanceFinished(processInstance, ksession);
        assertThat(eventAfterNodeLeftTriggered.get()).isTrue();
    }

    @Test
    public void testSignalBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundarySignalOnTask");
        
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
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalBoundaryEventOnTaskWithSignalName() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalWithNameEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BoundarySignalOnTask");
        kruntime.signalEvent("MySignal", "value");
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalBoundaryEventOnTaskComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        kruntime.signalEvent("MySignal", "value");
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime
                .startProcess("SignalBoundaryEvent");
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

        ksession = restoreSession(ksession, true);
        kruntime.signalEvent("MyMessage", null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "SignalIntermediateEvent", params);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testSignalBetweenProcesses() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "BPMN2-IntermediateCatchSignalSingle.bpmn2",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-IntermediateCatchSignalSingle");
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);

        KogitoProcessInstance processInstance2 = kruntime.startProcess("SignalIntermediateEvent");
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testEventBasedSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
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
        assertProcessInstanceFinished(processInstance, ksession);
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplitBefore() throws Exception {
        // signaling before the split is reached should have no effect
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);

    }

    @Test
    public void testEventBasedSplitAfter() throws Exception {
        // signaling the other alternative after one has been selected should
        // have no effect
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // No
        kruntime.signalEvent("No", "NoValue", processInstance.getStringId());

    }

    @Test
    @Timeout(10)
    public void testEventBasedSplit2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit2.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.addEventListener(countDownListener);
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
            .hasSize(2)
            .extracting("event").contains("Yes", "timerTriggered");
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
        
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Timer
        processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplit3() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit3.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("com.sample.test");
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
        
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        // Condition
        processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.insert(jack);

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplit4() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit4.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        KogitoProcessInstance processInstance = kruntime
                .startProcess("com.sample.test");
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
        
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        kruntime.signalEvent("Message-YesMessage", "YesValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        kruntime.signalEvent("Message-NoMessage", "NoValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit5.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(ksession);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
        assertProcessInstanceFinished(processInstance, ksession);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        kruntime.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(ksession);
        kruntime.getWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        // No
        processInstance = kruntime.startProcess("com.sample.test");
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        assertProcessInstanceFinished(processInstance, ksession);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");

    }

    @Test
    public void testEventBasedSplitWithSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExclusiveEventBasedGatewayInSubprocess.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        
        // Stop
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.bpmn.testEBGInSubprocess");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        kruntime.signalEvent("StopSignal", "", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);


        // Continue and Stop
        processInstance = kruntime.startProcess("com.sample.bpmn.testEBGInSubprocess");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        kruntime.signalEvent("ContinueSignal", "", processInstance.getStringId());

        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        kruntime.signalEvent("StopSignal", "", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testEventSubprocessSignal() throws Exception {
        String [] nodes = {
                "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "sub-script", "end-sub"
        };
        runTestEventSubprocessSignal("BPMN2-EventSubprocessSignal.bpmn2", nodes);
    }

    @Test
    public void testEventSubprocessSignalNested() throws Exception {
        String [] nodes = {
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
        runTestEventSubprocessSignal("BPMN2-EventSubprocessSignal-Nested.bpmn2", nodes);
    }

    public void runTestEventSubprocessSignal(String processFile, String [] completedNodes) throws Exception {
        KieBase kbase = createKnowledgeBase(processFile);
        final List<String> executednodes = new ArrayList<>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("sub-script")) {
                    executednodes.add( (( KogitoNodeInstance ) event.getNodeInstance()).getStringId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

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

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), completedNodes );
        assertThat(executednodes.size()).isEqualTo(4);

    }

    @Test
    public void testEventSubprocessSignalWithStateNode() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessSignalWithStateNode.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("User Task 2")) {
                    executednodes.add( (( KogitoNodeInstance ) event.getNodeInstance()).getStringId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        KogitoWorkItem workItemTopProcess = workItemHandler.getWorkItem();

        kruntime.signalEvent("MySignal", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        kruntime.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertThat(workItemTopProcess).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(
                workItemTopProcess.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "User Task 2", "end-sub");
        assertThat(executednodes.size()).isEqualTo(4);

    }

    @Test
    public void testEventSubprocessSignalInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessSignalInterrupting.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add( (( KogitoNodeInstance ) event.getNodeInstance()).getStringId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        kruntime.signalEvent("MySignal", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes.size()).isEqualTo(1);

    }

    @Test
    public void testEventSubprocessMessage() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessMessage.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add( (( KogitoNodeInstance ) event.getNodeInstance()).getStringId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
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
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

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
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes.size()).isEqualTo(4);

    }

    @Test
    @Timeout(10)
    public void testEventSubprocessTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Script Task 1", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessTimer.bpmn2");

        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);
        
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
            .hasSize(2)
            .extracting("event").contains("workItemCompleted", "timerTriggered");
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
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");

    }

    @Test
    @Timeout(10)
    @RequirePersistence
    public void testEventSubprocessTimerCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Script Task 1", 4);

        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessTimerCycle.bpmn2");

        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);
        
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
            .hasSize(2)
            .extracting("event").contains("workItemCompleted", "timerTriggered");
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
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "start-sub", "Script Task 1", "end-sub");

    }

    @Test
    public void testEventSubprocessConditional() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessConditional.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add( (( KogitoNodeInstance ) event.getNodeInstance()).getStringId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-EventSubprocessConditional");
        assertProcessInstanceActive(processInstance);

        Person person = new Person();
        person.setName("john");
        ksession.insert(person);


        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes.size()).isEqualTo(1);

    }

    @Test
    @Timeout(10)
    public void testEventSubprocessMessageWithLocalVars() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("subprocess/BPMN2-EventSubProcessWithLocalVariables.bpmn2");
        final Set<String> variablevalues = new HashSet<String>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
            	@SuppressWarnings("unchecked")
				Map<String, String> variable = (Map<String, String>)event.getNodeInstance().getVariable("richiesta");
            	if (variable != null) {
            		variablevalues.addAll(variable.keySet());
            	}
            }

        };
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(listener);
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSPWithVars");
        assertProcessInstanceActive(processInstance);

        Map<String, String> data = new HashMap<String, String>();
        kruntime.signalEvent("Message-MAIL", data, processInstance.getStringId());
        countDownListener.waitTillCompleted();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance).isNull();
        assertThat(variablevalues.size()).isEqualTo(2);
        assertThat(variablevalues.contains("SCRIPT1")).isTrue();
        assertThat(variablevalues.contains("SCRIPT2")).isTrue();
    }

    @Test
    public void testMessageIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testMessageIntermediateThrowVerifyWorkItemData() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem instanceof KogitoWorkItem ).isTrue();

        String nodeInstanceId = (( org.kie.kogito.process.workitems.KogitoWorkItem ) workItem).getNodeInstanceStringId();
        long nodeId = (( org.kie.kogito.process.workitems.KogitoWorkItem ) workItem).getNodeId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeId > 0).isTrue();
        assertThat(nodeInstanceId).isNotNull();
    }

    @Test
    public void testMessageIntermediateThrowVerifyWorkItemDataDeploymentId() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem instanceof KogitoWorkItem ).isTrue();

        String nodeInstanceId = (( org.kie.kogito.process.workitems.KogitoWorkItem ) workItem).getNodeInstanceStringId();
        long nodeId = (( org.kie.kogito.process.workitems.KogitoWorkItem ) workItem).getNodeId();
        String deploymentId = (( org.kie.kogito.process.workitems.KogitoWorkItem ) workItem).getDeploymentId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeId > 0).isTrue();
        assertThat(nodeInstanceId).isNotNull();
        assertThat(deploymentId).isNull();
    }

    @Test
    public void testMessageBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryMessageEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryMessageOnTask");
        kruntime.signalEvent("Message-HelloMessage", "message data");
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testMessageBoundaryEventOnTaskComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryMessageEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryMessageOnTask");
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        kruntime.signalEvent("Message-HelloMessage", "message data");
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "User Task2", "End1");

    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventDuration() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions)
            .hasSize(2)
            .extracting("event").contains("workItemCompleted", "timerTriggered");
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
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventDurationISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDurationISO.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventDateISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-TimerBoundaryEventDateISO.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        HashMap<String, Object> params = new HashMap<String, Object>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent", params);
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventCycle1() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle1.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventCycle2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 3);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle2.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());

    }

    @Test
    @Timeout(10)
    @RequirePersistence(false)
    public void testTimerBoundaryEventCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());
    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventInterrupting() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testTimerBoundaryEventInterruptingOnTask() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterruptingOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",  new TestWorkItemHandler());
        ksession.addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTaskCancelTimer() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterruptingOnTaskCancelTimer.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);        
        kruntime.getWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);

        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);        
        KogitoWorkItem workItem = handler.getWorkItem();
        if (workItem != null) {
            kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        }

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
   
        ksession = restoreSession(ksession, true);
        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testIntermediateCatchEventMessage() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
                
        ksession = restoreSession(ksession, true);
        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

    }
    
    @Test
    public void testIntermediateCatchEventMessageWithRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventMessageWithRef.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue",
                processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimerDuration() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
  
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimerDateISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventTimerDateISO.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        HashMap<String, Object> params = new HashMap<String, Object>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimerDurationISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDurationISO.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1.5 second for timer to trigger
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimerCycle1() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle1.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimerCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 5);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleISO.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());

    }

    @Test
    @Timeout(10)
    public void testIntermediateCatchEventTimerCycle2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        kruntime.abortProcessInstance(processInstance.getStringId());

    }

    @Test
    public void testIntermediateCatchEventCondition() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventConditionFilterByProcessInstance()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionFilterByProcessInstance.bpmn2");
        ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("personId", Long.valueOf(1L));
        Person person1 = new Person();
        person1.setId(1L);
        KogitoProcessInstance pi1 = kruntime
                .createProcessInstance(
                        "IntermediateCatchEventConditionFilterByProcessInstance",
                        params1);
        String pi1id = pi1.getStringId();

        ksession.insert(pi1);
        FactHandle personHandle1 = ksession.insert(person1);

        kruntime.startProcessInstance(pi1.getStringId());

        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("personId", Long.valueOf(2L));
        Person person2 = new Person();
        person2.setId(2L);

        KogitoProcessInstance pi2 = kruntime
                .createProcessInstance(
                        "IntermediateCatchEventConditionFilterByProcessInstance",
                        params2);
        String pi2id = pi2.getStringId();

        ksession.insert(pi2);
        FactHandle personHandle2 = ksession.insert(person2);

        kruntime.startProcessInstance(pi2.getStringId());

        person1.setName("John");
        ksession.update(personHandle1, person1);

        // First process should be completed
        assertThat(kruntime.getProcessInstance(pi1id)).isNull();
        // Second process should NOT be completed
        assertThat(kruntime.getProcessInstance(pi2id)).isNotNull();

    }

    @Test
    @Timeout(10)
    @RequirePersistence(false)
    public void testIntermediateCatchEventTimerCycleWithError()
            throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 0);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        Integer xValue = (Integer) ((WorkflowProcessInstance) processInstance).getVariable("x");
        assertThat(xValue).isEqualTo(3);

        kruntime.abortProcessInstance(processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    @Timeout(10)
    @RequirePersistence
    public void testIntermediateCatchEventTimerCycleWithErrorWithPersistence() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);


        final String piId = processInstance.getStringId();
        ksession.execute(new ExecutableCommand<Void>() {

            public Void execute(Context context) {
                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((RegistryContext) context).lookup( KieSession.class );
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kruntime.getProcessInstance(piId);
                processInstance.setVariable("x", 0);
                return null;
            }
        });

        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        Integer xValue = ksession.execute(new ExecutableCommand<Integer>() {

            public Integer execute(Context context) {
                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((RegistryContext) context).lookup( KieSession.class );
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kruntime.getProcessInstance(piId);
                return (Integer) processInstance.getVariable("x");

            }
        });
        assertThat(xValue).isEqualTo(2);
        kruntime.abortProcessInstance(processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testNoneIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventNone.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "NoneIntermediateEvent");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testLinkIntermediateEvent() throws Exception {

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateLinkEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        KogitoProcessInstance processInstance = kruntime.startProcess("linkEventProcessExample");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testLinkEventCompositeProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-LinkEventCompositeProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        KogitoProcessInstance processInstance = kruntime.startProcess("Composite");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testConditionalBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");

        Person person = new Person();
        person.setName("john");
        ksession.insert(person);

        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");

        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        Person person = new Person();
        person.setName("john");
        // as the node that boundary event is attached to has been completed insert will not have any effect
        ksession.insert(person);
        kruntime.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "User Task2", "End1");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskActiveOnStartup()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);


        assertProcessInstanceCompleted(processInstance);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ConditionalBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("ConditionalBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);


        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "Hello",
                "StartSubProcess", "Task", "BoundaryEvent", "Goodbye",
                "EndProcess");

    }

    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        StandaloneBPMNProcessTest.runTestSignallingExceptionServiceTask(ksession);
    }

    @Test
    public void testSignalBoundaryEventOnSubprocessTakingDifferentPaths() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "BPMN2-SignalBoundaryOnSubProcess.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        KogitoProcessInstance processInstance = kruntime.startProcess("jbpm.testing.signal");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("continue", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();

        ksession = createKnowledgeSession(kbase);
        kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        processInstance = kruntime.startProcess("jbpm.testing.signal");
        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("forward", null);
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testIntermediateCatchEventSameSignalOnTwoKsessions() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");

        KieBase kbase2 = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal2.bpmn2");
        KieSession ksession2 = createKnowledgeSession(kbase2);
        KogitoProcessRuntime kruntime2 = KogitoProcessRuntime.asKogitoProcessRuntime( ksession2 );
        kruntime2.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance2 = kruntime2.startProcess("IntermediateCatchEvent2");

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);
        ksession = restoreSession(ksession, true);
        ksession2 = restoreSession(ksession2, true);
        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue");
        assertProcessInstanceFinished(processInstance, ksession);
        assertProcessInstanceActive(processInstance2);

        // now signal the other one
        kruntime2.signalEvent("MyMessage", "SomeValue");
        assertProcessInstanceFinished(processInstance2, ksession2);
        ksession2.dispose();
    }
    
    @Test
    public void testIntermediateCatchEventNoIncommingConnection() throws Exception {
        try {
	    	KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventNoIncommingConnection.bpmn2");
	        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotNull();
            assertThat(e.getMessage().contains("has no incoming connection")).isTrue();
        }

    }

    @Test
    public void testSignalBoundaryEventOnMultiInstanceSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "subprocess/BPMN2-MultiInstanceSubprocessWithBoundarySignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> approvers = new ArrayList<String>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        KogitoProcessInstance processInstance = kruntime.startProcess("boundary-catch-error-event", params);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        kruntime.signalEvent("Outside", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testSignalBoundaryEventNoInteruptOnMultiInstanceSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "subprocess/BPMN2-MultiInstanceSubprocessWithBoundarySignalNoInterupting.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> approvers = new ArrayList<String>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        KogitoProcessInstance processInstance = kruntime.startProcess("boundary-catch-error-event", params);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        kruntime.signalEvent("Outside", null, processInstance.getStringId());

        assertProcessInstanceActive(processInstance.getStringId(), ksession);

        for (KogitoWorkItem wi : workItems) {
        	kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testErrorBoundaryEventOnMultiInstanceSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "subprocess/BPMN2-MultiInstanceSubprocessWithBoundaryError.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> approvers = new ArrayList<String>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        KogitoProcessInstance processInstance = kruntime.startProcess("boundary-catch-error-event", params);
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
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        kruntime.signalEvent("Inside", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testIntermediateCatchEventSignalAndBoundarySignalEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryEventWithSignals.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("boundaryeventtest");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        // now signal process instance
        kruntime.signalEvent("moveon", "", processInstance.getStringId());
        assertProcessInstanceActive(processInstance);

        KogitoWorkItem wi = handler.getWorkItem();
        assertThat(wi).isNotNull();

        // signal boundary event on user task
        kruntime.signalEvent("moveon", "", processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testSignalIntermediateThrowEventWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn",
                "BPMN2-IntermediateThrowEventSignalWithTransformation.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "john");
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");

        KogitoProcessInstance processInstance2 = kruntime.startProcess("SignalIntermediateEvent", params);
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isEqualTo("JOHN");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testSignalBoundaryEventWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-BoundarySignalEventOnTaskWithTransformation.bpmn",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "john");
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundarySignalOnTask");

        KogitoProcessInstance processInstance2 = kruntime.startProcess("SignalIntermediateEvent", params);
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isEqualTo("JOHN");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testMessageIntermediateThrowWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateThrowEventMessageWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        final StringBuffer messageContent = new StringBuffer();
        kruntime.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler(){

					@Override
					public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
						// collect message content for verification
						messageContent.append(workItem.getParameter("Message"));
						super.executeWorkItem(workItem, manager);
					}

        });
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        assertThat(messageContent.toString()).isEqualTo("MYVALUE");

    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testIntermediateCatchEventSignalWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventSignalWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");
        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull();
        assertThat(var).isEqualTo("SOMEVALUE");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testIntermediateCatchEventMessageWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventMessageWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        kruntime.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull();
        assertThat(var).isEqualTo("SOMEVALUE");
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testEventSubprocessSignalWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-EventSubprocessSignalWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        kruntime.signalEvent("MySignal", "john", processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "end-sub");

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull();
        assertThat(var).isEqualTo("JOHN");

    }

    @Test
    public void testMultipleMessageSignalSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-MultipleMessageSignalSubprocess.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.bpmn.Multiple_MessageSignal_Subprocess");
		logger.debug("Parent Process ID: " + processInstance.getStringId());

		kruntime.signalEvent("Message-Message 1","Test",processInstance.getStringId());
		assertProcessInstanceActive(processInstance.getStringId(), ksession);

		kruntime.signalEvent("Message-Message 1","Test",processInstance.getStringId());
		assertProcessInstanceCompleted(processInstance.getStringId(), ksession);
    }

    @Test
    public void testIntermediateCatchEventSignalWithRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventSignalWithRef.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        kruntime.signalEvent("Signal1", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    @Timeout(10)
    public void testTimerMultipleInstances() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        KieBase kbase = createKnowledgeBase("BPMN2-MultiInstanceLoopBoundaryTimer.bpmn2");

        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("boundaryTimerMultipleInstances");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(3);

        for (KogitoWorkItem wi : workItems) {
            kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }

        assertProcessInstanceFinished(processInstance, ksession);
    }

   

    @Test
    @Timeout(10)
    public void testIntermediateTimerParallelGateway() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Timer1", 1);
        NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Timer2", 1);
        NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Timer3", 1);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-IntermediateTimerParallelGateway.bpmn2");

        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);
        ksession.addEventListener(countDownListener2);
        ksession.addEventListener(countDownListener3);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation.timer-parallel");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        countDownListener2.waitTillCompleted();
        countDownListener3.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getStringId(), ksession);

    }

    @Test
    @Timeout(10)
    public void testIntermediateTimerEventMI() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("After timer", 3);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-IntermediateTimerEventMI.bpmn2");

        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("defaultprocessid");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance.getStringId(), ksession);

        kruntime.abortProcessInstance(processInstance.getStringId());

        assertProcessInstanceAborted(processInstance.getStringId(), ksession);
    }

    @Test
    public void testThrowIntermediateSignalWithScope() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2IntermediateThrowEventScope.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();

        KogitoProcessInstance processInstance = kruntime.startProcess("intermediate-event-scope", params);
        KogitoProcessInstance processInstance2 = kruntime.startProcess("intermediate-event-scope", params);

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        assertNodeActive(processInstance.getStringId(), ksession, "Complete work", "Wait");
        assertNodeActive(processInstance2.getStringId(), ksession, "Complete work", "Wait");

        List<KogitoWorkItem> items = handler.getWorkItems();

        KogitoWorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_output", "sending event");

        kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), result);

        assertProcessInstanceCompleted(processInstance);
        assertProcessInstanceActive(processInstance2);
        assertNodeActive(processInstance2.getStringId(), ksession, "Complete work", "Wait");

        wi = items.get(1);
        kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), result);
        assertProcessInstanceCompleted(processInstance2);

    }

    @Test
    public void testThrowEndSignalWithScope() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2EndThrowEventScope.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();

        KogitoProcessInstance processInstance = kruntime.startProcess("end-event-scope", params);
        KogitoProcessInstance processInstance2 = kruntime.startProcess("end-event-scope", params);

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        assertNodeActive(processInstance.getStringId(), ksession, "Complete work", "Wait");
        assertNodeActive(processInstance2.getStringId(), ksession, "Complete work", "Wait");

        List<KogitoWorkItem> items = handler.getWorkItems();

        KogitoWorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_output", "sending event");

        kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), result);

        assertProcessInstanceCompleted(processInstance);
        assertProcessInstanceActive(processInstance2);
        assertNodeActive(processInstance2.getStringId(), ksession, "Complete work", "Wait");

        wi = items.get(1);
        kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), result);
        assertProcessInstanceCompleted(processInstance2);

    }



    @Test
    public void testThrowIntermediateSignalWithExternalScope() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventExternalScope.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        TestWorkItemHandler handler = new TestWorkItemHandler();
        WorkItemHandler externalHandler = new KogitoWorkItemHandler() {

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

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        kruntime.getWorkItemManager().registerWorkItemHandler("External Send Task", externalHandler);
        Map<String, Object> params = new HashMap<String, Object>();

        KogitoProcessInstance processInstance = kruntime.startProcess("intermediate-event-scope", params);

        assertProcessInstanceActive(processInstance);

        assertNodeActive(processInstance.getStringId(), ksession, "Complete work", "Wait");

        List<KogitoWorkItem> items = handler.getWorkItems();
        assertThat(items.size()).isEqualTo(1);
        KogitoWorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_output", "sending event");

        kruntime.getWorkItemManager().completeWorkItem(wi.getStringId(), result);

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testIntermediateCatchEventSignalWithVariable() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("signalName", signalVar);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent", parameters);
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);
        kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        // now signal process instance
        kruntime.signalEvent(signalVar, "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testSignalIntermediateThrowWithVariable() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignalWithVariable.bpmn2", "BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        // create catch process instance
        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("signalName", signalVar);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent", parameters);
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);
        kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        params.put("signalName", signalVar);
        KogitoProcessInstance processInstanceThrow = kruntime.startProcess("SignalIntermediateEvent", params);
        assertThat(processInstanceThrow.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);

        // catch process instance should now be completed
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testInvalidDateTimerBoundary() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-TimerBoundaryEventDateInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (RuntimeException e) {
            assertThat(e.getMessage().contains("Could not parse date 'abcdef'")).isTrue();
        }
    }

    @Test
    public void testInvalidDurationTimerBoundary() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-TimerBoundaryEventDurationInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertThat(e.getMessage().contains("Could not parse delay 'abcdef'")).isTrue();
        }
    }

    @Test
    public void testInvalidCycleTimerBoundary() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-TimerBoundaryEventCycleInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertThat(e.getMessage().contains("Could not parse delay 'abcdef'")).isTrue();
        }
    }

    @Test
    public void testIntermediateCatchEventConditionSetVariableAfter() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionSetVariableAfter.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(new RuleAwareProcessEventListener());

        Collection<? extends Object> processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof KogitoProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(1);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        assertProcessInstanceFinished(processInstance, ksession);

        processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof KogitoProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(0);
    }

    @Test
    public void testIntermediateCatchEventConditionRemovePIAfter() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(new RuleAwareProcessEventListener());

        Collection<? extends Object> processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof KogitoProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(1);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        assertProcessInstanceFinished(processInstance, ksession);

        processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof KogitoProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(0);
    }

    

    @Test
    @Timeout(10)
    public void testEventBasedSplitWithCronTimerAndSignal() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");
        try {
            NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Request photos of order in use", 1);
            NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Request an online review", 1);
            NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Send a thank you card", 1);
            NodeLeftCountDownProcessEventListener countDownListener4 = new NodeLeftCountDownProcessEventListener("Request an online review", 1);
            KieBase kbase = createKnowledgeBase("timer/BPMN2-CronTimerWithEventBasedGateway.bpmn2");
            ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
            
            TestWorkItemHandler handler = new TestWorkItemHandler();
            kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);       
            ksession.addEventListener(countDownListener);
            ksession.addEventListener(countDownListener2);
            ksession.addEventListener(countDownListener3);
            ksession.addEventListener(countDownListener4);
            
            KogitoProcessInstance processInstance = kruntime.startProcess("timerWithEventBasedGateway");
            assertProcessInstanceActive(processInstance.getStringId(), ksession);
            
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
            assertThat(wi.size()).isEqualTo(3);
    
            kruntime.abortProcessInstance(processInstance.getStringId());
        } finally {
            // clear property only as the only relevant value is when it's set to true
            System.clearProperty("jbpm.enable.multi.con");
        }
    }
    
    @Test
    public void testEventSubprocessWithEmbeddedSignals() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessErrorSignalEmbedded.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
               
        KogitoProcessInstance processInstance = kruntime.startProcess("project2.myerrorprocess");
        
        assertProcessInstanceActive(processInstance.getStringId(), ksession);
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        kruntime.signalEvent("signal1", null, processInstance.getStringId());        
        assertProcessInstanceActive(processInstance.getStringId(), ksession);
  
        kruntime.signalEvent("signal2", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance.getStringId(), ksession);
        
        kruntime.signalEvent("signal3", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testEventSubprocessWithExpression() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessSignalExpression.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
               
        Map<String, Object> params = new HashMap<>();
        params.put("x", "signalling");
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EventSubprocessSignalExpression", params);
        
        assertProcessInstanceActive(processInstance.getStringId(), ksession);
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        kruntime.signalEvent("signalling", null, processInstance.getStringId());        
  
        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testConditionalProcessFactInsertedBefore() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionPI.bpmn2", "BPMN2-IntermediateCatchEventSignal.bpmn2");        
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        Person person0 = new Person("john");
        ksession.insert(person0);
        
        Map<String, Object> params0 = new HashMap<String, Object>();
        params0.put("name", "john");
        KogitoProcessInstance pi0 = kruntime.startProcess("IntermediateCatchEvent", params0);
        ksession.insert(pi0);

        Person person = new Person("Jack");
        ksession.insert(person);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Poul");
        KogitoProcessInstance pi = kruntime.startProcess("IntermediateCatchEventPI", params);
        ksession.insert(pi);
        pi = kruntime.getProcessInstance(pi.getStringId());
        assertThat(pi).isNotNull();
        
        Person person2 = new Person("Poul");
        ksession.insert(person2);
        
        pi = kruntime.getProcessInstance(pi.getStringId());
        assertThat(pi).isNull();
        
    }

    @Test
    public void testBoundarySignalEventOnSubprocessWithVariableResolution() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubprocessWithSignalEndEventAndSignalBoundaryEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        ksession.addEventListener(LOGGING_EVENT_LISTENER);

        Map<String, Object> params = new HashMap<>();
        params.put("document-ref", "signalling");
        params.put("message", "hello");
        KogitoProcessInstance processInstance = kruntime.startProcess("SubprocessWithSignalEndEventAndSignalBoundaryEvent", params);

        assertNodeTriggered(processInstance.getStringId(), "sysout from boundary", "end2");
        assertNotNodeTriggered(processInstance.getStringId(),"end1");

        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testSignalEndWithData() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateThrowEventSignalWithData.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );

        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                                                              new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        KogitoProcessInstance processInstance = kruntime.startProcess("testThrowingSignalEvent", params);

        assertProcessInstanceActive(processInstance);
        
        kruntime.signalEvent("mysignal", null, processInstance.getStringId());
        
        assertProcessInstanceCompleted(processInstance);

    }
    
    @Test
    public void testDynamicCatchEventSignal() throws Exception {
        KieBase kbase = createKnowledgeBase("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        final List<String> instances = new ArrayList<>();
        
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add( (( KogitoProcessInstance ) event.getProcessInstance()).getStringId());
            }
            
        });
        
        KogitoProcessInstance processInstance = kruntime.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
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
        
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        
        assertProcessInstanceFinished(processInstance, ksession);
        
        for (String id : instances) {
            assertNull(kruntime.getProcessInstance(id), "Child process instance has not been finished.");
        }
    }

    @Test
    public void testDynamicCatchEventSignalWithVariableUpdated() throws Exception {        
        KieBase kbase = createKnowledgeBase("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        final List<String> instances = new ArrayList<>();
        
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add( (( KogitoProcessInstance ) event.getProcessInstance()).getStringId());
            }
            
        });
        
        KogitoProcessInstance processInstance = kruntime.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
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
        ksession.execute(new KogitoSetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));
        
        // now complete user task to signal all child instances to stop
        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        
        assertProcessInstanceFinished(processInstance, ksession);
        
        for (String id : instances) {
            assertNull(kruntime.getProcessInstance(id), "Child process instance has not been finished.");
        }
        
        KogitoProcessInstance updatedChild = kruntime.getProcessInstance(changeProcessInstanceId);
        assertProcessInstanceActive(updatedChild);
        
        kruntime.signalEvent("stopChild:999", null, changeProcessInstanceId);
        assertProcessInstanceFinished(updatedChild, ksession);
    }
    
    @RequirePersistence
    @Test
    public void testDynamicCatchEventSignalWithVariableUpdatedBroadcastSignal() throws Exception {        
        KieBase kbase = createKnowledgeBase("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        ksession = createKnowledgeSession(kbase);        
        KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime( ksession );
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        final List<String> instances = new ArrayList<>();
        
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add( (( KogitoProcessInstance ) event.getProcessInstance()).getStringId());
            }
            
        });
        
        KogitoProcessInstance processInstance = kruntime.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
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
        ksession.execute(new KogitoSetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));
        
        // now complete user task to signal all child instances to stop
        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        
        assertProcessInstanceFinished(processInstance, ksession);
        
        for (String id : instances) {
            assertNull(kruntime.getProcessInstance(id), "Child process instance has not been finished.");
        }
        
        KogitoProcessInstance updatedChild = kruntime.getProcessInstance(changeProcessInstanceId);
        assertProcessInstanceActive(updatedChild);
        
        kruntime.signalEvent("stopChild:999", null, updatedChild.getStringId());
        assertProcessInstanceFinished(updatedChild, ksession);
    }
}
