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

package org.jbpm.workflow.core.node;

import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTypeTest {

    @Test
    public void testNodeTypeSpec() {

        Node node = new ActionNode();
        assertEquals(NodeType.SCRIPT_TASK, node.getNodeType());
        node = new ForEachNode();
        assertEquals(NodeType.FOR_EACH, node.getNodeType());
        node = new MilestoneNode();
        assertEquals(NodeType.MILESTONE, node.getNodeType());
        node = new FaultNode();
        assertEquals(NodeType.FAULT, node.getNodeType());
        node = new Join();
        assertEquals(NodeType.COMPLEX_GATEWAY, node.getNodeType());
        node = new Split(Split.TYPE_AND);
        assertEquals(NodeType.PARALLEL_GATEWAY, node.getNodeType());
        node = new Split(Split.TYPE_OR);
        assertEquals(NodeType.INCLUSIVE_GATEWAY, node.getNodeType());
        node = new Split(Split.TYPE_XOR);
        assertEquals(NodeType.EXCLUSIVE_GATEWAY, node.getNodeType());
        node = new Split(Split.TYPE_XAND);
        assertEquals(NodeType.EVENT_BASED_GATEWAY, node.getNodeType());
        node = new ThrowLinkNode();
        assertEquals(NodeType.THROW_LINK, node.getNodeType());
        node = new CatchLinkNode();
        assertEquals(NodeType.CATCH_LINK, node.getNodeType());
        node = new RuleSetNode();
        assertEquals(NodeType.BUSINESS_RULE, node.getNodeType());
        node = new TimerNode();
        assertEquals(NodeType.TIMER, node.getNodeType());
        node = new WorkItemNode();
        assertEquals(NodeType.WORKITEM_TASK, node.getNodeType());
        node = new SubProcessNode();
        assertEquals(NodeType.SUBPROCESS, node.getNodeType());
        node = new StateNode();
        assertEquals(NodeType.CONDITIONAL, node.getNodeType());
        node = new StartNode();
        assertEquals(NodeType.START, node.getNodeType());
        node = new HumanTaskNode();
        assertEquals(NodeType.HUMAN_TASK, node.getNodeType());
        node = new EventNode();
        assertEquals(NodeType.CATCH_EVENT, node.getNodeType());
        node = new EndNode();
        assertEquals(NodeType.END, node.getNodeType());
        node = new DynamicNode();
        assertEquals(NodeType.AD_HOC_SUBPROCESS, node.getNodeType());
        node = new EventSubProcessNode();
        assertEquals(NodeType.EVENT_SUBPROCESS, node.getNodeType());
        node = new BoundaryEventNode();
        assertEquals(NodeType.BOUNDARY_EVENT, node.getNodeType());
    }
}
