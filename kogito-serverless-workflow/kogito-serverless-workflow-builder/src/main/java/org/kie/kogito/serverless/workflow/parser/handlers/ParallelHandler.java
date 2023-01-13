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

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionReturnValueEvaluatorSupplier;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.branches.Branch;
import io.serverlessworkflow.api.states.ParallelState;
import io.serverlessworkflow.api.states.ParallelState.CompletionType;

public class ParallelHandler extends CompositeContextNodeHandler<ParallelState> {

    protected ParallelHandler(ParallelState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        SplitFactory<?> nodeFactory = factory.splitNode(parserContext.newId()).name(state.getName() + ServerlessWorkflowParser.NODE_START_NAME).type(Split.TYPE_AND);
        JoinFactory<?> connectionNode = factory.joinNode(parserContext.newId()).name(state.getName() + ServerlessWorkflowParser.NODE_END_NAME);
        CompletionType completionType = state.getCompletionType();
        if (completionType == CompletionType.ALL_OF) {
            connectionNode.type(Join.TYPE_AND);
        } else if (completionType == CompletionType.AT_LEAST) {
            String numCompleted = state.getNumCompleted();
            connectionNode.type(Join.TYPE_N_OF_M);
            connectionNode.type(numCompleted);
            if (ExpressionHandlerFactory.get(workflow.getExpressionLang(), numCompleted).isValid()) {
                connectionNode.metaData(Metadata.ACTION,
                        new ExpressionReturnValueEvaluatorSupplier(workflow.getExpressionLang(), state.getNumCompleted(), SWFConstants.DEFAULT_WORKFLOW_VAR, Integer.class));
            }
        }
        for (Branch branch : state.getBranches()) {
            CompositeContextNodeFactory<?> embeddedSubProcess = handleActions(makeCompositeNode(factory, getName(branch)), branch.getActions());
            long branchId = embeddedSubProcess.getNode().getId();
            embeddedSubProcess.done().connection(nodeFactory.getNode().getId(), branchId).connection(branchId, connectionNode.getNode().getId());
        }
        return new MakeNodeResult(nodeFactory, connectionNode);
    }

    private String getName(Branch branch) {
        StringBuilder sb = new StringBuilder(state.getName());
        // when branch name will be made mandatory (0.9), this if can be removed
        String branchName = branch.getName();
        if (branchName != null && !branchName.isBlank()) {
            sb.append('-').append(branchName);
        }
        return sb.toString();
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }
}
