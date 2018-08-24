/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.ruleflow.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RuleFlowProcessValidatorTest {

    private RuleFlowProcessValidator validator;

    private List<ProcessValidationError> errors;

    @Mock
    private RuleFlowProcess process;

    @Mock
    private Node node;

    @Before
    public void setUp() throws Exception {
        errors = new ArrayList<ProcessValidationError>();
        validator = RuleFlowProcessValidator.getInstance();
    }

    @Test
    public void testAddErrorMessage() throws Exception {
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
    public void testDynamicNodeValidationInNotDynamicProcess() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process");
        process.setName("Dynamic Node Process");
        process.setPackageName("org.mycomp.myprocess");
        process.setDynamic(false);

        DynamicNode dynamicNode = new DynamicNode();
        dynamicNode.setName("MyDynamicNode");
        dynamicNode.setId(1);
        dynamicNode.setAutoComplete(false);
        // empty completion expression to trigger validation error
        dynamicNode.setCompletionExpression("");
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
    public void testDynamicNodeValidationInDynamicProcess() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process");
        process.setName("Dynamic Node Process");
        process.setPackageName("org.mycomp.myprocess");
        process.setDynamic(true);

        DynamicNode dynamicNode = new DynamicNode();
        dynamicNode.setName("MyDynamicNode");
        dynamicNode.setId(1);
        dynamicNode.setAutoComplete(false);
        dynamicNode.setCompletionExpression("completion-expression");
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
        dynamicNode2.setCompletionExpression("");
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
    public void testEmptyPackageName() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process");
        process.setName("Empty Package Name Process");
        process.setPackageName("");
        process.setDynamic(true);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(0,
                     errors.length);
    }

    @Test
    public void testNoPackageName() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process");
        process.setName("No Package Name Process");
        process.setDynamic(true);

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(0,
                     errors.length);
    }

    @Test
    public void testCompositeNodeNoStart() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.process");
        process.setName("Process");

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
                Node.CONNECTION_DEFAULT_TYPE
        );
        new org.jbpm.workflow.core.impl.ConnectionImpl(
                compositeNode,
                Node.CONNECTION_DEFAULT_TYPE,
                endNode,
                Node.CONNECTION_DEFAULT_TYPE
        );

        ProcessValidationError[] errors = validator.validateProcess(process);
        assertNotNull(errors);
        assertEquals(1,
                     errors.length);
        assertEquals("Node 'CompositeNode' [3] Composite has no start node defined.",
                     errors[0].getMessage());
    }
}