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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.AbstractCompositeNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.suppliers.CloneVariableActionSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionReturnValueEvaluatorSupplier;
import org.kie.kogito.serverless.workflow.suppliers.MergeActionSupplier;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.branches.Branch;
import io.serverlessworkflow.api.states.ParallelState;
import io.serverlessworkflow.api.states.ParallelState.CompletionType;

import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR;

public class ParallelHandler extends CompositeContextNodeHandler<ParallelState> {

    protected ParallelHandler(ParallelState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    private Branch currentBranch;

    @Override
    public MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        SplitFactory<?> splitFactory = factory.splitNode(parserContext.newId()).name(state.getName() + ServerlessWorkflowParser.NODE_START_NAME).type(Split.TYPE_AND);
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
                        new ExpressionReturnValueEvaluatorSupplier(workflow.getExpressionLang(), state.getNumCompleted(), DEFAULT_WORKFLOW_VAR, Integer.class));
            }
        }

        Set<String> branchVariables = new HashSet<>();
        for (Branch branch : state.getBranches()) {
            currentBranch = branch;
            String branchVarName = getVarName();
            branchVariables.add(branchVarName);
            CompositeContextNodeFactory<?> embeddedSubProcess =
                    handleActions(makeCompositeNode(factory, getName(branch)), branch.getActions(), null, true, branchVarName);
            handleErrors(factory, embeddedSubProcess);
            WorkflowElementIdentifier branchId = embeddedSubProcess.getNode().getId();
            embeddedSubProcess.done().connection(splitFactory.getNode().getId(), branchId).connection(branchId, connectionNode.getNode().getId());
        }

        Iterator<String> iter = branchVariables.iterator();
        NodeFactory<?, ?> startNode;
        if (iter.hasNext()) {
            startNode = factory.actionNode(parserContext.newId())
                    .action(new CloneVariableActionSupplier(DEFAULT_WORKFLOW_VAR, iter.next()));
            NodeFactory<?, ?> currentNode = startNode;
            while (iter.hasNext()) {
                currentNode = connect(currentNode, factory.actionNode(parserContext.newId())
                        .action(new CloneVariableActionSupplier(DEFAULT_WORKFLOW_VAR, iter.next())));
            }
            connect(currentNode, splitFactory);
        } else {
            startNode = splitFactory;
        }
        return new MakeNodeResult(startNode, connectionNode);
    }

    @Override
    protected <T extends AbstractCompositeNodeFactory<?, ?>> NodeFactory<?, ?> handleActions(T embeddedSubProcess, NodeFactory<?, ?> currentNode, List<Action> actions, String outputVar,
            boolean shouldMerge, String modelVar) {
        currentNode = super.handleActions(embeddedSubProcess, currentNode, actions, outputVar, shouldMerge, modelVar);
        return connect(currentNode, embeddedSubProcess.actionNode(parserContext.newId())
                .action(new MergeActionSupplier(modelVar, DEFAULT_WORKFLOW_VAR)));
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
    protected NodeFactory<?, ?> addActionMetadata(NodeFactory<?, ?> node, Action action) {
        node = super.addActionMetadata(node, action);
        if (currentBranch != null && currentBranch.getName() != null) {
            node.metaData(SWFConstants.BRANCH_NAME, currentBranch.getName());
        }
        return node;
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }
}
