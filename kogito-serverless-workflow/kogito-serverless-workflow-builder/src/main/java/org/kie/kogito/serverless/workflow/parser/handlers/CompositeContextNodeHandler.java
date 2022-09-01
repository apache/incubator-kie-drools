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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.mvel.java.JavaDialect;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.AbstractCompositeNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.kie.kogito.serverless.workflow.parser.FunctionNamespaceFactory;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.events.EventRef;
import io.serverlessworkflow.api.filters.ActionDataFilter;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.functions.SubFlowRef;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.workflow.Functions;

import static org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils.subprocessNode;

public abstract class CompositeContextNodeHandler<S extends State> extends StateHandler<S> {

    protected CompositeContextNodeHandler(S state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    protected final CompositeContextNodeFactory<?> makeCompositeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        return factory.compositeContextNode(parserContext.newId()).name(state.getName()).autoComplete(true);
    }

    protected final <T extends AbstractCompositeNodeFactory<?, ?>> T handleActions(T embeddedSubProcess, List<Action> actions) {
        return handleActions(embeddedSubProcess, actions, null);
    }

    protected final <T extends AbstractCompositeNodeFactory<?, ?>> T handleActions(T embeddedSubProcess, List<Action> actions, String outputVar, String... extraVariables) {
        if (actions != null && !actions.isEmpty()) {
            NodeFactory<?, ?> startNode = embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart");
            NodeFactory<?, ?> currentNode = startNode;
            for (Action action : actions) {
                currentNode = connect(currentNode, getActionNode(embeddedSubProcess, action, outputVar, extraVariables));
            }
            connect(currentNode, embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        } else {
            connect(embeddedSubProcess.startNode(parserContext.newId()).name("EmbeddedStart"), embeddedSubProcess.endNode(parserContext.newId()).name("EmbeddedEnd").terminate(true)).done();
        }
        handleErrors(parserContext.factory(), embeddedSubProcess);
        return embeddedSubProcess;
    }

    protected final MakeNodeResult getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action) {
        return getActionNode(embeddedSubProcess, action, null);
    }

    public MakeNodeResult getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            Action action, String collectVar, String... extraVariables) {
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
            return filterAndMergeNode(embeddedSubProcess, fromExpr, resultExpr, toExpr, useData,
                    (factory, inputVar, outputVar) -> getActionNode(factory, action.getFunctionRef(), inputVar, outputVar, collectVar, extraVariables));
        } else if (action.getEventRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, fromExpr, resultExpr, toExpr, useData,
                    (factory, inputVar, outputVar) -> getActionNode(factory, action.getEventRef(), inputVar));
        } else if (action.getSubFlowRef() != null) {
            return filterAndMergeNode(embeddedSubProcess, fromExpr, resultExpr, toExpr, useData,
                    (factory, inputVar, outputVar) -> getActionNode(factory, action.getSubFlowRef(), inputVar, outputVar));
        } else {
            throw new IllegalArgumentException("Action node " + action.getName() + " of state " + state.getName() + " does not have function or event defined");
        }
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> factory,
            SubFlowRef subFlowRef,
            String inputVar,
            String outputVar) {
        return subprocessNode(
                factory.subProcessNode(parserContext.newId()).name(subFlowRef.getWorkflowId()).processId(subFlowRef.getWorkflowId()).waitForCompletion(true),
                inputVar,
                outputVar);
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            EventRef eventRef, String inputVar) {
        return sendEventNode(embeddedSubProcess.actionNode(parserContext.newId()), eventDefinition(eventRef.getTriggerEventRef()), eventRef.getData(), inputVar);
    }

    private NodeFactory<?, ?> getActionNode(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionRef functionRef, String inputVar, String outputVar, String collectVar, String... extraVariables) {
        String functionName = functionRef.getRefName();
        VariableInfo varInfo = new VariableInfo(inputVar, outputVar, collectVar, extraVariables);
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
