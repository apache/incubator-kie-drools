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
package org.jbpm.event.process;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessEventSupportTest extends AbstractBaseTest {

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testProcessEventListener() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.event");
        process.setName("Event Process");

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);

        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", (Action) context -> logger.info("Executed action"));
        actionNode.setAction(action);
        actionNode.setId(2);
        process.addNode(actionNode);
        new ConnectionImpl(
                startNode, Node.CONNECTION_DEFAULT_TYPE,
                actionNode, Node.CONNECTION_DEFAULT_TYPE);

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        process.addNode(endNode);
        new ConnectionImpl(
                actionNode, Node.CONNECTION_DEFAULT_TYPE,
                endNode, Node.CONNECTION_DEFAULT_TYPE);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);

        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {

            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

        };
        kruntime.getProcessEventManager().addEventListener(processEventListener);

        // execute the process
        kruntime.startProcess("org.drools.core.process.event");
        assertEquals(16, processEventList.size());
        assertEquals("org.drools.core.process.event", processEventList.get(0).getProcessInstance().getProcessId());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(1)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(2)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(3)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(4)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(5)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(6)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(7).getProcessInstance().getProcessId());
        assertEquals("org.drools.core.process.event", processEventList.get(8).getProcessInstance().getProcessId());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(9)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(10)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(11)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(12)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(13)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(14)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(15).getProcessInstance().getProcessId());
    }

    @Test
    public void testProcessEventListenerProcessState() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.event");
        process.setName("Event Process");

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);

        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", (Action) context -> logger.info("Executed action"));
        actionNode.setAction(action);
        actionNode.setId(2);
        process.addNode(actionNode);
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        process.addNode(endNode);
        new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);

        final List<Integer> processEventStatusList = new ArrayList<Integer>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {

            public void afterNodeLeft(ProcessNodeLeftEvent event) {
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                processEventStatusList.add(event.getProcessInstance().getState());
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                processEventStatusList.add(event.getProcessInstance().getState());
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
            }

        };
        kruntime.getProcessEventManager().addEventListener(processEventListener);

        // execute the process
        kruntime.startProcess("org.drools.core.process.event");
        assertEquals(2, processEventStatusList.size());
        assertEquals(Integer.valueOf(KogitoProcessInstance.STATE_ACTIVE), processEventStatusList.get(0));
        assertEquals(Integer.valueOf(KogitoProcessInstance.STATE_COMPLETED), processEventStatusList.get(1));
    }

    @Test
    public void testProcessEventListenerWithEvent() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.event");
        process.setName("Event Process");

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);

        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", (Action) context -> logger.info("Executed action"));
        actionNode.setAction(action);
        actionNode.setId(2);
        process.addNode(actionNode);
        new ConnectionImpl(
                startNode, Node.CONNECTION_DEFAULT_TYPE,
                actionNode, Node.CONNECTION_DEFAULT_TYPE);

        EventNode eventNode = new EventNode();
        eventNode.setName("Event");
        eventNode.setId(3);

        List<EventFilter> filters = new ArrayList<EventFilter>();
        EventTypeFilter filter = new EventTypeFilter();
        filter.setType("signal");
        filters.add(filter);
        eventNode.setEventFilters(filters);
        process.addNode(eventNode);
        new ConnectionImpl(
                actionNode, Node.CONNECTION_DEFAULT_TYPE,
                eventNode, Node.CONNECTION_DEFAULT_TYPE);

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(4);
        process.addNode(endNode);
        new ConnectionImpl(
                eventNode, Node.CONNECTION_DEFAULT_TYPE,
                endNode, Node.CONNECTION_DEFAULT_TYPE);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {

            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

        };
        kruntime.getProcessEventManager().addEventListener(processEventListener);

        // execute the process
        KogitoProcessInstance pi = kruntime.startProcess("org.drools.core.process.event");
        pi.signalEvent("signal", null);
        assertEquals(20, processEventList.size());
        assertEquals("org.drools.core.process.event", processEventList.get(0).getProcessInstance().getProcessId());

        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(1)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(2)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(3)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(4)).getNodeInstance().getNodeName());
        assertEquals("Event", ((ProcessNodeTriggeredEvent) processEventList.get(5)).getNodeInstance().getNodeName());
        assertEquals("Event", ((ProcessNodeTriggeredEvent) processEventList.get(6)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(7)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(8)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(9)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(10)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(11).getProcessInstance().getProcessId());
        assertEquals("Event", ((ProcessNodeLeftEvent) processEventList.get(12)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(13)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(14)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(15).getProcessInstance().getProcessId());
        assertEquals("org.drools.core.process.event", processEventList.get(16).getProcessInstance().getProcessId());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(17)).getNodeInstance().getNodeName());
        assertEquals("Event", ((ProcessNodeLeftEvent) processEventList.get(19)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(18)).getNodeInstance().getNodeName());
    }

    @Test
    public void testProcessEventListenerWithEndEvent() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.event");
        process.setName("Event Process");

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);

        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", (Action) context -> logger.info("Executed action"));
        actionNode.setAction(action);
        actionNode.setId(2);
        process.addNode(actionNode);
        new ConnectionImpl(
                startNode, Node.CONNECTION_DEFAULT_TYPE,
                actionNode, Node.CONNECTION_DEFAULT_TYPE);

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        endNode.setTerminate(false);
        process.addNode(endNode);
        new ConnectionImpl(
                actionNode, Node.CONNECTION_DEFAULT_TYPE,
                endNode, Node.CONNECTION_DEFAULT_TYPE);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {

            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

        };
        kruntime.getProcessEventManager().addEventListener(processEventListener);

        // execute the process
        kruntime.startProcess("org.drools.core.process.event");
        assertEquals(14, processEventList.size());
        assertEquals("org.drools.core.process.event", processEventList.get(0).getProcessInstance().getProcessId());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(1)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(2)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(3)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(4)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(5)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(6)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(7)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(8)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(9)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(10)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(11)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(12)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(13).getProcessInstance().getProcessId());
    }

    @Test
    public void testProcessEventListenerWithStartEvent() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.event");
        process.setName("Event Process");

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        EventTrigger trigger = new EventTrigger();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType("signal");
        trigger.addEventFilter(eventFilter);
        startNode.addTrigger(trigger);
        process.addNode(startNode);

        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", (Action) context -> logger.info("Executed action"));
        actionNode.setAction(action);
        actionNode.setId(2);
        process.addNode(actionNode);
        new ConnectionImpl(
                startNode, Node.CONNECTION_DEFAULT_TYPE,
                actionNode, Node.CONNECTION_DEFAULT_TYPE);

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        process.addNode(endNode);
        new ConnectionImpl(
                actionNode, Node.CONNECTION_DEFAULT_TYPE,
                endNode, Node.CONNECTION_DEFAULT_TYPE);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {

            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void afterProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
            }

            public void beforeProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
            }

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

        };
        kruntime.getProcessEventManager().addEventListener(processEventListener);

        kruntime.signalEvent("signal", null);
        assertEquals(16, processEventList.size());
        assertEquals("org.drools.core.process.event", processEventList.get(0).getProcessInstance().getProcessId());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(1)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(2)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(3)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(4)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(5)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(6)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(7).getProcessInstance().getProcessId());
        assertEquals("org.drools.core.process.event", processEventList.get(8).getProcessInstance().getProcessId());
        assertEquals("End", ((ProcessNodeLeftEvent) processEventList.get(9)).getNodeInstance().getNodeName());
        assertEquals("End", ((ProcessNodeTriggeredEvent) processEventList.get(10)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeLeftEvent) processEventList.get(11)).getNodeInstance().getNodeName());
        assertEquals("Print", ((ProcessNodeTriggeredEvent) processEventList.get(12)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeLeftEvent) processEventList.get(13)).getNodeInstance().getNodeName());
        assertEquals("Start", ((ProcessNodeTriggeredEvent) processEventList.get(14)).getNodeInstance().getNodeName());
        assertEquals("org.drools.core.process.event", processEventList.get(15).getProcessInstance().getProcessId());
    }

}
