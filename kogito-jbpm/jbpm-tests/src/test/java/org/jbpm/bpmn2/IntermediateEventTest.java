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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbpm.bpmn2.activity.BoundarySignalEventOnTaskWithTransformationModel;
import org.jbpm.bpmn2.activity.BoundarySignalEventOnTaskWithTransformationProcess;
import org.jbpm.bpmn2.event.BoundarySignalWithNameEventOnTaskModel;
import org.jbpm.bpmn2.event.BoundarySignalWithNameEventOnTaskProcess;
import org.jbpm.bpmn2.event.BoundaryTimerCycleISOModel;
import org.jbpm.bpmn2.event.BoundaryTimerCycleISOProcess;
import org.jbpm.bpmn2.event.BoundaryTimerCycleISOVariableModel;
import org.jbpm.bpmn2.event.BoundaryTimerCycleISOVariableProcess;
import org.jbpm.bpmn2.intermediate.BoundarySignalEventOnTaskModel;
import org.jbpm.bpmn2.intermediate.BoundarySignalEventOnTaskProcess;
import org.jbpm.bpmn2.intermediate.EventBasedSplit2Model;
import org.jbpm.bpmn2.intermediate.EventBasedSplit2Process;
import org.jbpm.bpmn2.intermediate.EventBasedSplit4Model;
import org.jbpm.bpmn2.intermediate.EventBasedSplit4Process;
import org.jbpm.bpmn2.intermediate.EventBasedSplitModel;
import org.jbpm.bpmn2.intermediate.EventBasedSplitProcess;
import org.jbpm.bpmn2.intermediate.EventSubprocessErrorSignalEmbeddedModel;
import org.jbpm.bpmn2.intermediate.EventSubprocessErrorSignalEmbeddedProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventMessageModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventMessageProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventMessageWithRefModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventMessageWithRefProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventMessageWithTransformationModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventMessageWithTransformationProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventSignal2Model;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventSignal2Process;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventSignalModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventSignalProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventSignalWithTransformationModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventSignalWithTransformationProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycle1Model;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycle1Process;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycle2Model;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycle2Process;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycle3Model;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycle3Process;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycleCronModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycleCronProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycleISOModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerCycleISOProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDateISOModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDateISOProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationISOModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationISOProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationWithErrorModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationWithErrorProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchEventTimerDurationWithErrorProcessInstance;
import org.jbpm.bpmn2.intermediate.IntermediateCatchSignalBetweenUserTasksModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchSignalBetweenUserTasksProcess;
import org.jbpm.bpmn2.intermediate.IntermediateCatchSignalSingleModel;
import org.jbpm.bpmn2.intermediate.IntermediateCatchSignalSingleProcess;
import org.jbpm.bpmn2.intermediate.IntermediateLinkEventModel;
import org.jbpm.bpmn2.intermediate.IntermediateLinkEventProcess;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventMessageModel;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventMessageProcess;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventMessageWithTransformationModel;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventMessageWithTransformationProcess;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventNoneModel;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventNoneProcess;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventSignalModel;
import org.jbpm.bpmn2.intermediate.IntermediateThrowEventSignalProcess;
import org.jbpm.bpmn2.intermediate.LinkEventCompositeProcessModel;
import org.jbpm.bpmn2.intermediate.LinkEventCompositeProcessProcess;
import org.jbpm.bpmn2.intermediate.SignalBoundaryEventInterruptingModel;
import org.jbpm.bpmn2.intermediate.SignalBoundaryEventInterruptingProcess;
import org.jbpm.bpmn2.loop.MultiInstanceLoopBoundaryTimerModel;
import org.jbpm.bpmn2.loop.MultiInstanceLoopBoundaryTimerProcess;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsProcessSequentialModel;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsProcessSequentialProcess;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsProcessWithOutputAndScriptsModel;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsProcessWithOutputAndScriptsProcess;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsTaskModel;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsTaskProcess;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsTaskSequentialModel;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsTaskSequentialProcess;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequentialModel;
import org.jbpm.bpmn2.loop.MultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequentialProcess;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestUserTaskWorkItemHandler;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.subprocess.EventSubprocessConditionalModel;
import org.jbpm.bpmn2.subprocess.EventSubprocessConditionalProcess;
import org.jbpm.bpmn2.subprocess.EventSubprocessMessageModel;
import org.jbpm.bpmn2.subprocess.EventSubprocessMessageProcess;
import org.jbpm.bpmn2.subprocess.EventSubprocessSignalWithTransformationModel;
import org.jbpm.bpmn2.subprocess.EventSubprocessSignalWithTransformationProcess;
import org.jbpm.bpmn2.test.RequirePersistence;
import org.jbpm.bpmn2.timer.TimerBoundaryEventCycle1Model;
import org.jbpm.bpmn2.timer.TimerBoundaryEventCycle1Process;
import org.jbpm.bpmn2.timer.TimerBoundaryEventCycle2Model;
import org.jbpm.bpmn2.timer.TimerBoundaryEventCycle2Process;
import org.jbpm.bpmn2.timer.TimerBoundaryEventInterruptingModel;
import org.jbpm.bpmn2.timer.TimerBoundaryEventInterruptingOnTaskCancelTimerModel;
import org.jbpm.bpmn2.timer.TimerBoundaryEventInterruptingOnTaskCancelTimerProcess;
import org.jbpm.bpmn2.timer.TimerBoundaryEventInterruptingOnTaskModel;
import org.jbpm.bpmn2.timer.TimerBoundaryEventInterruptingOnTaskProcess;
import org.jbpm.bpmn2.timer.TimerBoundaryEventInterruptingProcess;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventListener;
import org.jbpm.process.workitem.builtin.DoNothingWorkItemHandler;
import org.jbpm.process.workitem.builtin.ReceiveTaskHandler;
import org.jbpm.process.workitem.builtin.SendTaskHandler;
import org.jbpm.process.workitem.builtin.SystemOutWorkItemHandler;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.ProcessCompletedCountDownProcessEventListener;
import org.jbpm.test.utils.EventTrackerProcessListener;
import org.jbpm.test.utils.ProcessTestHelper;
import org.jbpm.test.utils.ProcessTestHelper.CompletionKogitoEventListener;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.Application;
import org.kie.kogito.event.impl.MessageProducer;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.NamedDataType;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;

