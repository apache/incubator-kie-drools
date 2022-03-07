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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.CallbackState;

import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.eventBasedExclusiveSplitNode;
import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.joinExclusiveNode;
import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.timerNode;
import static org.kie.kogito.serverless.workflow.utils.TimeoutsConfigResolver.resolveEventTimeout;

public class CallbackHandler extends CompositeContextNodeHandler<CallbackState> {

    protected CallbackHandler(CallbackState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

    @Override
    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        CompositeContextNodeFactory<?> embeddedSubProcess = factory.compositeContextNode(parserContext.newId()).name(state.getName()).autoComplete(true);
        NodeFactory<?, ?> currentNode = embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart");
        if (state.getAction() != null) {
            currentNode = connect(currentNode, getActionNode(embeddedSubProcess, state.getAction()));
        }
        String eventTimeout = resolveEventTimeout(state, workflow);
        if (eventTimeout != null) {
            // Create the event based exclusive split node.
            SplitFactory<?> splitNode = eventBasedExclusiveSplitNode(embeddedSubProcess.splitNode(parserContext.newId()));
            // Create the join node for joining the event fired and the timer fired branches.
            JoinFactory<?> joinNode = joinExclusiveNode(embeddedSubProcess.joinNode(parserContext.newId()));
            // Connect the currentNode with the split
            connect(currentNode, splitNode);
            // Create the event fired branch, normal path if the event arrives in time.
            NodeFactory<?, ?> eventFiredBranchLastNode = connect(splitNode,
                    filterAndMergeNode(embeddedSubProcess, state.getEventDataFilter(), (f, inputVar, outputVar) -> consumeEventNode(f, state.getEventRef(), inputVar, outputVar)));
            // Connect the event fired branch last node with the join node.
            connect(eventFiredBranchLastNode, joinNode);
            // Create the timer fired branch
            TimerNodeFactory<?> eventTimeoutTimerNode = timerNode(embeddedSubProcess.timerNode(parserContext.newId()), eventTimeout);
            connect(splitNode, eventTimeoutTimerNode);
            // Connect the timer fired branch last node with the join node
            currentNode = connect(eventTimeoutTimerNode, joinNode);
        } else {
            // No timeouts, standard case.
            currentNode = connect(currentNode,
                    filterAndMergeNode(embeddedSubProcess, state.getEventDataFilter(), (f, inputVar, outputVar) -> consumeEventNode(f, state.getEventRef(), inputVar, outputVar)));

        }
        // Finally, connect with the End event
        connect(currentNode, embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        return new MakeNodeResult(embeddedSubProcess);
    }
}
