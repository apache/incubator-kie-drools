/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.serverless.workflow;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.utils.WorkflowTestUtils;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.node.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.definition.process.Node;

import static org.junit.jupiter.api.Assertions.*;

public class ServerlessWorkflowParsingTest extends BaseServerlessTest {

    @BeforeAll
    public static void init() {
        System.setProperty("jbpm.enable.multi.con", "true");
    }

    @AfterAll
    public static void cleanup() {
        System.clearProperty("jbpm.enable.multi.con");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-operation.sw.json", "/exec/single-operation.sw.yml"})
    public void testSingleOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-operation-with-delay.sw.json", "/exec/single-operation-with-delay.sw.yml"})
    public void testSingleOperationWithDelayWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(4, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof TimerNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

        TimerNode timerNode = (TimerNode) process.getNodes()[3];
        assertEquals("PT1S", timerNode.getTimer().getDelay());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-service-operation.sw.json", "/exec/single-service-operation.sw.yml"})
    public void testSingleServiceOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof WorkItemNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

        WorkItemNode workItemNode = (WorkItemNode) compositeNode.getNodes()[1];
        assertEquals("helloWorld", workItemNode.getName());
        assertEquals("org.something.other.TestService", workItemNode.getWork().getParameter("Interface"));
        assertEquals("get", workItemNode.getWork().getParameter("Operation"));
        assertEquals("org.something.other.TestService", workItemNode.getWork().getParameter("interfaceImplementationRef"));
        assertEquals("get", workItemNode.getWork().getParameter("operationImplementationRef"));
        assertEquals("Java", workItemNode.getWork().getParameter("implementation"));

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-subflow.sw.json", "/exec/single-subflow.sw.yml"})
    public void testSingleSubFlowWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof SubProcessNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        SubProcessNode subProcessNode = (SubProcessNode) process.getNodes()[2];
        assertEquals("abc", subProcessNode.getProcessId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-eventstate.sw.json", "/exec/single-eventstate.sw.yml"})
    public void testSingleEventStateWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[2];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[0];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof CompositeContextNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[1];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-eventstate-multi-eventrefs.sw.json", "/exec/single-eventstate-multi-eventrefs.sw.yml"})
    public void testSingleEventStateMultiEventRefsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(5, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof Join);
        node = process.getNodes()[3];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof StartNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[1];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-operation-many-functions.sw.json", "/exec/single-operation-many-functions.sw.yml"})
    public void testSingleOperationWithManyFunctionsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(4, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[3];
        assertTrue(node instanceof EndNode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/multiple-operations.sw.json", "/exec/multiple-operations.sw.yml"})
    public void testMultipleOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(5, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

        compositeNode = (CompositeContextNode) process.getNodes()[3];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

        compositeNode = (CompositeContextNode) process.getNodes()[4];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-inject-state.sw.json", "/exec/single-inject-state.sw.yml"})
    public void testSingleInjectWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("function", process.getId());
        assertEquals("test-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        ActionNode actionNode = (ActionNode) process.getNodes()[2];
        assertEquals("SimpleInject", actionNode.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/switch-state.sw.json", "/exec/switch-state.sw.yml"})
    public void testSwitchWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("switchworkflow", process.getId());
        assertEquals("switch-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(7, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof Split);
        node = process.getNodes()[5];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[6];
        assertTrue(node instanceof ActionNode);

        Split split = (Split) process.getNodes()[4];
        assertEquals("ChooseOnAge", split.getName());
        assertEquals(2, split.getType());
        assertEquals(2, split.getConstraints().size());

        boolean haveDefaultConstraint = false;
        for (Constraint constraint : split.getConstraints().values()) {
            haveDefaultConstraint = haveDefaultConstraint || constraint.isDefault();
        }

        assertTrue(haveDefaultConstraint);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/switch-state-end-condition.sw.json", "/exec/switch-state-end-condition.sw.yml"})
    public void testSwitchWithEndConditionsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("switchworkflow", process.getId());
        assertEquals("switch-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(6, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof Split);
        node = process.getNodes()[3];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[5];
        assertTrue(node instanceof EndNode);

        Split split = (Split) process.getNodes()[2];
        assertEquals("ChooseOnAge", split.getName());
        assertEquals(2, split.getType());
        assertEquals(2, split.getConstraints().size());

        boolean haveDefaultConstraint = false;
        for (Constraint constraint : split.getConstraints().values()) {
            haveDefaultConstraint = haveDefaultConstraint || constraint.isDefault();
        }

        assertTrue(haveDefaultConstraint);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/parallel-state.sw.json", "/exec/parallel-state.sw.yml"})
    public void testParallelWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("parallelworkflow", process.getId());
        assertEquals("parallel-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(6, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof Split);
        node = process.getNodes()[3];
        assertTrue(node instanceof Join);
        node = process.getNodes()[4];
        assertTrue(node instanceof SubProcessNode);
        node = process.getNodes()[5];
        assertTrue(node instanceof SubProcessNode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/single-decision-operation.sw.json", "/exec/single-decision-operation.sw.yml"})
    public void testSingleDecisionService(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("singledecisionworkflow", process.getId());
        assertEquals("Single Decision Workflow", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);
        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof HumanTaskNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

        assertNotNull(process.getVariableScope().getVariables());
        assertEquals(2, process.getVariableScope().getVariables().size());

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/multi-decision-operation.sw.json", "/exec/multi-decision-operation.sw.yml"})
    public void testMultiDecisionService(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("multidecisionworkflow", process.getId());
        assertEquals("Multi Decision Workflow", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);
        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(4, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof HumanTaskNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof HumanTaskNode);
        node = compositeNode.getNodes()[3];
        assertTrue(node instanceof EndNode);

        assertNotNull(process.getVariableScope().getVariables());
        assertEquals(3, process.getVariableScope().getVariables().size());

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/rule-operation.sw.json", "/exec/rule-operation.sw.yml"})
    public void testRuleSetService(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("ruleunitworkflow", process.getId());
        assertEquals("Rule Unit Workflow", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);
        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof RuleSetNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/transition-produce-event.sw.json", "/exec/transition-produce-event.sw.yml"})
    public void testProduceEventOnTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("produceeventontransition", process.getId());
        assertEquals("Produce Event On Transition", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(5, process.getNodes().length);
        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        ActionNode actionNode = (ActionNode) process.getNodes()[4];
        assertEquals("TestKafkaEvent", actionNode.getName());
        assertEquals("ProduceMessage", actionNode.getMetaData("TriggerType"));
        assertEquals("workflowdata", actionNode.getMetaData("MappingVariable"));
        assertEquals("testtopic", actionNode.getMetaData("TriggerRef"));
        assertEquals("com.fasterxml.jackson.databind.JsonNode", actionNode.getMetaData("MessageType"));

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/eventbased-switch-state.sw.json", "/exec/eventbased-switch-state.sw.yml"})
    public void testEventBasedSwitchWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("eventswitchworkflow", process.getId());
        assertEquals("event-switch-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(10, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof Split);
        node = process.getNodes()[5];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[6];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[7];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[8];
        assertTrue(node instanceof EventNode);
        node = process.getNodes()[9];
        assertTrue(node instanceof EventNode);

        Split split = (Split) process.getNodes()[4];
        assertEquals("ChooseOnEvent", split.getName());
        assertEquals(Split.TYPE_XAND, split.getType());

        EventNode firstEventNode = (EventNode) process.getNodes()[8];
        assertEquals("visaApprovedEvent", firstEventNode.getName());
        assertEquals("workflowdata", firstEventNode.getVariableName());


        EventNode secondEventNode = (EventNode) process.getNodes()[9];
        assertEquals("visaDeniedEvent", secondEventNode.getName());
        assertEquals("workflowdata", secondEventNode.getVariableName());

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/integration-operation.sw.json", "/exec/integration-operation.sw.yml"})
    public void testSingleIntegrationOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("integrationfunctionworkflow", process.getId());
        assertEquals("Integration Function Workflow", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(3, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);

        // now check the composite one to see what nodes it has
        CompositeContextNode compositeNode = (CompositeContextNode) process.getNodes()[2];

        assertEquals(3, compositeNode.getNodes().length);

        node = compositeNode.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = compositeNode.getNodes()[1];
        assertTrue(node instanceof WorkItemNode);
        node = compositeNode.getNodes()[2];
        assertTrue(node instanceof EndNode);

        WorkItemNode workItemNode = (WorkItemNode) compositeNode.getNodes()[1];
        assertEquals("integrationfunction", workItemNode.getName());
        assertEquals("org.apache.camel.ProducerTemplate.requestBody", workItemNode.getWork().getName());
        assertEquals("direct:testroutename", workItemNode.getWork().getParameter("endpoint"));
        assertEquals("org.apache.camel.ProducerTemplate", workItemNode.getWork().getParameter("Interface"));
        assertEquals("requestBody", workItemNode.getWork().getParameter("Operation"));
        assertEquals("org.apache.camel.ProducerTemplate", workItemNode.getWork().getParameter("interfaceImplementationRef"));
        assertEquals("Java", workItemNode.getWork().getParameter("implementation"));

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/prchecker.sw.json", "/exec/prchecker.sw.yml"})
    public void testPrCheckerWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("prchecker", process.getId());
        assertEquals("Github PR Checker Workflow", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(9, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof Join);
        node = process.getNodes()[2];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof Split);
        node = process.getNodes()[5];
        assertTrue(node instanceof Split);
        node = process.getNodes()[6];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[7];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[8];
        assertTrue(node instanceof EndNode);

        Split split = (Split) process.getNodes()[4];
        assertEquals("CheckBackend", split.getName());
        assertEquals(2, split.getType());
        assertEquals(2, split.getConstraints().size());

        Split split2 = (Split) process.getNodes()[5];
        assertEquals("CheckFrontend", split2.getName());
        assertEquals(2, split2.getType());
        assertEquals(2, split2.getConstraints().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/transition-produce-multi-events.sw.json", "/exec/transition-produce-multi-events.sw.yml"})
    public void testProduceMultiEventsOnTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("produceeventontransition", process.getId());
        assertEquals("Produce Event On Transition", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(8, process.getNodes().length);
        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[5];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[6];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[7];
        assertTrue(node instanceof ActionNode);

        ActionNode actionNode = (ActionNode) process.getNodes()[4];
        assertEquals("TestKafkaEvent", actionNode.getName());

        ActionNode actionNode2 = (ActionNode) process.getNodes()[5];
        assertEquals("TestKafkaEvent2", actionNode2.getName());

        ActionNode actionNode3 = (ActionNode) process.getNodes()[6];
        assertEquals("TestKafkaEvent3", actionNode3.getName());

        ActionNode actionNode4 = (ActionNode) process.getNodes()[7];
        assertEquals("TestKafkaEvent4", actionNode4.getName());

    }

    @ParameterizedTest
    @ValueSource(strings = {"/exec/switch-state-produce-events.sw.json", "/exec/switch-state-produce-events.sw.yml"})
    public void testSwitchProduceEventsOnTransitionWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertEquals("switchworkflow", process.getId());
        assertEquals("switch-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(15, process.getNodes().length);

        Split split = (Split) process.getNodes()[4];
        assertEquals("ChooseOnAge", split.getName());
        assertEquals(2, split.getType());
        assertEquals(2, split.getConstraints().size());

        boolean haveDefaultConstraint = false;
        for (Constraint constraint : split.getConstraints().values()) {
            haveDefaultConstraint = haveDefaultConstraint || constraint.isDefault();
        }

        assertTrue(haveDefaultConstraint);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/examples/applicantrequest.sw.json", "/examples/applicantrequest.sw.yml",
            "/examples/carauctionbids.sw.json", "/examples/carauctionbids.sw.yml",
            "/examples/creditcheck.sw.json", "/examples/creditcheck.sw.yml",
            "/examples/eventbasedgreeting.sw.json", "/examples/eventbasedgreeting.sw.yml",
            "/examples/finalizecollegeapplication.sw.json", "/examples/finalizecollegeapplication.sw.yml",
            "/examples/greeting.sw.json", "/examples/greeting.sw.yml",
            "/examples/helloworld.sw.json", "/examples/helloworld.sw.yml",
            "/examples/jobmonitoring.sw.json", "/examples/jobmonitoring.sw.yml",
            "/examples/monitorpatient.sw.json", "/examples/monitorpatient.sw.yml",
            "/examples/parallel.sw.json", "/examples/parallel.sw.yml",
            "/examples/provisionorder.sw.json", "/examples/provisionorder.sw.yml",
            "/examples/sendcloudevent.sw.json", "/examples/sendcloudevent.sw.yml",
            "/examples/solvemathproblems.sw.json", "/examples/solvemathproblems.sw.yml",
            "/examples/foreachstatewithactions.sw.json", "/examples/foreachstatewithactions.sw.yml",
            "/examples/periodicinboxcheck.sw.json", "/examples/periodicinboxcheck.sw.yml",
            "/examples/vetappointmentservice.sw.json", "/examples/vetappointmentservice.sw.yml",
            "/examples/eventbasedtransition.sw.json", "/examples/eventbasedtransition.sw.yml"
    })
    public void testSpecExamplesParsing(String workflowLocation) throws Exception {
        Workflow workflow = Workflow.fromSource(WorkflowTestUtils.readWorkflowFile(workflowLocation));

        assertNotNull(workflow);
        assertNotNull(workflow.getId());
        assertNotNull(workflow.getName());
        assertNotNull(workflow.getStates());
        assertTrue(workflow.getStates().size() > 0);
    }

}