import static java.util.Collections.emptyMap;
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
    public void testBoundaryTimerCycleISO() {
        Application app = ProcessTestHelper.newApplication();
        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("Send Update Timer", 3);
        ProcessTestHelper.registerHandler(app, "Human Task", new TestUserTaskWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, listener);
        org.kie.kogito.process.Process<BoundaryTimerCycleISOModel> definition = BoundaryTimerCycleISOProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<BoundaryTimerCycleISOModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        listener.waitTillCompleted();
        ProcessTestHelper.completeWorkItem(instance, Collections.emptyMap(), "john");
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testBoundaryTimerCycleISOVariable() {
        Application app = ProcessTestHelper.newApplication();
        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("Send Update Timer",
                3);
        ProcessTestHelper.registerHandler(app, "Human Task", new TestUserTaskWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, listener);
        org.kie.kogito.process.Process<BoundaryTimerCycleISOVariableModel> definition = BoundaryTimerCycleISOVariableProcess
                .newProcess(app);
        BoundaryTimerCycleISOVariableModel model = definition.createModel();
        model.setCronStr("R3/PT0.1S");
        org.kie.kogito.process.ProcessInstance<BoundaryTimerCycleISOVariableModel> instance = definition
                .createInstance(model);
        instance.start();
        listener.waitTillCompleted();
        ProcessTestHelper.completeWorkItem(instance, Collections.emptyMap(), "john");
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalBoundaryEvent() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new TestWorkItemHandler());

        org.kie.kogito.process.Process<BoundarySignalEventOnTaskModel> signalEventOnTaskProcess = BoundarySignalEventOnTaskProcess.newProcess(app);
        org.kie.kogito.process.Process<IntermediateThrowEventSignalModel> throwEventSignalProcess = IntermediateThrowEventSignalProcess.newProcess(app);

        org.kie.kogito.process.ProcessInstance<BoundarySignalEventOnTaskModel> taskProcessInstance = signalEventOnTaskProcess.createInstance(signalEventOnTaskProcess.createModel());
        taskProcessInstance.start();

        Set<EventDescription<?>> eventDescriptions = taskProcessInstance.events();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("MySignal");
        assertThat(eventDescriptions).extracting("eventType").contains("signal");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(taskProcessInstance.id());
        assertThat(eventDescriptions).filteredOn("eventType", "signal").hasSize(1).extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));

        org.kie.kogito.process.ProcessInstance<IntermediateThrowEventSignalModel> signalProcessInstance = throwEventSignalProcess.createInstance(throwEventSignalProcess.createModel());
        signalProcessInstance.start();

        assertThat(signalProcessInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(taskProcessInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalBoundaryNonEffectiveEvent() throws Exception {
        final String signal = "signalTest";
        final AtomicBoolean eventAfterNodeLeftTriggered = new AtomicBoolean(false);
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-BoundaryEventWithNonEffectiveSignal.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // BoundaryEventNodeInstance
                if (signal.equals(event.getNodeInstance().getNodeName())) {
                    eventAfterNodeLeftTriggered.set(true);
                }
            }
        });
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryEventWithNonEffectiveSignal");

        // outer human work
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), emptyMap());

        kruntime.signalEvent(signal, signal);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertThat(eventAfterNodeLeftTriggered).isTrue();
    }

    @Test
    public void testSignalBoundaryEventOnTask() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        ProcessTestHelper.registerProcessEventListener(app, LOGGING_EVENT_LISTENER);

        org.kie.kogito.process.Process<BoundarySignalEventOnTaskModel> processDefinition = BoundarySignalEventOnTaskProcess.newProcess(app);
        BoundarySignalEventOnTaskModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<BoundarySignalEventOnTaskModel> instance = processDefinition.createInstance(model);
        instance.start();

        Set<EventDescription<?>> eventDescriptions = instance.events();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("MySignal", "workItemCompleted");
        assertThat(eventDescriptions).extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions).extracting("nodeId").contains("BoundaryEvent_2", "UserTask_1");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(instance.id());
        assertThat(eventDescriptions).filteredOn("eventType", "signal").hasSize(1).extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));
        assertThat(eventDescriptions).filteredOn("eventType", "signal").hasSize(1).extracting("nodeInstanceId")
                .containsOnlyNulls();
        assertThat(eventDescriptions).filteredOn("eventType", "workItem").hasSize(1).extracting("nodeInstanceId")
                .doesNotContainNull();

        instance.send(Sig.of("MySignal", "value"));
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalBoundaryEventOnTaskWithSignalName() throws Exception {

        Application app = ProcessTestHelper.newApplication();

        ProcessTestHelper.registerHandler(app, "Human Task", new TestWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, LOGGING_EVENT_LISTENER);
        org.kie.kogito.process.Process<BoundarySignalWithNameEventOnTaskModel> definition = BoundarySignalWithNameEventOnTaskProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<BoundarySignalWithNameEventOnTaskModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();

        instance.send(Sig.of("MySignal", "value"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testSignalBoundaryEventOnTaskComplete() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        ProcessTestHelper.registerProcessEventListener(app, LOGGING_EVENT_LISTENER);
        org.kie.kogito.process.Process<BoundarySignalEventOnTaskModel> processDefinition = BoundarySignalEventOnTaskProcess.newProcess(app);
        BoundarySignalEventOnTaskModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<BoundarySignalEventOnTaskModel> instance = processDefinition.createInstance(model);
        instance.start();
        KogitoWorkItem workItem = handler.getWorkItem();
        instance.completeWorkItem(workItem.getStringId(), null);
        workItem = handler.getWorkItem();
        instance.completeWorkItem(workItem.getStringId(), null);
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalBoundaryEventInterrupting() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<SignalBoundaryEventInterruptingModel> processDefinition = SignalBoundaryEventInterruptingProcess.newProcess(app);
        SignalBoundaryEventInterruptingModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<SignalBoundaryEventInterruptingModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        Set<EventDescription<?>> eventDescriptions = instance.events();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("MyMessage", "workItemCompleted");
        assertThat(eventDescriptions).extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(instance.id());
        assertThat(eventDescriptions).filteredOn("eventType", "signal").hasSize(1).extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("AttachedToID") && m.containsKey("AttachedToName"));
        instance.send(Sig.of("MyMessage", null));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalIntermediateThrow() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<IntermediateThrowEventSignalModel> processDefinition = IntermediateThrowEventSignalProcess.newProcess(app);
        IntermediateThrowEventSignalModel model = processDefinition.createModel();
        model.setX("MyValue");
        org.kie.kogito.process.ProcessInstance<IntermediateThrowEventSignalModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalBetweenProcesses() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<IntermediateCatchSignalSingleModel> catchSignalSingleProcess = IntermediateCatchSignalSingleProcess.newProcess(app);
        org.kie.kogito.process.Process<IntermediateThrowEventSignalModel> throwEventSignalProcess = IntermediateThrowEventSignalProcess.newProcess(app);

        org.kie.kogito.process.ProcessInstance<IntermediateCatchSignalSingleModel> signalSingleProcessInstance = catchSignalSingleProcess.createInstance(catchSignalSingleProcess.createModel());
        signalSingleProcessInstance.start();
        assertThat(signalSingleProcessInstance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        signalSingleProcessInstance.completeWorkItem(workItem.getStringId(), null);

        org.kie.kogito.process.ProcessInstance<IntermediateThrowEventSignalModel> throwEventSignalProcessInstance = throwEventSignalProcess.createInstance(throwEventSignalProcess.createModel());
        throwEventSignalProcessInstance.start();
        assertThat(throwEventSignalProcessInstance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(signalSingleProcessInstance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEventBasedSplit() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Email1", new SystemOutWorkItemHandler());
        ProcessTestHelper.registerHandler(app, "Email2", new SystemOutWorkItemHandler());

        org.kie.kogito.process.Process<EventBasedSplitModel> processDefinition = EventBasedSplitProcess.newProcess(app);
        EventBasedSplitModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<EventBasedSplitModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        Set<EventDescription<?>> eventDescriptions = instance.events();
        assertThat(eventDescriptions).hasSize(2).extracting(EventDescription::getEvent).contains("Yes", "No");
        assertThat(eventDescriptions).extracting(EventDescription::getEventType).contains("signal");
        assertThat(eventDescriptions).extracting(EventDescription::getDataType).hasOnlyElementsOfType(NamedDataType.class)
                .extracting("dataType").hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions).extracting(EventDescription::getProcessInstanceId).contains(instance.id());
        assertThat(eventDescriptions).extracting(EventDescription::getNodeInstanceId).doesNotContainNull();

        instance.send(Sig.of("Yes", "YesValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

        instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance.send(Sig.of("No", "NoValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEventBasedSplitBefore() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Email1", new DoNothingWorkItemHandler());
        ProcessTestHelper.registerHandler(app, "Email2", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<EventBasedSplitModel> processDefinition = EventBasedSplitProcess.newProcess(app);
        EventBasedSplitModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<EventBasedSplitModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance.send(Sig.of("Yes", "YesValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance.send(Sig.of("No", "NoValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testEventBasedSplitAfter() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Email1", new SystemOutWorkItemHandler());
        ProcessTestHelper.registerHandler(app, "Email2", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<EventBasedSplitModel> processDefinition = EventBasedSplitProcess.newProcess(app);
        EventBasedSplitModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<EventBasedSplitModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("Yes", "YesValue"));
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);

        instance.send(Sig.of("No", "NoValue"));
    }

    @Test
    public void testEventBasedSplit2() {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(2);
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Email1", new SystemOutWorkItemHandler());
        ProcessTestHelper.registerHandler(app, "Email2", new SystemOutWorkItemHandler());

        org.kie.kogito.process.Process<EventBasedSplit2Model> processDefinition = EventBasedSplit2Process.newProcess(app);
        EventBasedSplit2Model model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<EventBasedSplit2Model> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        Set<EventDescription<?>> eventDescriptions = instance.events();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("Yes", TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions).extracting("eventType").contains("signal", "timer");
        assertThat(eventDescriptions).filteredOn(i -> i.getDataType() != null).extracting("dataType")
                .hasOnlyElementsOfType(NamedDataType.class).extracting("dataType")
                .hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions).extracting("processInstanceId").contains(instance.id());
        assertThat(eventDescriptions).filteredOn("eventType", "timer").hasSize(1).extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Delay"));

        instance.send(Sig.of("Yes", "YesValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

        instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEventBasedSplit3() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit3.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit3");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("Yes");
        assertThat(eventDescriptions).extracting("eventType").contains("signal", "conditional");
        assertThat(eventDescriptions).filteredOn(i -> i.getDataType() != null).extracting("dataType")
                .hasOnlyElementsOfType(NamedDataType.class).extracting("dataType")
                .hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.signalEvent("Yes", "YesValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        // Condition
        processInstance = kruntime.startProcess("EventBasedSplit3");
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.getKieSession().insert(jack);

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testEventBasedSplit4() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Email1", new SystemOutWorkItemHandler());
        ProcessTestHelper.registerHandler(app, "Email2", new SystemOutWorkItemHandler());

        org.kie.kogito.process.Process<EventBasedSplit4Model> processDefinition = EventBasedSplit4Process.newProcess(app);
        EventBasedSplit4Model model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<EventBasedSplit4Model> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        Set<EventDescription<?>> eventDescriptions = instance.events();
        assertThat(eventDescriptions).hasSize(2)
                .extracting(EventDescription::getEvent)
                .contains("Message-YesMessage", "Message-NoMessage");
        assertThat(eventDescriptions).extracting(EventDescription::getEventType)
                .contains("message", "message");
        assertThat(eventDescriptions).extracting(EventDescription::getDataType)
                .hasOnlyElementsOfType(NamedDataType.class);
        assertThat(eventDescriptions).extracting(event -> ((NamedDataType) event.getDataType()).getDataType())
                .hasOnlyElementsOfType(StringDataType.class);
        assertThat(eventDescriptions).extracting(EventDescription::getProcessInstanceId)
                .contains(instance.id());

        instance.send(Sig.of("Message-YesMessage", "YesValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

        instance = processDefinition.createInstance(model);
        instance.start();
        instance.send(Sig.of("Message-NoMessage", "NoValue"));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventBasedSplit5.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // Yes
        KogitoProcessInstance processInstance = kruntime.startProcess("EventBasedSplit5");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);

        receiveTaskHandler.getWorkItemId().stream().findFirst().ifPresent(id -> kruntime.getKogitoWorkItemManager().completeWorkItem(id, Map.of("Message", "YesValue")));

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);

        receiveTaskHandler.getWorkItemId().forEach(id -> kruntime.getKogitoWorkItemManager().completeWorkItem(id, Map.of("Message", "NoValue")));

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // No
        processInstance = kruntime.startProcess("EventBasedSplit5");
        receiveTaskHandler.getWorkItemId().stream().findFirst().ifPresent(id -> kruntime.getKogitoWorkItemManager().completeWorkItem(id, Map.of("Message", "NoValue")));

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);

        receiveTaskHandler.getWorkItemId().forEach(id -> kruntime.getKogitoWorkItemManager().completeWorkItem(id, Map.of("Message", "YesValue")));

    }

    @Test
    public void testEventBasedSplitWithSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-ExclusiveEventBasedGatewayInSubprocess.bpmn2");

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
        String[] nodes = { "start", "User Task 1", "end", "Sub Process 1", "start-sub", "sub-script", "end-sub" };
        runTestEventSubprocessSignal("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignal.bpmn2",
                "EventSubprocessSignal", nodes);
    }

    @Test
    public void testEventSubprocessSignalNested() throws Exception {
        String[] nodes = { "Start", "Sub Process", "Sub Start", "Sub Sub Process", "Sub Sub Start", "Sub Sub User Task",
                "Sub Sub Sub Process", "start-sub", "sub-script", "end-sub", "Sub Sub End", "Sub End", "End" };
        runTestEventSubprocessSignal("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalNested.bpmn2",
                "EventSubprocessSignalNested", nodes);
    }

    public void runTestEventSubprocessSignal(String processFile, String[] completedNodes) throws Exception {
        runTestEventSubprocessSignal(processFile, processFile, completedNodes);
    }

    public void runTestEventSubprocessSignal(String processFile, String processId, String[] completedNodes)
            throws Exception {
        kruntime = createKogitoProcessRuntime(processFile);
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("sub-script")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };

        kruntime.getProcessEventManager().addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess(processId);
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("MySignal", "workItemCompleted");
        assertThat(eventDescriptions).extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalWithStateNode.bpmn2");
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
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessSignalWithStateNode");
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

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
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemTopProcess.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1", "end", "Sub Process 1", "start-sub",
                "User Task 2", "end-sub");
        assertThat(executednodes).hasSize(4);

    }

    @Test
    public void testEventSubprocessSignalInterrupting() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalInterrupting.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessSignalInterrupting");
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);

        kruntime.signalEvent("MySignal", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1", "Sub Process 1", "start-sub",
                "Script Task 1", "end-sub");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessMessage() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        TestUserTaskWorkItemHandler workItemHandler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getNodeId().toExternalFormat());
                }
            }

        };
        ProcessTestHelper.registerProcessEventListener(app, listener);

        EventTrackerProcessListener trackerListener = new EventTrackerProcessListener();
        ProcessTestHelper.registerProcessEventListener(app, trackerListener);

        org.kie.kogito.process.Process<EventSubprocessMessageModel> definition = EventSubprocessMessageProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<EventSubprocessMessageModel> instance = definition
                .createInstance(definition.createModel());

        instance.start();
        Set<EventDescription<?>> eventDescriptions = instance.events();
        assertThat(eventDescriptions).hasSize(3).extracting("event").contains("Message-HelloMessage",
                "workItemCompleted", "HelloMessage");
        assertThat(eventDescriptions).extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(instance.id());

        instance.send(Sig.of("Message-HelloMessage"));
        definition.send(Sig.of("Message-HelloMessage"));
        definition.send(Sig.of("Message-HelloMessage"));
        definition.send(Sig.of("Message-HelloMessage"));

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ProcessTestHelper.completeWorkItem(instance, Collections.emptyMap(), "john");

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

        assertThat(trackerListener.tracked()).anyMatch(ProcessTestHelper.triggered("start"))
                .anyMatch(ProcessTestHelper.triggered("User Task 1")).anyMatch(ProcessTestHelper.triggered("end"))
                .anyMatch(ProcessTestHelper.left("Sub Process 1"))
                .anyMatch(ProcessTestHelper.left("start-sub"))
                .anyMatch(ProcessTestHelper.triggered("Script Task 1"))
                .anyMatch(ProcessTestHelper.triggered("end-sub"));

        assertThat(executednodes).hasSize(4);

    }

    @Test
    public void testEventSubprocessTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener(
                "Script Task 1", 1);
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessTimer.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("workItemCompleted",
                TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions).extracting("eventType").contains("workItem", "timer");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions).filteredOn("eventType", "timer").hasSize(1).extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Delay"));
        countDownListener.waitTillCompleted();

        eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions).hasSize(1).extracting("event").contains("workItemCompleted");
        assertThat(eventDescriptions).extracting("eventType").contains("workItem");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1", "end", "Sub Process 1", "start-sub",
                "Script Task 1", "end-sub");

    }

    @Test
    @RequirePersistence
    public void testEventSubprocessTimerCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener(
                "Script Task 1", 4);

        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessTimerCycle.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessTimerCycle");
        assertProcessInstanceActive(processInstance);

        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("workItemCompleted",
                TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions).extracting("eventType").contains("workItem", "timer");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions).filteredOn("eventType", "timer").hasSize(1).extracting("properties", Map.class)
                .anyMatch(m -> m.containsKey("TimerID") && m.containsKey("Period"));

        countDownListener.waitTillCompleted();

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1", "end", "start-sub", "Script Task 1",
                "end-sub");

    }

    @Test
    public void testEventSubprocessConditional() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        ProcessTestHelper.registerProcessEventListener(app, listener);
        EventTrackerProcessListener trackerListener = new EventTrackerProcessListener();
        ProcessTestHelper.registerProcessEventListener(app, trackerListener);
        TestUserTaskWorkItemHandler workItemHandler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);

        org.kie.kogito.process.Process<EventSubprocessConditionalModel> definition = EventSubprocessConditionalProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<EventSubprocessConditionalModel> instance = definition
                .createInstance(definition.createModel());

        instance.start();

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        Person person = new Person("john");
        EventSubprocessConditionalModel model = definition.createModel();
        model.setPerson(person);
        instance.updateVariables(model);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();

        ProcessTestHelper.completeWorkItem(instance, Collections.emptyMap(), "john");

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

        assertThat(trackerListener.tracked()).anyMatch(ProcessTestHelper.triggered("start"))
                .anyMatch(ProcessTestHelper.triggered("User Task 1"))
                .anyMatch(ProcessTestHelper.triggered("end"))
                .anyMatch(ProcessTestHelper.left("Sub Process 1"))
                .anyMatch(ProcessTestHelper.left("start-sub"))
                .anyMatch(ProcessTestHelper.triggered("Script Task 1"))
                .anyMatch(ProcessTestHelper.triggered("end-sub"));

        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessMessageWithLocalVars() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-EventSubProcessWithLocalVariables.bpmn2");
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
        Application app = ProcessTestHelper.newApplication();

        ProcessTestHelper.registerHandler(app, "Send Task", new SendTaskHandler());
        IntermediateThrowEventMessageProcess definition = (IntermediateThrowEventMessageProcess) IntermediateThrowEventMessageProcess
                .newProcess(app);
        StringBuilder builder = new StringBuilder();
        definition.setProducer__2(new MessageProducer<String>() {
            @Override
            public void produce(KogitoProcessInstance pi, String eventData) {
                builder.append(eventData);
            }
        });
        IntermediateThrowEventMessageModel model = definition.createModel();

        model.setX("MyValue");

        org.kie.kogito.process.ProcessInstance<IntermediateThrowEventMessageModel> instance = definition
                .createInstance(model);
        instance.start();

        assertThat(builder.toString()).isEqualTo("MyValue");
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testMessageBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryMessageEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryMessageEventOnTask");
        kruntime.signalEvent("Message-HelloMessage", "message data");
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "User Task", "Boundary event",
                "Condition met", "End2");

    }

    @Test
    public void testMessageBoundaryEventOnTaskComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryMessageEventOnTask.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryMessageEventOnTask");
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        kruntime.signalEvent("Message-HelloMessage", "message data");
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "User Task", "User Task2", "End1");

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
        assertThat(eventDescriptions).hasSize(2).extracting("event").contains("workItemCompleted",
                TIMER_TRIGGERED_EVENT);
        assertThat(eventDescriptions).extracting("eventType").contains("workItem", "timer");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions).filteredOn("eventType", "timer").hasSize(1).extracting("properties", Map.class)
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
    public void testTimerBoundaryEventCycle1() {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(2);
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new DoNothingWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);

        org.kie.kogito.process.Process<TimerBoundaryEventCycle1Model> processDefinition = TimerBoundaryEventCycle1Process.newProcess(app);
        TimerBoundaryEventCycle1Model model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<TimerBoundaryEventCycle1Model> instance = processDefinition.createInstance(model);
        instance.start();

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testTimerBoundaryEventCycle2() {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 3);
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new DoNothingWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);

        org.kie.kogito.process.Process<TimerBoundaryEventCycle2Model> processDefinition = TimerBoundaryEventCycle2Process.newProcess(app);
        TimerBoundaryEventCycle2Model model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<TimerBoundaryEventCycle2Model> instance = processDefinition.createInstance(model);
        instance.start();

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        instance.abort();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ABORTED);
    }

    @Test
    @RequirePersistence(false)
    public void testTimerBoundaryEventCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener(
                "TimerEvent", 2);
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
    public void testTimerBoundaryEventInterrupting() {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(2);
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new DoNothingWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);

        org.kie.kogito.process.Process<TimerBoundaryEventInterruptingModel> processDefinition = TimerBoundaryEventInterruptingProcess.newProcess(app);
        TimerBoundaryEventInterruptingModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<TimerBoundaryEventInterruptingModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        countDownListener.waitTillCompleted();
        logger.debug("Firing timer");
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTask() {
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(2); // Expecting 2 completion events
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new TestWorkItemHandler());
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);

        org.kie.kogito.process.Process<TimerBoundaryEventInterruptingOnTaskModel> processDefinition = TimerBoundaryEventInterruptingOnTaskProcess.newProcess(app);
        TimerBoundaryEventInterruptingOnTaskModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<TimerBoundaryEventInterruptingOnTaskModel> instance = processDefinition.createInstance(model);
        instance.start();

        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        logger.debug("Firing timer");
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTaskCancelTimer() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        org.kie.kogito.process.Process<TimerBoundaryEventInterruptingOnTaskCancelTimerModel> processDefinition = TimerBoundaryEventInterruptingOnTaskCancelTimerProcess.newProcess(app);
        TimerBoundaryEventInterruptingOnTaskCancelTimerModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<TimerBoundaryEventInterruptingOnTaskCancelTimerModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        KogitoWorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        instance.completeWorkItem(workItem.getStringId(), null);
        workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        instance.completeWorkItem(workItem.getStringId(), null);

        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignal.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignal");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("MyMessage", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testIntermediateCatchEventMessage() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<IntermediateCatchEventMessageModel> processDefinition = IntermediateCatchEventMessageProcess.newProcess(app);
        IntermediateCatchEventMessageModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventMessageModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("Message-HelloMessage", "SomeValue"));
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventMessageWithRef() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<IntermediateCatchEventMessageWithRefModel> processDefinition = IntermediateCatchEventMessageWithRefProcess.newProcess(app);
        IntermediateCatchEventMessageWithRefModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventMessageWithRefModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("Message-HelloMessage", "SomeValue"));
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventTimerDuration() {

        Application app = ProcessTestHelper.newApplication();

        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(1);
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Human Task", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<IntermediateCatchEventTimerDurationModel> processDefinition = IntermediateCatchEventTimerDurationProcess.newProcess(app);
        IntermediateCatchEventTimerDurationModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerDurationModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventTimerDateISO() {
        Application app = ProcessTestHelper.newApplication();
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Human Task", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<IntermediateCatchEventTimerDateISOModel> processDefinition = IntermediateCatchEventTimerDateISOProcess.newProcess(app);
        IntermediateCatchEventTimerDateISOModel model = processDefinition.createModel();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        model.setDate(plusTwoSeconds.toString());
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerDateISOModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);

        boolean completed = countDownListener.waitTillCompleted(3000);
        assertThat(completed).isTrue();

        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventTimerDurationISO() {
        Application app = ProcessTestHelper.newApplication();

        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener();
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Human Task", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<IntermediateCatchEventTimerDurationISOModel> processDefinition = IntermediateCatchEventTimerDurationISOProcess.newProcess(app);
        IntermediateCatchEventTimerDurationISOModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerDurationISOModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        boolean completed = countDownListener.waitTillCompleted(2000);
        assertThat(completed).isTrue();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventTimerCycle1() {
        Application app = ProcessTestHelper.newApplication();
        ProcessCompletedCountDownProcessEventListener countDownListener = new ProcessCompletedCountDownProcessEventListener(); // Expecting 1 completion event
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Human Task", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<IntermediateCatchEventTimerCycle1Model> processDefinition = IntermediateCatchEventTimerCycle1Process.newProcess(app);
        IntermediateCatchEventTimerCycle1Model model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerCycle1Model> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        boolean completed = countDownListener.waitTillCompleted(2000); // Wait for up to 2 seconds
        assertThat(completed).isTrue();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventTimerCycleISO() {
        Application app = ProcessTestHelper.newApplication();
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 5);
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Human Task", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<IntermediateCatchEventTimerCycleISOModel> processDefinition = IntermediateCatchEventTimerCycleISOProcess.newProcess(app);
        IntermediateCatchEventTimerCycleISOModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerCycleISOModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        boolean completed = countDownListener.waitTillCompleted();
        assertThat(completed).isTrue();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        instance.abort();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ABORTED);
    }

    @Test
    public void testIntermediateCatchEventTimerCycle2() {
        Application app = ProcessTestHelper.newApplication();
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        ProcessTestHelper.registerHandler(app, "Human Task", new DoNothingWorkItemHandler());

        org.kie.kogito.process.Process<IntermediateCatchEventTimerCycle2Model> processDefinition = IntermediateCatchEventTimerCycle2Process.newProcess(app);
        IntermediateCatchEventTimerCycle2Model model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerCycle2Model> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // now wait for 1 second for timer to trigger
        boolean completed = countDownListener.waitTillCompleted(); // Wait for up to 10 seconds
        assertThat(completed).isTrue();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ACTIVE);
        instance.abort();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_ABORTED);
    }

    @Test
    public void testIntermediateCatchEventCondition() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventCondition.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventCondition");
        assertProcessInstanceActive(processInstance);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        kruntime.getKieSession().insert(person);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testIntermediateCatchEventConditionFilterByProcessInstance() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventConditionFilterByProcessInstance.bpmn2");

        Map<String, Object> params1 = new HashMap<>();
        params1.put("personId", Long.valueOf(1L));
        Person person1 = new Person();
        person1.setId(1L);
        KogitoProcessInstance pi1 = kruntime
                .createProcessInstance("IntermediateCatchEventConditionFilterByProcessInstance", params1);
        String pi1id = pi1.getStringId();

        kruntime.getKieSession().insert(pi1);
        FactHandle personHandle1 = kruntime.getKieSession().insert(person1);

        kruntime.startProcessInstance(pi1.getStringId());

        Map<String, Object> params2 = new HashMap<>();
        params2.put("personId", Long.valueOf(2L));
        Person person2 = new Person();
        person2.setId(2L);

        KogitoProcessInstance pi2 = kruntime
                .createProcessInstance("IntermediateCatchEventConditionFilterByProcessInstance", params2);
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
    public void testIntermediateCatchEventTimerCycleWithError() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        Map<String, Object> params = new HashMap<>();
        params.put("x", 0);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycleWithError",
                params);
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

        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventTimerCycleWithError");
        assertProcessInstanceActive(processInstance);

        final String piId = processInstance.getStringId();
        kruntime.getKieSession().execute((ExecutableCommand<Void>) context -> {
            KogitoWorkflowProcessInstance processInstance1 = (KogitoWorkflowProcessInstance) kruntime
                    .getProcessInstance(piId);
            processInstance1.setVariable("x", 0);
            return null;
        });

        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        Integer xValue = kruntime.getKieSession().execute((ExecutableCommand<Integer>) context -> {
            KogitoWorkflowProcessInstance processInstance2 = (KogitoWorkflowProcessInstance) kruntime
                    .getProcessInstance(piId);
            return (Integer) processInstance2.getVariable("x");

        });
        assertThat(xValue).isEqualTo(2);
        kruntime.abortProcessInstance(processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testNoneIntermediateThrow() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<IntermediateThrowEventNoneModel> processDefinition = IntermediateThrowEventNoneProcess.newProcess(app);
        IntermediateThrowEventNoneModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateThrowEventNoneModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testLinkIntermediateEvent() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<IntermediateLinkEventModel> processDefinition = IntermediateLinkEventProcess.newProcess(app);
        IntermediateLinkEventModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<IntermediateLinkEventModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testLinkEventCompositeProcess() {
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<LinkEventCompositeProcessModel> processDefinition = LinkEventCompositeProcessProcess.newProcess(app);
        LinkEventCompositeProcessModel model = processDefinition.createModel();
        org.kie.kogito.process.ProcessInstance<LinkEventCompositeProcessModel> instance = processDefinition.createInstance(model);
        instance.start();
        assertThat(instance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testConditionalBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryConditionalEventOnTask");

        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "User Task", "Boundary event",
                "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryConditionalEventOnTask");

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        Person person = new Person();
        person.setName("john");
        // as the node that boundary event is attached to has been completed insert will
        // not have any effect
        kruntime.getKieSession().insert(person);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "User Task", "User Task2", "End1");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskActiveOnStartup() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler());

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryConditionalEventOnTask");
        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceCompleted(processInstance);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "User Task", "Boundary event",
                "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventInterrupting() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-ConditionalBoundaryEventInterrupting.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("ConditionalBoundaryEventInterrupting");
        assertProcessInstanceActive(processInstance);

        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "Hello", "StartSubProcess", "Task",
                "BoundaryEvent", "Goodbye", "EndProcess");

    }

    @Test
    public void testSignalBoundaryEventOnSubprocessTakingDifferentPaths() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/intermediate/BPMN2-SignalBoundaryOnSubProcess.bpmn");

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
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new SystemOutWorkItemHandler());

        org.kie.kogito.process.Process<IntermediateCatchEventSignalModel> definition1 = IntermediateCatchEventSignalProcess
                .newProcess(app);
        org.kie.kogito.process.Process<IntermediateCatchEventSignal2Model> definition2 = IntermediateCatchEventSignal2Process
                .newProcess(app);

        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventSignalModel> instance1 = definition1
                .createInstance(definition1.createModel());
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventSignal2Model> instance2 = definition2
                .createInstance(definition2.createModel());

        instance1.start();
        instance2.start();
        assertThat(instance1.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        assertThat(instance2.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance1.send(Sig.of("MyMessage", "SomeValue"));
        assertThat(instance1.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance2.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance2.send(Sig.of("MyMessage", "SomeValue"));
        assertThat(instance1.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance2.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

    }

    @Test
    @Disabled
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

        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceSubprocessWithBoundarySignal",
                params);
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

        KogitoProcessInstance processInstance = kruntime
                .startProcess("MultiInstanceSubprocessWithBoundarySignalNoInterupting", params);
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

        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceSubprocessWithBoundaryError",
                params);
        assertProcessInstanceActive(processInstance);
        Set<EventDescription<?>> eventDescriptions = processInstance.getEventDescriptions();
        assertThat(eventDescriptions).hasSize(3).extracting("event").contains("workItemCompleted", "Inside",
                "Error-_D83CFC28-3322-4ABC-A12D-83476B08C7E8-MyError");
        assertThat(eventDescriptions).extracting("eventType").contains("workItem", "signal");
        assertThat(eventDescriptions).extracting("processInstanceId").contains(processInstance.getStringId());
        assertThat(eventDescriptions).filteredOn("eventType", "signal").hasSize(2).extracting("properties", Map.class)
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
        kruntime = createKogitoProcessRuntime("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn",
                "BPMN2-IntermediateThrowEventSignalWithTransformation.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

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
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(application, "Human Task", handler);
        org.kie.kogito.process.Process<BoundarySignalEventOnTaskWithTransformationModel> processBoundary = BoundarySignalEventOnTaskWithTransformationProcess
                .newProcess(application);
        org.kie.kogito.process.Process<IntermediateThrowEventSignalModel> processIntermediate = IntermediateThrowEventSignalProcess
                .newProcess(application);

        ProcessInstance<BoundarySignalEventOnTaskWithTransformationModel> instanceBoundary = processBoundary
                .createInstance(processBoundary.createModel());
        instanceBoundary.start();
        IntermediateThrowEventSignalModel modelIntermediate = processIntermediate.createModel();
        modelIntermediate.setX("john");
        ProcessInstance<IntermediateThrowEventSignalModel> instanceIntermediate = processIntermediate
                .createInstance(modelIntermediate);
        instanceIntermediate.start();
        assertThat(instanceIntermediate).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(instanceBoundary).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(instanceBoundary.variables().getX()).isEqualTo("JOHN");
    }

    @Test

    public void testMessageIntermediateThrowWithTransformation() throws Exception {
        StringBuilder messageContent = new StringBuilder();
        Application application = ProcessTestHelper.newApplication();
        IntermediateThrowEventMessageWithTransformationProcess definition = (IntermediateThrowEventMessageWithTransformationProcess) IntermediateThrowEventMessageWithTransformationProcess
                .newProcess(application);
        definition.setProducer__2(new MessageProducer<String>() {
            @Override
            public void produce(KogitoProcessInstance pi, String eventData) {
                messageContent.append(eventData);
            }
        });
        IntermediateThrowEventMessageWithTransformationModel model = definition.createModel();
        model.setX("MyValue");
        ProcessInstance<IntermediateThrowEventMessageWithTransformationModel> instance = definition
                .createInstance(model);
        instance.start();

        assertThat(instance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(messageContent).hasToString("MYVALUE");

    }

    @Test
    public void testIntermediateCatchEventSignalWithTransformation() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        EventTrackerProcessListener listener = new EventTrackerProcessListener();
        ProcessTestHelper.registerProcessEventListener(app, listener);
        ProcessTestHelper.registerHandler(app, "Human Task", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<IntermediateCatchEventSignalWithTransformationModel> definition = IntermediateCatchEventSignalWithTransformationProcess
                .newProcess(app);

        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventSignalWithTransformationModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("MyMessage", "SomeValue"));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getX()).isNotNull().isEqualTo("SOMEVALUE");
        assertThat(listener.tracked()).anyMatch(ProcessTestHelper.triggered("StartProcess"))
                .anyMatch(ProcessTestHelper.triggered("UserTask")).anyMatch(ProcessTestHelper.triggered("EndProcess"))
                .anyMatch(ProcessTestHelper.triggered("event"));

    }

    @Test
    public void testIntermediateCatchEventTimerCycle3() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        final CountDownLatch latch = new CountDownLatch(3);
        ProcessTestHelper.registerProcessEventListener(app, new DefaultKogitoProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                latch.countDown();
            }
        });
        org.kie.kogito.process.Process<IntermediateCatchEventTimerCycle3Model> definition = IntermediateCatchEventTimerCycle3Process
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerCycle3Model> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        latch.await(5, TimeUnit.SECONDS);
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        instance.abort();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ABORTED);
    }

    @Test
    public void testIntermediateCatchEventTimerCycleCron() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        final CountDownLatch latch = new CountDownLatch(3);
        ProcessTestHelper.registerProcessEventListener(app, new DefaultKogitoProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                latch.countDown();
            }
        });
        org.kie.kogito.process.Process<IntermediateCatchEventTimerCycleCronModel> definition = IntermediateCatchEventTimerCycleCronProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventTimerCycleCronModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        latch.await(5, TimeUnit.SECONDS);
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchSignalBetweenUserTasksModel() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<IntermediateCatchSignalBetweenUserTasksModel> definition = IntermediateCatchSignalBetweenUserTasksProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<IntermediateCatchSignalBetweenUserTasksModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("MySignal", null));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntermediateCatchEventTimerDurationWithError() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<IntermediateCatchEventTimerDurationWithErrorModel> definition = IntermediateCatchEventTimerDurationWithErrorProcess
                .newProcess(app);
        IntermediateCatchEventTimerDurationWithErrorProcessInstance instance = (IntermediateCatchEventTimerDurationWithErrorProcessInstance) definition
                .createInstance(definition.createModel());
        instance.start();
        CompletionKogitoEventListener listener = ProcessTestHelper.registerCompletionEventListener(instance);
        listener.await();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getTestOK()).isEqualTo(Boolean.TRUE);

    }

    @Test
    public void testIntermediateCatchEventMessageWithTransformation() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Human Task", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<IntermediateCatchEventMessageWithTransformationModel> definition = IntermediateCatchEventMessageWithTransformationProcess
                .newProcess(app);

        org.kie.kogito.process.ProcessInstance<IntermediateCatchEventMessageWithTransformationModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        instance.send(Sig.of("Message-HelloMessage", "SomeValue"));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getX()).isNotNull().isEqualTo("SOMEVALUE");

    }

    @Test

    public void testEventSubprocessSignalWithTransformation() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);

        EventTrackerProcessListener trackerListener = new EventTrackerProcessListener();
        ProcessTestHelper.registerProcessEventListener(app, trackerListener);
        org.kie.kogito.process.Process<EventSubprocessSignalWithTransformationModel> definition = EventSubprocessSignalWithTransformationProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<EventSubprocessSignalWithTransformationModel> instance = definition
                .createInstance(definition.createModel());

        instance.start();

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        instance.send(Sig.of("MySignal", "john"));

        assertThat(trackerListener.tracked()).anyMatch(ProcessTestHelper.triggered("start"))
                .anyMatch(ProcessTestHelper.triggered("User Task 1"))
                .anyMatch(ProcessTestHelper.left("Sub Process 1"))
                .anyMatch(ProcessTestHelper.left("start-sub"))
                .anyMatch(ProcessTestHelper.triggered("end-sub"));

        assertThat(instance.variables().getX()).isNotNull().isEqualTo("JOHN");
    }

    @Test
    public void testMultipleMessageSignalSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-MultipleMessageSignalSubprocess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("MultipleMessageSignalSubprocess");
        logger.debug("Parent Process ID: " + processInstance.getStringId());

        kruntime.signalEvent("Message-Message 1", "Test", processInstance.getStringId());
        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        kruntime.signalEvent("Message-Message 1", "Test", processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testIntermediateCatchEventSignalWithRef() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignalWithRef.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignalWithRef");
        assertProcessInstanceActive(processInstance);

        // now signal process instance
        kruntime.signalEvent("Signal1", "SomeValue", processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testMultiInstanceLoopBoundaryTimer() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        ProcessTestHelper.registerProcessEventListener(app, countDownListener);
        TestUserTaskWorkItemHandler handler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<MultiInstanceLoopBoundaryTimerModel> definition = MultiInstanceLoopBoundaryTimerProcess.newProcess(app);
        org.kie.kogito.process.ProcessInstance<MultiInstanceLoopBoundaryTimerModel> instance = definition.createInstance(definition.createModel());
        instance.start();

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        countDownListener.waitTillCompleted();

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(3);

        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());
        ProcessTestHelper.completeWorkItem(instance, "mary", Collections.emptyMap());
        ProcessTestHelper.completeWorkItem(instance, "krisv", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcessSequential() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        TestUserTaskWorkItemHandler handler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<MultiInstanceLoopCharacteristicsProcessSequentialModel> definition = MultiInstanceLoopCharacteristicsProcessSequentialProcess.newProcess(app);
        MultiInstanceLoopCharacteristicsProcessSequentialModel model = definition.createModel();
        model.setList(List.of(1, 2, 3));
        org.kie.kogito.process.ProcessInstance<MultiInstanceLoopCharacteristicsProcessSequentialModel> instance = definition.createInstance(model);
        instance.start();

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        assertThat(handler.getWorkItems()).isNotNull().hasSize(1);
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(handler.getWorkItems()).isNotNull().hasSize(1);
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(handler.getWorkItems()).isNotNull().hasSize(1);
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcessWithOutputAndScripts() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<MultiInstanceLoopCharacteristicsProcessWithOutputAndScriptsModel> definition =
                MultiInstanceLoopCharacteristicsProcessWithOutputAndScriptsProcess.newProcess(app);
        MultiInstanceLoopCharacteristicsProcessWithOutputAndScriptsModel model = definition.createModel();
        model.setList(new ArrayList<>(List.of("1", "2", "3")));
        model.setScriptList(new ArrayList<String>());
        org.kie.kogito.process.ProcessInstance<MultiInstanceLoopCharacteristicsProcessWithOutputAndScriptsModel> instance = definition.createInstance(model);
        instance.start();

        assertThat(instance.variables().getListOut()).containsExactly("1 changed", "2 changed", "3 changed");

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsTask() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        TestUserTaskWorkItemHandler handler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<MultiInstanceLoopCharacteristicsTaskModel> definition = MultiInstanceLoopCharacteristicsTaskProcess.newProcess(app);
        MultiInstanceLoopCharacteristicsTaskModel model = definition.createModel();
        model.setList(new ArrayList<>(List.of("1", "2", "3")));

        org.kie.kogito.process.ProcessInstance<MultiInstanceLoopCharacteristicsTaskModel> instance = definition.createInstance(model);
        instance.start();

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(3);
        assertThat(workItems.get(0).getParameter("Item")).isEqualTo("1");
        assertThat(workItems.get(1).getParameter("Item")).isEqualTo("2");
        assertThat(workItems.get(2).getParameter("Item")).isEqualTo("3");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsTaskSequential() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        TestUserTaskWorkItemHandler handler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<MultiInstanceLoopCharacteristicsTaskSequentialModel> definition = MultiInstanceLoopCharacteristicsTaskSequentialProcess.newProcess(app);
        MultiInstanceLoopCharacteristicsTaskSequentialModel model = definition.createModel();
        model.setList(new ArrayList<>(List.of("1", "2", "3")));

        org.kie.kogito.process.ProcessInstance<MultiInstanceLoopCharacteristicsTaskSequentialModel> instance = definition.createInstance(model);
        instance.start();

        List<KogitoWorkItem> workItems = null;
        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(1);
        assertThat(workItems.get(0).getParameter("Item")).isEqualTo("1");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(1);
        assertThat(workItems.get(0).getParameter("Item")).isEqualTo("2");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(1);
        assertThat(workItems.get(0).getParameter("Item")).isEqualTo("3");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequential() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        TestUserTaskWorkItemHandler handler = new TestUserTaskWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        org.kie.kogito.process.Process<MultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequentialModel> definition =
                MultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequentialProcess.newProcess(app);
        MultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequentialModel model = definition.createModel();
        model.setList(new ArrayList<>(List.of("1", "2", "3")));
        model.setListOut(new ArrayList());

        org.kie.kogito.process.ProcessInstance<MultiInstanceLoopCharacteristicsTaskWithOutputCmpCondSequentialModel> instance = definition.createInstance(model);
        instance.start();

        List<KogitoWorkItem> workItems = null;
        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull().hasSize(1);
        assertThat(workItems.get(0).getParameter("Item")).isEqualTo("1");
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getListOut()).hasSize(1);
    }

    @Test
    public void testIntermediateTimerParallelGateway() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener1 = new NodeLeftCountDownProcessEventListener("Timer1",
                1);
        NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Timer2",
                1);
        NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Timer3",
                1);
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
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener(
                "After timer", 3);
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventExternalScope.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        KogitoWorkItemHandler externalHandler = new DefaultKogitoWorkItemHandler() {

            @Override
            public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
                String signal = (String) workItem.getParameter("Signal");
                kruntime.signalEvent(signal, null);
                return Optional.of(this.workItemLifeCycle.newTransition("complete", workItem.getPhaseStatus(), Collections.emptyMap()));
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("signalName", signalVar);
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignalWithVariable",
                parameters);
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
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEventSignalWithVariable",
                parameters);
        assertProcessInstanceActive(processInstance);

        Map<String, Object> params = new HashMap<>();
        params.put("x", "MyValue");
        params.put("signalName", signalVar);
        KogitoProcessInstance processInstanceThrow = kruntime.startProcess("IntermediateThrowEventSignalWithVariable",
                params);
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventConditionSetVariableAfter.bpmn2");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime
                .startProcess("IntermediateCatchEventConditionSetVariableAfter");
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventCondition.bpmn2");
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
            NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener(
                    "Request photos of order in use", 1);
            NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener(
                    "Request an online review", 1);
            NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener(
                    "Send a thank you card", 1);
            NodeLeftCountDownProcessEventListener countDownListener4 = new NodeLeftCountDownProcessEventListener(
                    "Request an online review", 1);
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
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<EventSubprocessErrorSignalEmbeddedModel> definition = EventSubprocessErrorSignalEmbeddedProcess
                .newProcess(app);
        org.kie.kogito.process.ProcessInstance<EventSubprocessErrorSignalEmbeddedModel> instance = definition
                .createInstance(definition.createModel());
        instance.start();
        instance.send(Sig.of("signal1", null));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("signal2", null));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
        instance.send(Sig.of("signal3", null));
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ABORTED);
    }

    @Test
    public void testEventSubprocessWithExpression() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-EventSubprocessSignalExpression.bpmn2");
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventConditionPI.bpmn2",
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateCatchEventSignal.bpmn2");
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
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-SubprocessWithSignalEndEventAndSignalBoundaryEvent.bpmn2");
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);

        Map<String, Object> params = new HashMap<>();
        params.put("document-ref", "signalling");
        params.put("message", "hello");
        KogitoProcessInstance processInstance = kruntime
                .startProcess("SubprocessWithSignalEndEventAndSignalBoundaryEvent", params);

        assertNodeTriggered(processInstance.getStringId(), "sysout from boundary", "end2");
        assertNotNodeTriggered(processInstance.getStringId(), "end1");

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testSignalEndWithData() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/intermediate/BPMN2-IntermediateThrowEventSignalWithData.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateThrowEventSignalWithData", params);

        assertProcessInstanceActive(processInstance);

        kruntime.signalEvent("mysignal", null, processInstance.getStringId());

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testDynamicCatchEventSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/dynamic-signal-parent.bpmn2",
                "subprocess/dynamic-signal-child.bpmn2");
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
        kruntime = createKogitoProcessRuntime("subprocess/dynamic-signal-parent.bpmn2",
                "subprocess/dynamic-signal-child.bpmn2");
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

        // change one child process instance variable (fatherId) to something else then
        // original fatherId
        String changeProcessInstanceId = instances.remove(0);
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("fatherId", "999");
        kruntime.getKieSession()
                .execute(new KogitoSetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));

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
        kruntime = createKogitoProcessRuntime("subprocess/dynamic-signal-parent.bpmn2",
                "subprocess/dynamic-signal-child.bpmn2");
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

        // change one child process instance variable (fatherId) to something else then
        // original fatherId
        String changeProcessInstanceId = instances.remove(0);
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("fatherId", "999");
        kruntime.getKieSession()
                .execute(new KogitoSetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));

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
