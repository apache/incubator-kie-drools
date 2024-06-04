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

import java.util.List;
import java.util.function.BiFunction;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.OnEvents;
import io.serverlessworkflow.api.states.EventState;

import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.startMessageNode;

public class EventHandler extends CompositeContextNodeHandler<EventState> {

    protected EventHandler(EventState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public void handleStart() {
        // disable standard procedure
    }

    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        if (isStartState) {
            return joinNodes(factory, state.getOnEvents(), this::processOnEvent);
        } else {
            CompositeContextNodeFactory<?> embeddedContainer = makeCompositeNode(factory);
            connect(connect(embeddedContainer.startNode(parserContext.newId()).name("EmbeddedStart"),
                    makeTimeoutNode(embeddedContainer, joinNodes(embeddedContainer, state.getOnEvents(), this::processOnEvent))),
                    embeddedContainer.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
            handleErrors(factory, embeddedContainer);
            return new MakeNodeResult(embeddedContainer);
        }

    }

    private MakeNodeResult processOnEvent(RuleFlowNodeContainerFactory<?, ?> factory, OnEvents onEvent) {
        MakeNodeResult result = joinNodes(factory,
                onEvent.getEventRefs(), (fact, onEventRef) -> filterAndMergeNode(fact, onEvent.getEventDataFilter(), getVarName(),
                        (f, inputVar, outputVar) -> buildEventNode(f, onEventRef, inputVar, outputVar)));
        CompositeContextNodeFactory<?> embeddedSubProcess = handleActions(makeCompositeNode(factory), onEvent.getActions());
        connect(result.getOutgoingNode(), embeddedSubProcess);
        return new MakeNodeResult(result.getIncomingNode(), embeddedSubProcess);
    }

    private <T> MakeNodeResult joinNodes(RuleFlowNodeContainerFactory<?, ?> factory, List<T> events, BiFunction<RuleFlowNodeContainerFactory<?, ?>, T, MakeNodeResult> function) {
        if (events.size() == 1) {
            return function.apply(factory, events.get(0));
        } else {
            final int splitType;
            final int joinType;
            if (state.isExclusive()) {
                splitType = Split.TYPE_XAND;
                joinType = Join.TYPE_XOR;
            } else {
                splitType = Split.TYPE_AND;
                joinType = Join.TYPE_AND;
            }
            if (isStartState) {
                JoinFactory<?> joinFactory = joinFactory(factory, joinType);
                for (T event : events) {
                    connect(function.apply(factory, event).getOutgoingNode(), joinFactory);
                }
                return new MakeNodeResult(joinFactory);
            } else {
                CompositeContextNodeFactory<?> compositeNode = makeCompositeNode(factory);
                SplitFactory<?> splitFactory = compositeNode.splitNode(parserContext.newId()).name(state.getName() + "Split").type(splitType);
                connect(compositeNode.startNode(parserContext.newId()), splitFactory);
                JoinFactory<?> joinFactory = joinFactory(compositeNode, joinType);
                for (T event : events) {
                    MakeNodeResult node = function.apply(compositeNode, event);
                    connect(splitFactory, node.getIncomingNode());
                    connect(node.getOutgoingNode(), joinFactory);
                }
                connect(joinFactory, compositeNode.endNode(parserContext.newId()));
                return new MakeNodeResult(compositeNode);
            }
        }
    }

    private JoinFactory<?> joinFactory(RuleFlowNodeContainerFactory<?, ?> factory, int joinType) {
        return factory.joinNode(parserContext.newId()).name(state.getName() + "Join").type(joinType);
    }

    private NodeFactory<?, ?> buildEventNode(RuleFlowNodeContainerFactory<?, ?> factory, String eventRef, String inputVar, String outputVar) {
        return isStartState ? messageStartNode(factory, eventRef, inputVar, outputVar) : consumeEventNode(factory, eventRef, inputVar, outputVar);
    }

    private StartNodeFactory<?> messageStartNode(RuleFlowNodeContainerFactory<?, ?> factory, String eventRef, String inputVar, String outputVar) {
        return startMessageNode(factory.startNode(parserContext.newId()), eventDefinition(eventRef), inputVar, outputVar);
    }
}
