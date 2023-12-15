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

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertClassAndGetNode;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertHasName;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertHasNodesSize;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertIsConnected;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertProcessMainParams;

class CallbackStateServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/callback-state.sw.json", "/exec/callback-state.sw.yml" })
    void produceCallbackState(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        // assert the process main parameters
        assertProcessMainParams(process,
                "callback_state",
                "Callback State",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        // assert the main process structure
        assertCallbackProcessMainStructure(process);

        // assert the CallbackState internal structure for the no timeouts case
        CompositeContextNode callbackState = assertClassAndGetNode(process, 3, CompositeContextNode.class);
        assertHasNodesSize(callbackState, 6);
        StartNode stateStartNode = assertClassAndGetNode(callbackState, 0, StartNode.class);
        assertHasName(stateStartNode, "EmbeddedStart");
        WorkItemNode stateActionNode = assertClassAndGetNode(callbackState, 1, WorkItemNode.class);
        assertHasName(stateActionNode, "callbackFunction");
        ActionNode afterStateActionMergeNode = assertClassAndGetNode(callbackState, 2, ActionNode.class);
        EventNode stateEventNode = assertClassAndGetNode(callbackState, 3, EventNode.class);
        assertHasName(stateEventNode, "callbackEvent");
        ActionNode afterStateEventMergeNode = assertClassAndGetNode(callbackState, 4, ActionNode.class);
        EndNode stateEndNode = assertClassAndGetNode(callbackState, 5, EndNode.class);
        assertHasName(stateEndNode, "EmbeddedEnd");

        assertIsConnected(stateStartNode, stateActionNode);
        assertIsConnected(stateActionNode, afterStateActionMergeNode);
        assertIsConnected(afterStateActionMergeNode, stateEventNode);
        assertIsConnected(stateEventNode, afterStateEventMergeNode);
        assertIsConnected(afterStateEventMergeNode, stateEndNode);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/callback-state-timeouts.sw.json", "/exec/callback-state-timeouts.sw.yml" })
    void produceCallbackStateWithTimeouts(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        // assert the process main parameters
        assertProcessMainParams(process,
                "callback_state_timeouts",
                "Callback State Timeouts",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        // assert the main process structure
        assertCallbackProcessMainStructure(process);

        // assert the CallbackState internal structure for the timeouts case
        CompositeContextNode callbackState = assertClassAndGetNode(process, 3, CompositeContextNode.class);
        assertHasNodesSize(callbackState, 9);
        StartNode stateStartNode = assertClassAndGetNode(callbackState, 0, StartNode.class);
        assertHasName(stateStartNode, "EmbeddedStart");
        WorkItemNode stateActionNode = assertClassAndGetNode(callbackState, 1, WorkItemNode.class);
        assertHasName(stateActionNode, "callbackFunction");
        ActionNode afterStateActionMergeNode = assertClassAndGetNode(callbackState, 2, ActionNode.class);
        Split stateSplitNode = assertClassAndGetNode(callbackState, 5, Split.class);
        assertHasName(stateSplitNode, "EventSplit_" + stateSplitNode.getId());
        Join stateJoinNode = assertClassAndGetNode(callbackState, 6, Join.class);
        assertHasName(stateJoinNode, "ExclusiveJoin_" + stateJoinNode.getId());
        EventNode stateEventNode = assertClassAndGetNode(callbackState, 3, EventNode.class);
        assertHasName(stateEventNode, "callbackEvent");
        ActionNode afterStateEventMergeNode = assertClassAndGetNode(callbackState, 4, ActionNode.class);
        TimerNode stateTimerNode = assertClassAndGetNode(callbackState, 7, TimerNode.class);
        assertHasName(stateTimerNode, "TimerNode_" + stateTimerNode.getId());
        assertThat(stateTimerNode.getTimer().getDelay()).isEqualTo("PT5S");
        assertThat(stateTimerNode.getTimer().getTimeType()).isEqualTo(1);
        EndNode stateEndNode = assertClassAndGetNode(callbackState, 8, EndNode.class);
        assertHasName(stateEndNode, "EmbeddedEnd");

        assertIsConnected(stateStartNode, stateActionNode);
        assertIsConnected(stateActionNode, afterStateActionMergeNode);
        assertIsConnected(afterStateActionMergeNode, stateSplitNode);
        assertIsConnected(stateSplitNode, stateEventNode);
        assertIsConnected(stateEventNode, afterStateEventMergeNode);
        assertIsConnected(afterStateEventMergeNode, stateJoinNode);
        assertIsConnected(stateSplitNode, stateTimerNode);
        assertIsConnected(stateTimerNode, stateJoinNode);
        assertIsConnected(stateJoinNode, stateEndNode);
    }

    private void assertCallbackProcessMainStructure(RuleFlowProcess process) {
        assertHasNodesSize(process, 7);
        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode processEndNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode processEndNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        CompositeContextNode callbackState = assertClassAndGetNode(process, 3, CompositeContextNode.class);
        assertHasName(callbackState, "CallbackState");
        ActionNode processFinalizeSuccessfulState = assertClassAndGetNode(process, 5, ActionNode.class);
        assertHasName(processFinalizeSuccessfulState, "FinalizeSuccessful");
        ActionNode processFinalizeWithErrorState = assertClassAndGetNode(process, 6, ActionNode.class);
        assertHasName(processFinalizeWithErrorState, "FinalizeWithError");
        assertClassAndGetNode(process, 4, BoundaryEventNode.class);
        assertIsConnected(processStartNode, callbackState);
        assertIsConnected(callbackState, processFinalizeSuccessfulState);
        assertIsConnected(processFinalizeSuccessfulState, processEndNode1);
        assertIsConnected(processFinalizeWithErrorState, processEndNode2);
    }
}
