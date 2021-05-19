/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.serverless.workflow.parser.handlers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.serverless.workflow.parser.NodeIdGenerator;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.jbpm.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.jbpm.workflow.core.node.Split;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.EventDefinition;
import io.serverlessworkflow.api.produce.ProduceEvent;
import io.serverlessworkflow.api.states.SwitchState;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.serverlessworkflow.api.switchconditions.EventCondition;
import io.serverlessworkflow.api.transitions.Transition;

public class SwitchHandler<P extends RuleFlowNodeContainerFactory<P, ?>> extends
        StateHandler<SwitchState, SplitFactory<P>, P> {

    private static final String XORSPLITDEFAULT = "Default";

    protected SwitchHandler(SwitchState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            NodeIdGenerator idGenerator) {
        super(state, workflow, factory, idGenerator);

    }

    @Override
    public SplitFactory<P> makeNode() {
        long id = idGenerator.getId();
        SplitFactory<P> splitFactory = factory.splitNode(id).name(state
                .getName());
        // check if data-based or event-based switch state
        if (!state.getDataConditions().isEmpty()) {
            splitFactory.type(Split.TYPE_XOR);
        } else {
            splitFactory.type(Split.TYPE_XAND);
            splitFactory.metaData("UniqueId", Long.toString(id));
            splitFactory.metaData("EventBased", "true");
        }
        return splitFactory;
    }

    @Override
    public void handleTransitions(Map<String, StateHandler<?, ?, ?>> stateConnection) {
        super.handleTransitions(stateConnection);
        StateHandler<?, ?, ?> connInfo = stateConnection.get(state.getName());
        if (connInfo == null) {
            throw new IllegalStateException("unable to get split node for switch state }" + state.getName());
        }
        if (!state.getDataConditions().isEmpty()) {
            finalizeDataBasedSwitchState(connInfo.getNode(), stateConnection);
        } else {
            finalizeEventBasedSwitchState(connInfo.getNode(), stateConnection);
        }
    }

    private void finalizeEventBasedSwitchState(NodeFactory<?, ?> startNode, Map<String, StateHandler<?, ?, ?>> stateConnection) {
        List<EventCondition> conditions = state.getEventConditions();
        for (EventCondition eventCondition : conditions) {
            EventDefinition eventDefinition = ServerlessWorkflowUtils.getWorkflowEventFor(workflow,
                    eventCondition.getEventRef());
            long targetId = stateConnection.get(eventCondition.getTransition().getNextState()).getNode().getNode()
                    .getId();
            long eventId = idGenerator.getId();
            ServerlessWorkflowParser.consumeEventNode(factory.eventNode(eventId), eventDefinition).done().connection(
                    startNode.getNode().getId(), eventId).connection(eventId, targetId);
        }
    }

    private void finalizeDataBasedSwitchState(NodeFactory<?, ?> startNode, Map<String, StateHandler<?, ?, ?>> stateConnection) {
        long splitId = startNode.getNode().getId();
        // set default connection
        if (state.getDefault() != null) {
            Transition transition = state.getDefault().getTransition();
            if (transition != null && transition.getNextState() != null) {
                startNode.metaData(XORSPLITDEFAULT, concatId(splitId, stateConnection.get(transition
                        .getNextState()).getNode().getNode().getId()));
            } else if (state.getDefault().getEnd() != null) {
                EndNodeFactory<P> endNodeFactory = endNodeFactory(state.getDefault().getEnd().getProduceEvents());
                endNodeFactory.done().connection(splitId, endNodeFactory.getNode().getId());
                startNode.metaData(XORSPLITDEFAULT, concatId(splitId, endNodeFactory.getNode().getId()));
            }
        }

        List<DataCondition> conditions = state.getDataConditions();
        for (DataCondition condition : conditions) {
            Optional<Long> targetId = handleTransition(condition.getTransition(), splitId, stateConnection);
            if (!targetId.isPresent() && condition.getEnd() != null) {
                EndNodeFactory<P> endNodeFactory = endNodeFactory(condition.getEnd().getProduceEvents());
                endNodeFactory.done().connection(splitId, endNodeFactory.getNode().getId());
                targetId = Optional.of(endNodeFactory.getNode().getId());
            }
            if (targetId.isPresent()) {
                ((SplitFactory<?>) startNode).constraint(targetId.get(), concatId(splitId, targetId.get()),
                        "DROOLS_DEFAULT", "java", ServerlessWorkflowUtils.conditionScript(condition.getCondition()), 0,
                        isDefaultCondition(state, condition));
            } else {
                throw new IllegalArgumentException("Invalid condition, not transition not end");
            }
        }
    }

    private EndNodeFactory<P> endNodeFactory(List<ProduceEvent> produceEvents) {
        EndNodeFactory<P> endNodeFactory = factory.endNode(idGenerator.getId());
        if (produceEvents == null || produceEvents.isEmpty()) {
            endNodeFactory.terminate(true);
        } else {
            ServerlessWorkflowParser.sendEventNode(endNodeFactory, ServerlessWorkflowUtils.getWorkflowEventFor(workflow,
                    produceEvents.get(0).getEventRef()));
        }
        return endNodeFactory;
    }

    private static String concatId(long start, long end) {
        return start + "_" + end;
    }

    private static boolean isDefaultCondition(SwitchState switchState, DataCondition condition) {
        return switchState.getDefault() != null &&
                (switchState.getDefault().getTransition() != null &&
                        condition.getTransition() != null &&
                        condition.getTransition().getNextState().equals(switchState.getDefault().getTransition()
                                .getNextState())
                        || switchState.getDefault().getEnd() != null && condition.getEnd() != null);
    }

}
