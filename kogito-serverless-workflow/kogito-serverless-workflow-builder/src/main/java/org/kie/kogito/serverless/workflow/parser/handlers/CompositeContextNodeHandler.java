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
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.mvel.java.JavaDialect;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.AbstractCompositeNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.node.Join;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.FunctionNamespaceFactory;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.utils.JsonNodeContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.events.EventRef;
import io.serverlessworkflow.api.filters.ActionDataFilter;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.functions.SubFlowRef;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.sleep.Sleep;
import io.serverlessworkflow.api.workflow.Functions;

import static org.kie.kogito.internal.utils.ConversionUtils.isEmpty;
import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.exclusiveSplitNode;
import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.subprocessNode;
import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.timerNode;

public abstract class CompositeContextNodeHandler<S extends State> extends StateHandler<S> {

    protected CompositeContextNodeHandler(S state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    protected final CompositeContextNodeFactory<?> makeCompositeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        return makeCompositeNode(factory, state.getName());
    }

    protected final CompositeContextNodeFactory<?> makeCompositeNode(RuleFlowNodeContainerFactory<?, ?> factory, String nodeName) {
        return factory.compositeContextNode(parserContext.newId()).name(nodeName).autoComplete(true);
    }

    protected final <T extends AbstractCompositeNodeFactory<?, ?>> T handleActions(T embeddedSubProcess, List<Action> actions) {
        return handleActions(embeddedSubProcess, actions, null, true);
    }

