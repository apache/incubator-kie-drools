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

import java.io.InputStreamReader;
import java.io.Reader;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.jbpm.workflow.core.node.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.definition.process.Node;

import static org.junit.jupiter.api.Assertions.*;

public class ServlerlessWorkflowParsingTest {

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
        assertTrue(node instanceof CompositeContextNode);
        node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
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
    @ValueSource(strings = {"/specexamples/helloworld.sw.json", "/specexamples/helloworld.sw.yml",
            "/specexamples/greeting.sw.json", "/specexamples/greeting.sw.yml",
            "/specexamples/eventbasedgreeting.sw.json", "/specexamples/eventbasedgreeting.sw.yml",
            "/specexamples/solvemathproblems.sw.json", "/specexamples/solvemathproblems.sw.yml",
            "/specexamples/parallel.sw.json", "/specexamples/parallel.sw.yml",
            "/specexamples/jobmonitoring.sw.json", "/specexamples/jobmonitoring.sw.yml",
            "/specexamples/sendcloudevent.sw.json", "/specexamples/sendcloudevent.sw.yml",
            "/specexamples/monitorpatient.sw.json", "/specexamples/monitorpatient.sw.yml",
            "/specexamples/finalizecollegeapplication.sw.json", "/specexamples/finalizecollegeapplication.sw.yml",
            "/specexamples/creditcheck.sw.json", "/specexamples/creditcheck.sw.yml"
    })
    public void testSpecExamplesParsing(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation).parseWorkFlow(classpathResourceReader(workflowLocation));
        assertNotNull(process);
    }

        protected Reader classpathResourceReader(String location) {
        return new InputStreamReader(this.getClass().getResourceAsStream(location));
    }

    protected ServerlessWorkflowParser getWorkflowParser(String workflowLocation) {
        ServerlessWorkflowParser parser;
        if(workflowLocation.endsWith(".sw.json")) {
            parser = new ServerlessWorkflowParser("json");
        } else {
            parser = new ServerlessWorkflowParser("yml");
        }
        return parser;
    }
}