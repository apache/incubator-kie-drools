/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.ruleflow.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleFlowProcessValidatorTest {

    private RuleFlowProcessValidator validator;

    private List<ProcessValidationError> errors;

    private RuleFlowProcess process = new RuleFlowProcess();

    private Node node = mock(Node.class);

    @BeforeEach
    public void setUp() {
        errors = new ArrayList<>();
        validator = RuleFlowProcessValidator.getInstance();

        process = new RuleFlowProcess();
        process.setId("org.drools.core.process");
        process.setName("Dynamic Node Process");
        process.setPackageName("org.mycomp.myprocess");
        process.setDynamic(false);
    }

    @Test
    void testAddErrorMessage() {
        when(node.getName()).thenReturn("nodeName");
        when(node.getId()).thenReturn(Long.MAX_VALUE);
        validator.addErrorMessage(process,
                node,
                errors,
                "any message");
        assertEquals(1,
                errors.size());
        assertEquals("Node 'nodeName' [" + Long.MAX_VALUE + "] any message",
                errors.get(0).getMessage());
    }

    @Test
    void testDynamicNodeValidationInNotDynamicProcess() {
        DynamicNode dynamicNode = new DynamicNode();
        dynamicNode.setName("MyDynamicNode");
        dynamicNode.setId(1);
        dynamicNode.setAutoComplete(false);
        // empty completion expression to trigger validation error
        process.addNode(dynamicNode);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        // in non-dynamic processes all check should be triggered
        // they should also include process level checks (start node, end node etc)
        assertEquals(6,
                errors.length);
        assertEquals("Process has no start node.",
                errors[0].getMessage());
        assertEquals("Process has no end node.",
                errors[1].getMessage());
        assertEquals("Node 'MyDynamicNode' [1] Dynamic has no incoming connection",
                errors[2].getMessage());
        assertEquals("Node 'MyDynamicNode' [1] Dynamic has no outgoing connection",
                errors[3].getMessage());
        assertEquals("Node 'MyDynamicNode' [1] Dynamic has no completion condition set",
                errors[4].getMessage());
        assertEquals("Node 'MyDynamicNode' [1] Has no connection to the start node.",
                errors[5].getMessage());
    }

    @Test
    void testDynamicNodeValidationInDynamicProcess() {
        process.setDynamic(true);

        DynamicNode dynamicNode = new DynamicNode();
        dynamicNode.setName("MyDynamicNode");
        dynamicNode.setId(1);
        dynamicNode.setAutoComplete(false);
        dynamicNode.setCompletionExpression(kcontext -> true);
        process.addNode(dynamicNode);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        // if dynamic process no longer triggering incoming / outgoing connection errors for dynamic nodes
        assertEquals(0,
                errors.length);

        // empty completion expression to trigger validation error
        process.removeNode(dynamicNode);
        DynamicNode dynamicNode2 = new DynamicNode();
        dynamicNode2.setName("MyDynamicNode");
        dynamicNode2.setId(1);
        dynamicNode2.setAutoComplete(false);
        process.addNode(dynamicNode2);

        ProcessValidationError[] errors2 = validator.validateProcess(process);
        assertNotNull(errors2);
        // autocomplete set to false and empty completion condition triggers error
        assertEquals(1,
                errors2.length);
        assertEquals("Node 'MyDynamicNode' [1] Dynamic has no completion condition set",
                errors2[0].getMessage());
    }

    @Test
    void testEmptyPackageName() {
        process.setDynamic(true);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(0,
                errors.length);
    }

    @Test
    void testNoPackageName() {
        process.setDynamic(true);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(0,
                errors.length);
    }

    @Test
    void testIdVariableName() {
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                startNode,
                Node.CONNECTION_DEFAULT_TYPE,
                endNode,
                Node.CONNECTION_DEFAULT_TYPE);

        Variable idVariable = new Variable();
        idVariable.setName("id");
        idVariable.setType(new StringDataType());
        process.getVariableScope().addVariable(idVariable);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(1,
                errors.length);
        assertEquals("Variable 'id' is used by Kogito, please rename it.",
                errors[0].getMessage());
    }

    @Test
    void testCompositeNodeNoStart() {
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        CompositeNode compositeNode = new CompositeNode();
        compositeNode.setName("CompositeNode");
        compositeNode.setId(3);
        process.addNode(compositeNode);
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                startNode,
                Node.CONNECTION_DEFAULT_TYPE,
                compositeNode,
                Node.CONNECTION_DEFAULT_TYPE);
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                compositeNode,
                Node.CONNECTION_DEFAULT_TYPE,
                endNode,
                Node.CONNECTION_DEFAULT_TYPE);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(1,
                errors.length);
        assertEquals("Node 'CompositeNode' [3] Composite has no start node defined.",
                errors[0].getMessage());
    }

    //TODO To be removed once https://issues.redhat.com/browse/KOGITO-2067 is fixed
    @Test
    void testOnEntryOnExitValidation() {
        testNodeOnEntryOnExit(new MilestoneNode());
        RuleSetNode ruleSetNode = new RuleSetNode();
        ruleSetNode.setRuleType(RuleSetNode.RuleType.ruleUnit("test"));
        testNodeOnEntryOnExit(ruleSetNode);
        testNodeOnEntryOnExit(new SubProcessNode());
        testNodeOnEntryOnExit(new WorkItemNode());
        testNodeOnEntryOnExit(new ForEachNode());
        testNodeOnEntryOnExit(new DynamicNode());
        testNodeOnEntryOnExit(new CompositeNode());
    }

    private void testNodeOnEntryOnExit(ExtendedNodeImpl node) {
        List<ProcessValidationError> errors = new ArrayList<>();
        node.setName("name");
        node.setId(1);
        node.setActions(ExtendedNodeImpl.EVENT_NODE_ENTER, singletonList(new DroolsAction()));
        node.setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, singletonList(new DroolsAction()));
        validator.validateNodes(new org.kie.api.definition.process.Node[] { node }, errors, process);
        assertThat(errors).extracting("message").contains(
                "Node 'name' [1] On Entry Action is not yet supported in Kogito",
                "Node 'name' [1] On Exit Action is not yet supported in Kogito");
    }

    @Test
    void testScriptTaskDialect() {
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        ActionNode actionNode1 = new ActionNode();
        actionNode1.setName("ActionNode1");
        actionNode1.setAction(new DroolsConsequenceAction("mvel", "System.out.println();"));
        actionNode1.setId(3);
        process.addNode(actionNode1);
        ActionNode actionNode2 = new ActionNode();
        actionNode2.setName("ActionNode2");
        actionNode2.setAction(new DroolsConsequenceAction("java", "System.out.println();"));
        actionNode2.setId(4);
        process.addNode(actionNode2);
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                startNode,
                Node.CONNECTION_DEFAULT_TYPE,
                actionNode1,
                Node.CONNECTION_DEFAULT_TYPE);
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                actionNode1,
                Node.CONNECTION_DEFAULT_TYPE,
                actionNode2,
                Node.CONNECTION_DEFAULT_TYPE);
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                actionNode2,
                Node.CONNECTION_DEFAULT_TYPE,
                endNode,
                Node.CONNECTION_DEFAULT_TYPE);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(1,
                errors.length);
        assertEquals("Node 'ActionNode1' [3] mvel script language is not supported in Kogito.",
                errors[0].getMessage());
    }
}
