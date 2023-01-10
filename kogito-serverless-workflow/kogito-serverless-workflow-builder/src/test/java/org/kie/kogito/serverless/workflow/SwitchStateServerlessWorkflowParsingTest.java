/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serverless.workflow;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertClassAndGetNode;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertConstraintIsDefault;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertExclusiveSplit;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertHasName;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertHasNodesSize;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertIsConnected;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertProcessMainParams;

class SwitchStateServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-data-condition-transition.sw.json", "/exec/switch-state-data-condition-transition.sw.yml" })
    void switchStateDataConditionTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_data_condition_transition",
                "Switch State Data Condition Transition Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 7);
        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode processEndNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode processEndNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        Split splitNode = assertClassAndGetNode(process, 3, Split.class);
        assertExclusiveSplit(splitNode, "ChooseOnAge", 2);
        assertConstraintIsDefault(splitNode, "4_7");
        ActionNode approveTransitionActionNode = assertClassAndGetNode(process, 4, ActionNode.class);
        assertHasName(approveTransitionActionNode, "Approve");
        ActionNode denyTransitionActionNode = assertClassAndGetNode(process, 5, ActionNode.class);
        assertHasName(denyTransitionActionNode, "Deny");
        Join joinNode = assertClassAndGetNode(process, 6, Join.class);

        assertIsConnected(processStartNode, splitNode);
        assertIsConnected(splitNode, approveTransitionActionNode);
        assertIsConnected(approveTransitionActionNode, processEndNode1);
        assertIsConnected(splitNode, joinNode);
        assertIsConnected(joinNode, denyTransitionActionNode);
        assertIsConnected(denyTransitionActionNode, processEndNode2);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-data-condition-end.sw.json", "/exec/switch-state-data-condition-end.sw.yml" })
    void switchStateDataConditionEnd(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_data_condition_end",
                "Switch State Data Condition End Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 6);
        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        ActionNode addInfoActionNode = assertClassAndGetNode(process, 1, ActionNode.class);
        assertHasName(addInfoActionNode, "AddInfo");
        Split splitNode = assertClassAndGetNode(process, 2, Split.class);
        assertExclusiveSplit(splitNode, "ChooseOnAge", 2);
        assertConstraintIsDefault(splitNode, "3_5");
        assertConstraintIsDefault(splitNode, "3_6");
        EndNode processEndNode1 = assertClassAndGetNode(process, 3, EndNode.class);
        EndNode processEndNode2 = assertClassAndGetNode(process, 4, EndNode.class);
        EndNode processEndNode3 = assertClassAndGetNode(process, 5, EndNode.class);

        assertIsConnected(processStartNode, addInfoActionNode);
        assertIsConnected(addInfoActionNode, splitNode);
        assertIsConnected(splitNode, processEndNode1);
        assertIsConnected(splitNode, processEndNode2);
        assertIsConnected(splitNode, processEndNode3);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-end.sw.json", "/exec/switch-state-event-condition-timeouts-end.sw.yml" })
    void switchStateEventConditionTimeoutsEnd(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_end",
                "Switch State Event Condition Timeouts End Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 12);

        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode endNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode endNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        Split splitNode = assertClassAndGetNode(process, 3, Split.class);
        assertHasName(splitNode, "ChooseOnEvent");
        CompositeContextNode approvedVisaState = assertClassAndGetNode(process, 4, CompositeContextNode.class);
        assertHasName(approvedVisaState, "ApprovedVisa");
        CompositeContextNode deniedVisaState = assertClassAndGetNode(process, 5, CompositeContextNode.class);
        assertHasName(deniedVisaState, "DeniedVisa");
        TimerNode timeoutTimerNode = assertClassAndGetNode(process, 6, TimerNode.class);
        assertThat(timeoutTimerNode.getTimer().getDelay()).isEqualTo("PT5S");
        EndNode endNode3 = assertClassAndGetNode(process, 7, EndNode.class);
        EventNode visaApprovedEventNode = assertClassAndGetNode(process, 8, EventNode.class);
        assertHasName(visaApprovedEventNode, "visaApprovedEvent");
        ActionNode visaApprovedEventNodeMergeAction = assertClassAndGetNode(process, 9, ActionNode.class);
        EventNode visaDeniedEventNode = assertClassAndGetNode(process, 10, EventNode.class);
        assertHasName(visaDeniedEventNode, "visaDeniedEvent");
        ActionNode visaDeniedEventNodeMergeAction = assertClassAndGetNode(process, 11, ActionNode.class);

        assertIsConnected(processStartNode, splitNode);
        assertIsConnected(splitNode, timeoutTimerNode);
        assertIsConnected(timeoutTimerNode, endNode3);

        assertIsConnected(splitNode, visaApprovedEventNode);
        assertIsConnected(visaApprovedEventNode, visaApprovedEventNodeMergeAction);
        assertIsConnected(visaApprovedEventNodeMergeAction, approvedVisaState);
        assertIsConnected(approvedVisaState, endNode1);

        assertIsConnected(splitNode, visaDeniedEventNode);
        assertIsConnected(visaDeniedEventNode, visaDeniedEventNodeMergeAction);
        assertIsConnected(visaDeniedEventNodeMergeAction, deniedVisaState);
        assertIsConnected(deniedVisaState, endNode2);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-transition.sw.json", "/exec/switch-state-event-condition-timeouts-transition.sw.yml" })
    void switchStateEventConditionTimeoutsTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_transition",
                "Switch State Event Condition Timeouts Transition Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 13);

        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode endNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode endNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        EndNode endNode3 = assertClassAndGetNode(process, 3, EndNode.class);
        Split splitNode = assertClassAndGetNode(process, 4, Split.class);
        assertHasName(splitNode, "ChooseOnEvent");
        CompositeContextNode approvedVisaState = assertClassAndGetNode(process, 5, CompositeContextNode.class);
        assertHasName(approvedVisaState, "ApprovedVisa");
        CompositeContextNode deniedVisaState = assertClassAndGetNode(process, 6, CompositeContextNode.class);
        assertHasName(deniedVisaState, "DeniedVisa");
        CompositeContextNode handleNoVisaDecisionState = assertClassAndGetNode(process, 7, CompositeContextNode.class);
        assertHasName(handleNoVisaDecisionState, "HandleNoVisaDecision");
        TimerNode timeoutTimerNode = assertClassAndGetNode(process, 8, TimerNode.class);
        assertThat(timeoutTimerNode.getTimer().getDelay()).isEqualTo("PT5S");
        EventNode visaApprovedEvent = assertClassAndGetNode(process, 9, EventNode.class);
        assertHasName(visaApprovedEvent, "visaApprovedEvent");
        ActionNode visaApprovedEventNodeMergeAction = assertClassAndGetNode(process, 10, ActionNode.class);
        EventNode visaDeniedEvent = assertClassAndGetNode(process, 11, EventNode.class);
        assertHasName(visaDeniedEvent, "visaDeniedEvent");
        ActionNode visaDeniedEventMergeAction = assertClassAndGetNode(process, 12, ActionNode.class);

        assertIsConnected(processStartNode, splitNode);

        assertIsConnected(splitNode, visaApprovedEvent);
        assertIsConnected(visaApprovedEvent, visaApprovedEventNodeMergeAction);
        assertIsConnected(visaApprovedEventNodeMergeAction, approvedVisaState);
        assertIsConnected(approvedVisaState, endNode1);

        assertIsConnected(splitNode, visaDeniedEvent);
        assertIsConnected(visaDeniedEvent, visaDeniedEventMergeAction);
        assertIsConnected(visaDeniedEventMergeAction, deniedVisaState);
        assertIsConnected(deniedVisaState, endNode2);

        assertIsConnected(splitNode, timeoutTimerNode);
        assertIsConnected(timeoutTimerNode, handleNoVisaDecisionState);
        assertIsConnected(handleNoVisaDecisionState, endNode3);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-transition2.sw.json", "/exec/switch-state-event-condition-timeouts-transition2.sw.yml" })
    void switchStateEventConditionTimeoutsTransition2(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_transition2",
                "Switch State Event Condition Timeouts Transition2 Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 12);

        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode endNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode endNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        Split splitNode = assertClassAndGetNode(process, 3, Split.class);
        assertHasName(splitNode, "ChooseOnEvent");
        CompositeContextNode approvedVisaState = assertClassAndGetNode(process, 4, CompositeContextNode.class);
        assertHasName(approvedVisaState, "ApprovedVisa");
        CompositeContextNode deniedVisaState = assertClassAndGetNode(process, 5, CompositeContextNode.class);
        assertHasName(deniedVisaState, "DeniedVisa");
        TimerNode timeoutTimerNode = assertClassAndGetNode(process, 6, TimerNode.class);
        assertThat(timeoutTimerNode.getTimer().getDelay()).isEqualTo("PT5S");
        EventNode visaApprovedEvent = assertClassAndGetNode(process, 7, EventNode.class);
        assertHasName(visaApprovedEvent, "visaApprovedEvent");
        ActionNode visaApprovedEventNodeMergeAction = assertClassAndGetNode(process, 8, ActionNode.class);
        EventNode visaDeniedEvent = assertClassAndGetNode(process, 9, EventNode.class);
        assertHasName(visaDeniedEvent, "visaDeniedEvent");
        ActionNode visaDeniedEventMergeAction = assertClassAndGetNode(process, 10, ActionNode.class);
        Join visaDeniedJoinNode = assertClassAndGetNode(process, 11, Join.class);

        assertIsConnected(processStartNode, splitNode);

        assertIsConnected(splitNode, visaApprovedEvent);
        assertIsConnected(visaApprovedEvent, visaApprovedEventNodeMergeAction);
        assertIsConnected(visaApprovedEventNodeMergeAction, approvedVisaState);
        assertIsConnected(approvedVisaState, endNode1);

        assertIsConnected(splitNode, visaDeniedEvent);
        assertIsConnected(visaDeniedEvent, visaDeniedEventMergeAction);
        assertIsConnected(visaDeniedEventMergeAction, visaDeniedJoinNode);

        assertIsConnected(splitNode, timeoutTimerNode);
        assertIsConnected(timeoutTimerNode, visaDeniedJoinNode);

        assertIsConnected(visaDeniedJoinNode, deniedVisaState);
        assertIsConnected(deniedVisaState, endNode2);
    }
}
