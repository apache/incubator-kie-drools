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
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.defaultdef.DefaultConditionDefinition;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.states.SwitchState;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.serverlessworkflow.api.switchconditions.EventCondition;
import io.serverlessworkflow.api.transitions.Transition;

import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.eventBasedExclusiveSplitNode;
import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.exclusiveSplitNode;
import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.timerNode;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.validateConditions;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.validateDefaultCondition;
import static org.kie.kogito.serverless.workflow.utils.TimeoutsConfigResolver.resolveEventTimeout;

public class SwitchHandler extends StateHandler<SwitchState> {

    private List<Runnable> targetHandlers = new ArrayList<>();

    protected SwitchHandler(SwitchState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

    @Override
    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        validateConditions(state, workflow);
        SplitFactory<?> splitNode = factory.splitNode(parserContext.newId());
        splitNode = isDataBased() ? exclusiveSplitNode(splitNode) : eventBasedExclusiveSplitNode(splitNode);
        return new MakeNodeResult(splitNode.name(state.getName()));
    }

    @Override
    protected void handleTransitions(RuleFlowNodeContainerFactory<?, ?> factory,
            Transition transition,
            NodeFactory<?, ?> sourceNode) {
        super.handleTransitions(factory, transition, sourceNode);
        if (isDataBased()) {
            finalizeDataBasedSwitchState(factory);
        } else {
            finalizeEventBasedSwitchState(factory);
        }
    }

    private boolean isDataBased() {
        return !state.getDataConditions().isEmpty();
    }

    private void finalizeEventBasedSwitchState(RuleFlowNodeContainerFactory<?, ?> factory) {
        NodeFactory<?, ?> splitNode = getNode();
        List<EventCondition> conditions = state.getEventConditions();
        DefaultConditionDefinition defaultCondition = state.getDefaultCondition();
        if (defaultCondition != null) {
            validateDefaultCondition(defaultCondition, state, workflow, parserContext);
            // Create the timer for controlling the eventTimeout and connect it with the exclusive split.
            NodeFactory<?, ?> eventTimeoutTimerNode = connect(splitNode, timerNode(factory.timerNode(parserContext.newId()), resolveEventTimeout(state, workflow)));
            handleTransition(factory, defaultCondition.getTransition(), eventTimeoutTimerNode, Optional.of(new StateHandler.HandleTransitionCallBack() {
                @Override
                public void onEmptyTarget() {
                    // Connect the timer with a process finalization sequence that might produce events.
                    endIt(eventTimeoutTimerNode.getNode().getId(), factory, defaultCondition.getEnd());
                }
            }));
        }
        // Process the event conditions.
        for (EventCondition eventCondition : conditions) {
            NodeFactory<?, ?> outNode = connect(splitNode,
                    filterAndMergeNode(factory, eventCondition.getEventDataFilter(), (f, inputVar, outputVar) -> consumeEventNode(f, eventCondition.getEventRef(), inputVar, outputVar)));
            handleTransition(factory, eventCondition.getTransition(), outNode, Optional.of(new StateHandler.HandleTransitionCallBack() {
                @Override
                public void onEmptyTarget() {
                    // Connect the timer with a process finalization sequence that might produce events.
                    endIt(outNode.getNode().getId(), factory, eventCondition.getEnd());
                }
            }));
        }
    }

    private void finalizeDataBasedSwitchState(RuleFlowNodeContainerFactory<?, ?> factory) {
        final NodeFactory<?, ?> startNode = getNode();
        final WorkflowElementIdentifier splitId = startNode.getNode().getId();

        DefaultConditionDefinition defaultCondition = state.getDefaultCondition();
        // set default connection
        if (defaultCondition != null) {
            validateDefaultCondition(defaultCondition, state, workflow, parserContext);
            handleTransition(factory, defaultCondition.getTransition(), startNode, Optional.of(new StateHandler.HandleTransitionCallBack() {
                @Override
                public void onStateTarget(StateHandler<?> targetState) {
                    targetHandlers.add(() -> startNode.metaData(XORSPLITDEFAULT, concatId(splitId, targetState.getIncomingNode(factory).getNode().getId()).toExternalFormat()));
                }

                @Override
                public void onIdTarget(WorkflowElementIdentifier targetId) {
                    startNode.metaData(XORSPLITDEFAULT, concatId(splitId, targetId).toExternalFormat());
                }

                @Override
                public void onEmptyTarget() {
                    NodeFactory<?, ?> endNodeFactory = endIt(splitId, factory, defaultCondition.getEnd());
                    startNode.metaData(XORSPLITDEFAULT, concatId(splitId, endNodeFactory.getNode().getId()).toExternalFormat());
                }
            }));
        }

        List<DataCondition> conditions = state.getDataConditions();
        for (DataCondition condition : conditions) {
            handleTransition(factory, condition.getTransition(), startNode, Optional.of(new StateHandler.HandleTransitionCallBack() {
                @Override
                public void onStateTarget(StateHandler<?> targetState) {
                    targetHandlers.add(() -> addConstraint(factory, startNode, targetState, condition));
                }

                @Override
                public void onIdTarget(WorkflowElementIdentifier targetId) {
                    addConstraint(startNode, targetId, condition);
                }

                @Override
                public void onEmptyTarget() {
                    NodeFactory<?, ?> endNodeFactory = endIt(splitId, factory, condition.getEnd());
                    addConstraint(startNode, endNodeFactory.getNode().getId(), condition);
                }
            }));
        }
    }

    private NodeFactory<?, ?> endIt(WorkflowElementIdentifier sourceNodeId, RuleFlowNodeContainerFactory<?, ?> factory, End end) {
        NodeFactory<?, ?> endNodeFactory = endNodeFactory(factory, end);
        endNodeFactory.done().connection(sourceNodeId, endNodeFactory.getNode().getId());
        return endNodeFactory;
    }

    private void addConstraint(RuleFlowNodeContainerFactory<?, ?> factory, NodeFactory<?, ?> startNode, StateHandler<?> stateHandler, DataCondition condition) {
        addConstraint(startNode, stateHandler.getIncomingNode(factory).getNode().getId(), condition);
    }

    private void addConstraint(NodeFactory<?, ?> startNode, WorkflowElementIdentifier targetId, DataCondition condition) {
        addCondition((SplitFactory<?>) startNode, targetId, condition.getCondition(), isDefaultCondition(state, condition));
    }

    private static boolean isDefaultCondition(SwitchState switchState, DataCondition condition) {
        return switchState.getDefaultCondition() != null &&
                (switchState.getDefaultCondition().getTransition() != null &&
                        condition.getTransition() != null &&
                        condition.getTransition().getNextState().equals(switchState.getDefaultCondition().getTransition()
                                .getNextState())
                        || switchState.getDefaultCondition().getEnd() != null && condition.getEnd() != null);
    }

    @Override
    public void handleConnections() {
        super.handleConnections();
        targetHandlers.forEach(Runnable::run);
    }
}