    protected final <T extends AbstractCompositeNodeFactory<?, ?>> T handleActions(T embeddedSubProcess, List<Action> actions, String outputVar, boolean shouldMerge) {
        if (actions != null && !actions.isEmpty()) {
            NodeFactory<?, ?> startNode = embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart");
            NodeFactory<?, ?> currentNode = startNode;
            for (Action action : actions) {
                currentNode = connect(currentNode, getActionNode(embeddedSubProcess, action, outputVar != null ? outputVar : getVarName(), shouldMerge));
            }
            connect(currentNode, embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        } else {
            connect(embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart"), embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        }
        return embeddedSubProcess;
    }

    protected final MakeNodeResult getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action) {
        return getActionNode(embeddedSubProcess, action, getVarName(), true);
    }

    protected final MakeNodeResult getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action, String collectVar, boolean shouldMerge) {
        return addActionCondition(embeddedSubProcess, action, addActionSleep(embeddedSubProcess, action, processActionFilter(embeddedSubProcess, action, collectVar, shouldMerge)));
    }

    private MakeNodeResult addActionCondition(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, Action action, MakeNodeResult actionNode) {
        String condition = action.getCondition();
        if (condition == null) {
            return actionNode;
        }
        String actionName = action.getName();
        SplitFactory<?> start = addCondition(exclusiveSplitNode(embeddedSubProcess.splitNode(parserContext.newId())), actionNode.getIncomingNode(), condition, false);
        if (actionName != null) {
            start.name("Split_" + actionName);
        }
        connect(start, actionNode.getIncomingNode());
        NodeFactory<?, ?> end = connect(actionNode.getOutgoingNode(), embeddedSubProcess.joinNode(parserContext.newId()).type(Join.TYPE_OR));
        if (actionName != null) {
            end.name("Join_" + actionName);
        }
        connect(start, end);
        start.metaData(XORSPLITDEFAULT, concatId(start.getNode().getId(), end.getNode().getId()).toExternalFormat());
        return new MakeNodeResult(start, end);
    }

    private MakeNodeResult addActionSleep(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action, MakeNodeResult actionNode) {
        Sleep sleep = action.getSleep();
        if (sleep != null) {
            if (!isEmpty(sleep.getBefore())) {
                NodeFactory<?, ?> beforeNode = createTimerNode(embeddedSubProcess, sleep.getBefore());
                connect(beforeNode, actionNode.getIncomingNode());
                return !isEmpty(sleep.getAfter()) ? new MakeNodeResult(beforeNode,
                        connect(actionNode.getOutgoingNode(), createTimerNode(embeddedSubProcess, sleep.getAfter()))) : new MakeNodeResult(beforeNode, actionNode.getOutgoingNode());
            } else if (!isEmpty(sleep.getAfter())) {
                return new MakeNodeResult(actionNode.getIncomingNode(), connect(actionNode.getOutgoingNode(), createTimerNode(embeddedSubProcess, sleep.getAfter())));
            }
        }
        return actionNode;
    }

    @SuppressWarnings("squid:S1452")
    protected NodeFactory<?, ?> addActionMetadata(NodeFactory<?, ?> node, Action action) {
        String actionName = action.getName();
        if (actionName != null) {
            node.metaData(SWFConstants.ACTION_NAME, actionName);
        }
        return node.metaData(SWFConstants.STATE_NAME, state.getName());
    }

    private MakeNodeResult processActionFilter(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action, String collectVar, boolean shouldMerge) {
        ActionDataFilter actionFilter = action.getActionDataFilter();
        String fromExpr = null;
        String resultExpr = null;
        String toExpr = null;
        boolean useData = true;
        if (actionFilter != null) {
            fromExpr = actionFilter.getFromStateData();
            resultExpr = actionFilter.getResults();
            toExpr = actionFilter.getToStateData();
            useData = actionFilter.isUseResults();
        }
        if (action.getFunctionRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, collectVar, fromExpr, resultExpr, toExpr, useData, shouldMerge,
                    (factory, inputVar, outputVar) -> addActionMetadata(getActionNode(factory, action.getFunctionRef(), inputVar, outputVar), action));
        } else if (action.getEventRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, collectVar, fromExpr, resultExpr, toExpr, useData, shouldMerge,
                    (factory, inputVar, outputVar) -> addActionMetadata(getActionNode(factory, action.getEventRef(), inputVar), action));
        } else if (action.getSubFlowRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, collectVar, fromExpr, resultExpr, toExpr, useData, shouldMerge,
                    (factory, inputVar, outputVar) -> addActionMetadata(getActionNode(factory, action.getSubFlowRef(), inputVar, outputVar), action));
        } else {
            throw new IllegalArgumentException("Action node " + action.getName() + " of state " + state.getName() + " does not have function or event defined");
        }
    }

    private TimerNodeFactory<?> createTimerNode(RuleFlowNodeContainerFactory<?, ?> factory, String duration) {
        return timerNode(factory.timerNode(parserContext.newId()), duration);
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> factory,
            SubFlowRef subFlowRef,
            String inputVar,
            String outputVar) {
        SubProcessNodeFactory<?> subProcessNode = subprocessNode(
                factory.subProcessNode(parserContext.newId()).name(subFlowRef.getWorkflowId()).processId(subFlowRef.getWorkflowId()).waitForCompletion(true),
                inputVar,
                outputVar);
        JsonNodeContext.getEvalVariables(factory.getNode()).forEach(v -> subProcessNode.inMapping(v.getName(), v.getName()));
        return subProcessNode;
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            EventRef eventRef, String inputVar) {
        return sendEventNode(embeddedSubProcess.actionNode(parserContext.newId()), eventDefinition(eventRef.getTriggerEventRef()), eventRef.getData(), inputVar);
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionRef functionRef, String inputVar, String outputVar) {
        String functionName = functionRef.getRefName();
        VariableInfo varInfo = new VariableInfo(inputVar, outputVar);
        return getFunctionDefStream()
                .filter(wf -> wf.getName().equals(functionName))
                .findFirst()
                .map(functionDef -> fromFunctionDefinition(embeddedSubProcess, functionDef, functionRef, varInfo))
                .or(() -> fromPredefinedFunction(embeddedSubProcess, functionRef, varInfo))
                .orElseThrow(() -> new IllegalArgumentException("Cannot find function " + functionName));
    }

    private Stream<FunctionDefinition> getFunctionDefStream() {
        Functions functions = workflow.getFunctions();
        return functions == null ? Stream.empty() : workflow.getFunctions().getFunctionDefs().stream();
    }

    private NodeFactory fromFunctionDefinition(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionDefinition functionDef,
            FunctionRef functionRef, VariableInfo varInfo) {
        return FunctionTypeHandlerFactory.instance().getTypeHandler(functionDef)
                .map(type -> type.getActionNode(workflow, parserContext, embeddedSubProcess, functionDef, functionRef, varInfo))
                .orElseGet(() -> (NodeFactory) embeddedSubProcess.actionNode(parserContext.newId()).name(functionRef.getRefName()).action(JavaDialect.ID, ""));
    }

    private Optional<NodeFactory> fromPredefinedFunction(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionRef functionRef, VariableInfo varInfo) {
        return FunctionNamespaceFactory.instance().getNamespace(functionRef).map(f -> f.getActionNode(workflow, parserContext, embeddedSubProcess, functionRef, varInfo));
    }
}
