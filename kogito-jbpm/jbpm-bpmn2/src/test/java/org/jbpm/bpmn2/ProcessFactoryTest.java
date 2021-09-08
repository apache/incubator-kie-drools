/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.List;

import org.jbpm.bpmn2.objects.ExceptionOnPurposeHandler;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.process.instance.LightProcessRuntime;
import org.jbpm.process.instance.LightProcessRuntimeServiceProvider;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.io.Resource;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.ruleflow.core.Metadata.CANCEL_ACTIVITY;
import static org.jbpm.ruleflow.core.Metadata.ERROR_EVENT;
import static org.jbpm.ruleflow.core.Metadata.ERROR_STRUCTURE_REF;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_TIMER;
import static org.jbpm.ruleflow.core.Metadata.HAS_ERROR_EVENT;
import static org.jbpm.ruleflow.core.Metadata.TIME_CYCLE;
import static org.jbpm.ruleflow.core.Metadata.TIME_DURATION;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessFactoryTest extends JbpmBpmn2TestCase {

    @Test
    public void testProcessFactory() throws Exception {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                // header
                .name("My process")
                .packageName("org.jbpm")
                // nodes
                .startNode(1)
                .name("Start")
                .done()
                .actionNode(2)
                .name("Action")
                .action("java",
                        "System.out.println(\"Action\");")
                .done()
                .endNode(3)
                .name("End")
                .done()
                // connections
                .connection(1, 2)
                .connection(2, 3);
        RuleFlowProcess process = factory.validate().getProcess();
        Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        res.setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
        kruntime = createKogitoProcessRuntime(res);
        kruntime.startProcess("org.jbpm.process");
    }

    @Test
    public void testCompositeNode() throws Exception {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                // header
                .name("My process")
                .packageName("org.jbpm")
                // nodes
                .startNode(1)
                .name("Start")
                .done()
                .compositeContextNode(2)
                .name("SubProcess")
                .startNode(1)
                .name("SubProcess Start")
                .done()
                .actionNode(2)
                .name("SubProcess Action")
                .action("java",
                        "System.out.println(\"SubProcess Action\");")
                .done()
                .endNode(3)
                .name("SubProcess End")
                .terminate(true)
                .done()
                .connection(1, 2)
                .connection(2, 3)
                .done()
                .endNode(3)
                .name("End")
                .done()
                // connections
                .connection(1, 2)
                .connection(2, 3);
        RuleFlowProcess process = factory.validate().getProcess();

        assertEquals("SubProcess",
                process.getNode(2).getName());

        Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        res.setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
        kruntime = createKogitoProcessRuntime(res);
        KogitoProcessInstance pi = kruntime.startProcess("org.jbpm.process");

        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                pi.getState());

    }

    @Test
    @Timeout(10)
    public void testBoundaryTimerTimeCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("BoundaryTimerEvent",
                1);
        String timeCycle = "1s###5s";
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                // header
                .name("My process")
                .packageName("org.jbpm")
                // nodes
                .startNode(1)
                .name("Start")
                .done()
                .humanTaskNode(2)
                .name("Task")
                .actorId("john")
                .taskName("MyTask")
                .done()
                .endNode(3)
                .name("End1")
                .terminate(false)
                .done()
                .boundaryEventNode(4)
                .name("BoundaryTimerEvent")
                .attachedTo(2)
                .metaData(TIME_CYCLE, timeCycle)
                .metaData(CANCEL_ACTIVITY, false)
                .eventType(EVENT_TYPE_TIMER, timeCycle)
                .done()
                .endNode(5)
                .name("End2")
                .terminate(false)
                .done()
                // connections
                .connection(1, 2)
                .connection(2, 3)
                .connection(4, 5);
        RuleFlowProcess process = factory.validate().getProcess();

        Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        res.setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
        kruntime = createKogitoProcessRuntime(res);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                testHandler);
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance pi = kruntime.startProcess("org.jbpm.process");
        assertProcessInstanceActive(pi);

        countDownListener.waitTillCompleted(); // wait for boundary timer firing

        assertNodeTriggered(pi.getStringId(),
                "End2");
        assertProcessInstanceActive(pi); // still active because CancelActivity = false

        kruntime.getKogitoWorkItemManager().completeWorkItem(testHandler.getWorkItem().getStringId(),
                null);
        assertProcessInstanceCompleted(pi);

    }

    @Test
    @Timeout(10)
    public void testBoundaryTimerTimeDuration() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("BoundaryTimerEvent",
                1);
        String timeDuration = "1s";
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                // header
                .name("My process")
                .packageName("org.jbpm")
                // nodes
                .startNode(1)
                .name("Start")
                .done()
                .humanTaskNode(2)
                .name("Task")
                .actorId("john")
                .taskName("MyTask")
                .done()
                .endNode(3)
                .name("End1")
                .terminate(false)
                .done()
                .boundaryEventNode(4)
                .name("BoundaryTimerEvent")
                .attachedTo(2)
                .metaData(TIME_DURATION, timeDuration)
                .metaData(CANCEL_ACTIVITY, false)
                .eventType(EVENT_TYPE_TIMER, timeDuration)
                .done()
                .endNode(5)
                .name("End2")
                .terminate(false)
                .done()
                // connections
                .connection(1, 2)
                .connection(2, 3)
                .connection(4, 5);
        RuleFlowProcess process = factory.validate().getProcess();

        Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        res.setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
        kruntime = createKogitoProcessRuntime(res);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                testHandler);
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        KogitoProcessInstance pi = kruntime.startProcess("org.jbpm.process");
        assertProcessInstanceActive(pi);

        countDownListener.waitTillCompleted(); // wait for boundary timer firing

        assertNodeTriggered(pi.getStringId(),
                "End2");
        assertProcessInstanceActive(pi); // still active because CancelActivity = false

        kruntime.getKogitoWorkItemManager().completeWorkItem(testHandler.getWorkItem().getStringId(),
                null);
        assertProcessInstanceCompleted(pi);

    }

    @Test
    @Timeout(10)
    public void testAdHocSimple() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                .dynamic(true)
                .name("Event Process")
                .version("1")
                .packageName("org.jbpm");
        RuleFlowProcess process = factory.validate().getProcess();
        assertNotNull(process);
        assertTrue(process.isDynamic());
    }

    @Test
    @Timeout(10)
    public void testSignalEvent() throws Exception {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                .name("Event Process")
                .version("1")
                .packageName("org.jbpm")
                .variable("eventData",
                        new org.jbpm.process.core.datatype.impl.type.StringDataType())
                .startNode(1)
                .name("Start")
                .done()
                .eventNode(2)
                .name("Event1")
                .eventType("testEvent")
                .variableName("eventData")
                .done()
                .actionNode(3)
                .name("simpleActionNode")
                .action("java",
                        "System.out.println(\"test event action\");")
                .done()
                .endNode(4)
                .name("End")
                .done()
                .connection(1, 2)
                .connection(2, 3)
                .connection(3, 4);
        RuleFlowProcess process = factory.validate().getProcess();

        assertNotNull(process);

        Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        res.setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
        kruntime = createKogitoProcessRuntime(res);

        KogitoProcessInstance pi = kruntime.startProcess("org.jbpm.process");

        assertNotNull(pi);

        assertEquals(KogitoProcessInstance.STATE_ACTIVE,
                pi.getState());

        pi.signalEvent("testEvent",
                null);

        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                pi.getState());

    }

    @Test
    @Timeout(10)
    public void testActionNodeIsDroolsAction() throws Exception {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.process");
        factory
                .name("ActionNodeActionProcess")
                .version("1")
                .startNode(1)
                .name("Start")
                .done()
                .endNode(3)
                .name("End")
                .done()
                .actionNode(2)
                .name("printTextActionNode")
                .action("java",
                        "System.out.println(\"test print\");",
                        true)
                .done()
                .connection(1, 2)
                .connection(2, 3);
        RuleFlowProcess process = factory.validate().getProcess();

        assertNotNull(process);

        Resource res = ResourceFactory.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
        res.setSourcePath("/tmp/processFactory.bpmn2");
        kruntime = createKogitoProcessRuntime(res);

        KogitoProcessInstance pi = kruntime.startProcess("org.jbpm.process");

        assertNotNull(pi);

        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                pi.getState());

    }

    @Test
    public void testBoundaryErrorEvent() throws Exception {
        final String boundaryErrorEvent = "BoundaryErrorEvent";
        final String errorCode = "java.lang.RuntimeException";
        final String processId = "myProcess";
        final RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(processId);
        final String startNode = "Start";
        final String task = "Task";
        final String endOnError = "EndOnError";
        factory
                // header
                .name("My process")
                .packageName("org.kie.kogito")
                // nodes
                .startNode(1)
                .name(startNode)
                .metaData(UNIQUE_ID, startNode)
                .done()
                .workItemNode(2)
                .name(task)
                .workName(task)
                .done()
                .endNode(3)
                .name("EndOnSuccess")
                .done()
                .boundaryEventNode(4)
                .name(boundaryErrorEvent)
                .attachedTo(2)
                .metaData(ERROR_EVENT, errorCode)
                .metaData(HAS_ERROR_EVENT, true)
                .metaData(ERROR_STRUCTURE_REF, null)
                .metaData("EventTpe", "error")
                .metaData(UNIQUE_ID, boundaryErrorEvent)
                .eventType("Error", errorCode)
                .done()
                .endNode(5)
                .name(endOnError)
                .metaData(UNIQUE_ID, endOnError)
                .terminate(true)
                .done()
                // connections
                .connection(1, 2)
                .connection(2, 3)
                .connection(4, 5);

        final RuleFlowProcess process = factory.validate().getProcess();

        final LightProcessRuntime processRuntime = LightProcessRuntime.of(null, Collections.singletonList(process), new LightProcessRuntimeServiceProvider());

        processRuntime.getKogitoWorkItemManager().registerWorkItemHandler(task, new ExceptionOnPurposeHandler());

        final List<String> completedNodes = new ArrayList<>();
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                completedNodes.add(event.getNodeInstance().getNodeName());
                super.afterNodeLeft(event);
            }
        };
        processRuntime.addEventListener(listener);

        ProcessInstance processInstance = processRuntime.startProcess(processId);

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(completedNodes).contains(startNode, task, boundaryErrorEvent, endOnError);
    }
}
