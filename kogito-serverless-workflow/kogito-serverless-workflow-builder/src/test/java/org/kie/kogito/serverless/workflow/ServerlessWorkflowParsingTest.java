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
package org.kie.kogito.serverless.workflow;

import java.util.Collection;
import java.util.List;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState.Type;
import io.serverlessworkflow.api.states.SleepState;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-operation.sw.json", "/exec/single-operation.sw.yml" })
    public void testSingleOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(3);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-operation-with-delay.sw.json", "/exec/single-operation-with-delay.sw.yml" })
    public void testSingleOperationWithDelayWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(4);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(TimerNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);

        TimerNode timerNode = (TimerNode) process.getNodes()[3];
        assertThat(timerNode.getTimer().getDelay()).isEqualTo("PT1S");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-service-operation.sw.json", "/exec/single-service-operation.sw.yml" })
    public void testSingleServiceOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(3);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(WorkItemNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);

        WorkItemNode workItemNode = (WorkItemNode) compositeNode.getNodes()[1];
        assertThat(workItemNode.getName()).isEqualTo("helloWorld");
        assertThat(workItemNode.getWork().getParameter("Interface")).isEqualTo("org.something.other.TestService");
        assertThat(workItemNode.getWork().getParameter("Operation")).isEqualTo("get");
        assertThat(workItemNode.getWork().getParameter("interfaceImplementationRef")).isEqualTo("org.something.other.TestService");
        assertThat(workItemNode.getWork().getParameter("operationImplementationRef")).isEqualTo("get");
        assertThat(workItemNode.getWork().getParameter("implementation")).isEqualTo("Java");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-eventstate.sw.json", "/exec/single-eventstate.sw.yml" })
    public void testSingleEventStateWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(4);

        Node node = process.getNodes()[1];
        assertThat(((StartNode) node).getMetaData(Metadata.TRIGGER_REF)).isEqualTo("kafka");
        node = process.getNodes()[0];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(CompositeContextNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[3];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-eventstate-multi-eventrefs.sw.json", "/exec/single-eventstate-multi-eventrefs.sw.yml" })
    public void testSingleEventStateMultiEventRefsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(7);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[6];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[5];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(Join.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(ActionNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[6];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-operation-many-functions.sw.json", "/exec/single-operation-many-functions.sw.yml" })
    public void testSingleOperationWithManyFunctionsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(3);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertThat(compositeNode.getNodes()).hasSize(6);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[4];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[5];
        assertThat(node).isInstanceOf(EndNode.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/multiple-operations.sw.json", "/exec/multiple-operations.sw.yml" })
    public void testMultipleOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(5);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[4];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);

        compositeNode = (CompositeContextNode) process.getNodes()[3];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);

        compositeNode = (CompositeContextNode) process.getNodes()[4];

        assertThat(compositeNode.getNodes()).hasSize(4);

        node = compositeNode.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = compositeNode.getNodes()[1];
        assertThat(node).isInstanceOf(Node.class);
        node = compositeNode.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = compositeNode.getNodes()[3];
        assertThat(node).isInstanceOf(EndNode.class);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-inject-state.sw.json", "/exec/single-inject-state.sw.yml" })
    public void testSingleInjectWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(3);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        ActionNode actionNode = (ActionNode) process.getNodes()[2];
        assertThat(actionNode.getName()).isEqualTo("SimpleInject");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/parallel-state.sw.json", "/exec/parallel-state.sw.yml" })
    public void testParallelWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("parallelworkflow");
        assertThat(process.getName()).isEqualTo("parallel-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(6);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(Split.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(Join.class);
        node = process.getNodes()[4];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[5];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/transition-produce-event.sw.json", "/exec/transition-produce-event.sw.yml" })
    public void testProduceEventOnTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("produceeventontransition");
        assertThat(process.getName()).isEqualTo("Produce Event On Transition");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(5);
        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[4];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);

        ActionNode actionNode = (ActionNode) process.getNodes()[4];
        assertThat(actionNode.getName()).isEqualTo("TestKafkaEvent");
        assertThat(actionNode.getMetaData("TriggerType")).isEqualTo("ProduceMessage");
        assertThat(actionNode.getMetaData("MappingVariableInput")).isEqualTo("workflowdata");
        assertThat(actionNode.getMetaData("TriggerRef")).isEqualTo("kafka");
        assertThat(actionNode.getMetaData("MessageType")).isEqualTo("com.fasterxml.jackson.databind.JsonNode");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/eventbased-switch-state.sw.json", "/exec/eventbased-switch-state.sw.yml" })
    public void testEventBasedSwitchWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("eventswitchworkflow");
        assertThat(process.getName()).isEqualTo("event-switch-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(15);

        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[4];
        assertThat(node).isInstanceOf(Split.class);
        node = process.getNodes()[5];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[6];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[7];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[8];
        assertThat(node).isInstanceOf(EventNode.class);
        node = process.getNodes()[10];
        assertThat(node).isInstanceOf(EventNode.class);

        Split split = (Split) process.getNodes()[4];
        assertThat(split.getName()).isEqualTo("ChooseOnEvent");
        assertThat(split.getType()).isEqualTo(Split.TYPE_XAND);

        EventNode firstEventNode = (EventNode) process.getNodes()[8];
        assertThat(firstEventNode.getName()).isEqualTo("visaApprovedEvent");

        EventNode secondEventNode = (EventNode) process.getNodes()[10];
        assertThat(secondEventNode.getName()).isEqualTo("visaDeniedEvent");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/prchecker.sw.json", "/exec/prchecker.sw.yml" })
    public void testPrCheckerWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("prchecker");
        assertThat(process.getName()).isEqualTo("Github PR Checker Workflow");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(13);

        Node node = process.getNodes()[5];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[4];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[0];
        assertThat(node).isInstanceOf(Join.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[6];
        assertThat(node).isInstanceOf(Split.class);
        node = process.getNodes()[7];
        assertThat(node).isInstanceOf(Split.class);
        node = process.getNodes()[8];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[9];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[11];
        assertThat(node).isInstanceOf(EndNode.class);

        Split split = (Split) process.getNodes()[6];
        assertThat(split.getName()).isEqualTo("CheckBackend");
        assertThat(split.getType()).isEqualTo(2);
        assertThat(split.getConstraints()).hasSize(2);

        Split split2 = (Split) process.getNodes()[7];
        assertThat(split2.getName()).isEqualTo("CheckFrontend");
        assertThat(split2.getType()).isEqualTo(2);
        assertThat(split2.getConstraints()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/transition-produce-multi-events.sw.json", "/exec/transition-produce-multi-events.sw.yml" })
    public void testProduceMultiEventsOnTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("produceeventontransition");
        assertThat(process.getName()).isEqualTo("Produce Event On Transition");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(8);
        Node node = process.getNodes()[0];
        assertThat(node).isInstanceOf(StartNode.class);
        node = process.getNodes()[1];
        assertThat(node).isInstanceOf(EndNode.class);
        node = process.getNodes()[2];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[3];
        assertThat(node).isInstanceOf(CompositeContextNode.class);
        node = process.getNodes()[4];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[5];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[6];
        assertThat(node).isInstanceOf(ActionNode.class);
        node = process.getNodes()[7];
        assertThat(node).isInstanceOf(ActionNode.class);

        ActionNode actionNode = (ActionNode) process.getNodes()[4];
        assertThat(actionNode.getName()).isEqualTo("TestKafkaEvent");

        ActionNode actionNode2 = (ActionNode) process.getNodes()[5];
        assertThat(actionNode2.getName()).isEqualTo("TestKafkaEvent2");

        ActionNode actionNode3 = (ActionNode) process.getNodes()[6];
        assertThat(actionNode3.getName()).isEqualTo("TestKafkaEvent3");

        ActionNode actionNode4 = (ActionNode) process.getNodes()[7];
        assertThat(actionNode4.getName()).isEqualTo("TestKafkaEvent4");
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-produce-events.sw.json", "/exec/switch-state-produce-events.sw.yml" })
    public void testSwitchProduceEventsOnTransitionWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("switchworkflow");
        assertThat(process.getName()).isEqualTo("switch-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(16);

        Split split = (Split) process.getNodes()[4];
        assertThat(split.getName()).isEqualTo("ChooseOnAge");
        assertThat(split.getType()).isEqualTo(2);
        assertThat(split.getConstraints()).hasSize(2);

        assertHaveDefaultConstraint(split);
    }

    @ParameterizedTest
    @ValueSource(strings = "/exec/switch-state-produce-events-default.sw.json")
    public void testSwitchProduceEventsDefaultOnTransitionWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("switchworkflow");
        assertThat(process.getName()).isEqualTo("switch-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(17);

        Split split = (Split) process.getNodes()[4];
        assertThat(split.getName()).isEqualTo("ChooseOnAge");
        assertThat(split.getType()).isEqualTo(2);
        assertThat(split.getConstraints()).hasSize(2);

        assertHaveDefaultConstraint(split);
    }

    private void assertHaveDefaultConstraint(Split split) {
        assertThat(split.getConstraints().values().stream().flatMap(Collection::stream).anyMatch(Constraint::isDefault)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "/examples/applicantworkflow.sw.json", "/exec/error.sw.json", "/exec/callback.sw.json", "/exec/compensation.sw.json", "/exec/compensation.end.sw.json",
            "/exec/foreach.sw.json" })
    public void testSpecExamplesParsing(String workflowLocation) throws Exception {
        Workflow workflow = Workflow.fromSource(WorkflowTestUtils.readWorkflowFile(workflowLocation));

        assertThat(workflow).isNotNull();
        assertThat(workflow.getId()).isNotNull();
        assertThat(workflow.getName()).isNotNull();
        assertThat(workflow.getStates()).isNotNull().hasSizeGreaterThan(0);

        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process).isNotNull();
        assertThat(process.getId()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/expression.schema.sw.json" })
    public void testSpecWithInputSchema(String workflowLocation) throws Exception {
        Workflow workflow = Workflow.fromSource(WorkflowTestUtils.readWorkflowFile(workflowLocation));

        assertThat(workflow).isNotNull();
        assertThat(workflow.getDataInputSchema()).isNotNull();
        assertThat(workflow.getStates()).hasSizeGreaterThan(0);

        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process).isNotNull();
        assertThat(process.getId()).isNotNull();
    }

    @Test
    public void testMinimumWorkflow() {
        Workflow workflow = createMinimumWorkflow();
        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();
        assertThat(parser.getProcessInfo().info()).isSameAs(process);
        assertThat(process.getName()).isEqualTo(ServerlessWorkflowParser.DEFAULT_NAME);
        assertThat(process.getVersion()).isEqualTo(ServerlessWorkflowParser.DEFAULT_VERSION);
        assertThat(process.getPackageName()).isEqualTo(ServerlessWorkflowParser.DEFAULT_PACKAGE);
    }

    private static Workflow createMinimumWorkflow() {
        Workflow workflow = new Workflow();
        workflow.setId("javierito");
        Start start = new Start();
        start.setStateName("javierito");
        End end = new End();
        end.setTerminate(true);
        SleepState startState = new SleepState();
        startState.setType(Type.SLEEP);
        startState.setDuration("1s");
        startState.setName("javierito");
        startState.setEnd(end);
        workflow.setStates(List.of(startState));
        workflow.setStart(start);

        return workflow;
    }

    @Test
    void testWorkflowWithAnnotations() {
        List<String> annotations = List.of("machine learning", "monitoring", "networking");

        Workflow workflow = createMinimumWorkflow().withAnnotations(annotations);

        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();

        assertThat(process.getMetaData()).containsEntry(Metadata.TAGS, annotations);
    }

    @Test
    void workflowWithoutAnnotationsShouldResultInProcessWithoutTags() {
        Workflow workflow = createMinimumWorkflow();

        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();

        assertThat(process.getMetaData()).doesNotContainKey(Metadata.TAGS);
    }

    @Test
    void testWorkflowWithDescription() {
        String description = "This is a description";

        String workflowId = "my-workflow";

        Workflow workflow = createMinimumWorkflow()
                .withId(workflowId)
                .withDescription(description);

        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();

        assertThat(process.getMetaData()).containsEntry(Metadata.DESCRIPTION, description);
    }
}
