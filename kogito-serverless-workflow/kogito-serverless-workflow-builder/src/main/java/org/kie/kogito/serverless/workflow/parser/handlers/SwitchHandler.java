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
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.workflow.core.node.Split;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.parser.util.ServerlessWorkflowUtils;

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

    private List<Runnable> targetHandlers = new ArrayList<>();

    protected SwitchHandler(SwitchState state, Workflow workflow, RuleFlowNodeContainerFactory<P, ?> factory,
            ParserContext parserContext) {
        super(state, workflow, factory, parserContext);
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

    @Override
    public SplitFactory<P> makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        long id = parserContext.newId();
        SplitFactory<P> splitFactory = (SplitFactory<P>) factory.splitNode(id).name(state
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
    protected <N extends RuleFlowNodeContainerFactory<N, ?>> void handleTransitions(RuleFlowNodeContainerFactory<N, ?> factory,
            Transition transition,
            long sourceId) {
        super.handleTransitions(factory, transition, sourceId);
        if (!state.getDataConditions().isEmpty()) {
            finalizeDataBasedSwitchState(getNode());
        } else {
            finalizeEventBasedSwitchState(getNode());
        }
    }

    private void finalizeEventBasedSwitchState(NodeFactory<?, ?> startNode) {
        List<EventCondition> conditions = state.getEventConditions();
        for (EventCondition eventCondition : conditions) {
            EventDefinition eventDefinition = ServerlessWorkflowUtils.getWorkflowEventFor(workflow,
                    eventCondition.getEventRef());
            StateHandler<?, ?, ?> targetState = parserContext.getStateHandler(eventCondition.getTransition());
            long eventId = parserContext.newId();
            ServerlessWorkflowParser.consumeEventNode(factory.eventNode(eventId), eventDefinition).done().connection(
                    startNode.getNode().getId(), eventId);
            targetState.connect(eventId);
        }
    }

    private void finalizeDataBasedSwitchState(NodeFactory<?, ?> startNode) {
        final long splitId = startNode.getNode().getId();
        // set default connection
        if (state.getDefault() != null) {
            Transition transition = state.getDefault().getTransition();
            StateHandler<?, ?, ?> stateHandler = parserContext.getStateHandler(transition);
            if (stateHandler != null) {
                startNode.metaData(XORSPLITDEFAULT, concatId(splitId, stateHandler.getNode().getNode().getId()));
            } else if (state.getDefault().getEnd() != null) {
                EndNodeFactory<P> endNodeFactory = endNodeFactory(state.getDefault().getEnd().getProduceEvents());
                endNodeFactory.done().connection(splitId, endNodeFactory.getNode().getId());
                startNode.metaData(XORSPLITDEFAULT, concatId(splitId, endNodeFactory.getNode().getId()));
            }
        }

        List<DataCondition> conditions = state.getDataConditions();
        for (DataCondition condition : conditions) {
            handleTransition(factory, condition.getTransition(), splitId, Optional.of(new StateHandler.HandleTransitionCallBack() {
                @Override
                public void onStateTarget(StateHandler<?, ?, ?> targetState) {
                    targetHandlers.add(() -> addConstraint(startNode, targetState, condition));
                }

                @Override
                public void onIdTarget(long targetId) {
                    addConstraint(startNode, targetId, condition);
                }

                @Override
                public void onEmptyTarget() {
                    if (condition.getEnd() != null) {
                        EndNodeFactory<P> endNodeFactory = endNodeFactory(condition.getEnd().getProduceEvents());
                        endNodeFactory.done().connection(splitId, endNodeFactory.getNode().getId());
                        addConstraint(startNode, endNodeFactory.getNode().getId(), condition);
                    } else {
                        throw new IllegalArgumentException("Invalid condition, not transition not end");
                    }
                }
            }));
        }
    }

    private void addConstraint(NodeFactory<?, ?> startNode, StateHandler<?, ?, ?> stateHandler, DataCondition condition) {
        addConstraint(startNode, stateHandler.getIncomingNode().getNode().getId(), condition);
    }

    private void addConstraint(NodeFactory<?, ?> startNode, long targetId, DataCondition condition) {
        ((SplitFactory<?>) startNode).constraintBuilder(targetId, concatId(startNode.getNode().getId(), targetId),
                "DROOLS_DEFAULT", workflow.getExpressionLang(), ServerlessWorkflowUtils.conditionScript(condition.getCondition())).withDefault(isDefaultCondition(state, condition))
                .metadata(Metadata.VARIABLE, ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR);
    }

    private EndNodeFactory<P> endNodeFactory(List<ProduceEvent> produceEvents) {
        EndNodeFactory<P> endNodeFactory = factory.endNode(parserContext.newId());
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

    @Override
    public void connect(long sourceId) {
        factory.connection(sourceId, getNode().getNode().getId());
    }

    @Override
    public void handleConnections() {
        targetHandlers.forEach(Runnable::run);
    }
}
