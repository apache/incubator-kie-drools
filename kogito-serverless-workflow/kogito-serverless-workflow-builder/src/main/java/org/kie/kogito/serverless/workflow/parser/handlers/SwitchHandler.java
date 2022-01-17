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
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.produce.ProduceEvent;
import io.serverlessworkflow.api.states.SwitchState;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.serverlessworkflow.api.switchconditions.EventCondition;
import io.serverlessworkflow.api.transitions.Transition;

import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR;

public class SwitchHandler extends StateHandler<SwitchState> {

    private static final String XORSPLITDEFAULT = "Default";

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
        long id = parserContext.newId();
        SplitFactory<?> splitFactory = factory.splitNode(id).name(state.getName());
        // check if data-based or event-based switch state
        if (!state.getDataConditions().isEmpty()) {
            splitFactory.type(Split.TYPE_XOR);
        } else {
            splitFactory.type(Split.TYPE_XAND);
            splitFactory.metaData("UniqueId", Long.toString(id));
            splitFactory.metaData("EventBased", "true");
        }
        return new MakeNodeResult(splitFactory);
    }

    @Override
    protected void handleTransitions(RuleFlowNodeContainerFactory<?, ?> factory,
            Transition transition,
            long sourceId) {
        super.handleTransitions(factory, transition, sourceId);
        if (!state.getDataConditions().isEmpty()) {
            finalizeDataBasedSwitchState(factory);
        } else {
            finalizeEventBasedSwitchState(factory);
        }
    }

    private void finalizeEventBasedSwitchState(RuleFlowNodeContainerFactory<?, ?> factory) {
        List<EventCondition> conditions = state.getEventConditions();
        for (EventCondition eventCondition : conditions) {
            StateHandler<?> targetState = parserContext.getStateHandler(eventCondition.getTransition());
            MakeNodeResult eventNode =
                    filterAndMergeNode(factory, eventCondition.getEventDataFilter(), (f, inputVar, outputVar) -> consumeEventNode(f, eventCondition.getEventRef(), inputVar, outputVar));
            factory.connection(getNode().getNode().getId(), eventNode.getIncomingNode().getNode().getId());
            targetState.connect(factory, eventNode.getOutgoingNode().getNode().getId());
        }
    }

    private void finalizeDataBasedSwitchState(RuleFlowNodeContainerFactory<?, ?> factory) {
        final NodeFactory<?, ?> startNode = getNode();
        final long splitId = startNode.getNode().getId();
        // set default connection
        if (state.getDefault() != null) {
            Transition transition = state.getDefault().getTransition();
            StateHandler<?> stateHandler = parserContext.getStateHandler(transition);
            if (stateHandler != null) {
                startNode.metaData(XORSPLITDEFAULT, concatId(splitId, stateHandler.getNode().getNode().getId()));
            } else if (state.getDefault().getEnd() != null) {
                EndNodeFactory<?> endNodeFactory = endIt(splitId, factory, state.getDefault().getEnd().getProduceEvents());
                startNode.metaData(XORSPLITDEFAULT, concatId(splitId, endNodeFactory.getNode().getId()));
            }
        }

        List<DataCondition> conditions = state.getDataConditions();
        for (DataCondition condition : conditions) {
            handleTransition(factory, condition.getTransition(), splitId, Optional.of(new StateHandler.HandleTransitionCallBack() {
                @Override
                public void onStateTarget(StateHandler<?> targetState) {
                    targetHandlers.add(() -> addConstraint(factory, startNode, targetState, condition));
                }

                @Override
                public void onIdTarget(long targetId) {
                    addConstraint(startNode, targetId, condition);
                }

                @Override
                public void onEmptyTarget() {
                    if (condition.getEnd() != null) {
                        EndNodeFactory<?> endNodeFactory = endIt(splitId, factory, condition.getEnd().getProduceEvents());
                        addConstraint(startNode, endNodeFactory.getNode().getId(), condition);
                    } else {
                        throw new IllegalArgumentException("Invalid condition, not transition not end");
                    }
                }
            }));
        }
    }

    private EndNodeFactory<?> endIt(long sourceNodeId, RuleFlowNodeContainerFactory<?, ?> factory, List<ProduceEvent> produceEvents) {
        EndNodeFactory<?> endNodeFactory = endNodeFactory(factory, produceEvents);
        endNodeFactory.done().connection(sourceNodeId, endNodeFactory.getNode().getId());
        return endNodeFactory;
    }

    private void addConstraint(RuleFlowNodeContainerFactory<?, ?> factory, NodeFactory<?, ?> startNode, StateHandler<?> stateHandler, DataCondition condition) {
        addConstraint(startNode, stateHandler.getIncomingNode(factory).getNode().getId(), condition);
    }

    private void addConstraint(NodeFactory<?, ?> startNode, long targetId, DataCondition condition) {
        ((SplitFactory<?>) startNode).constraintBuilder(targetId, concatId(startNode.getNode().getId(), targetId),
                "DROOLS_DEFAULT", workflow.getExpressionLang(), ServerlessWorkflowUtils.conditionScript(condition.getCondition())).withDefault(isDefaultCondition(state, condition))
                .metadata(Metadata.VARIABLE, DEFAULT_WORKFLOW_VAR);
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
    public void connect(RuleFlowNodeContainerFactory<?, ?> factory, long sourceId) {
        factory.connection(sourceId, getNode().getNode().getId());
    }

    @Override
    public void handleConnections() {
        targetHandlers.forEach(Runnable::run);
    }
}
