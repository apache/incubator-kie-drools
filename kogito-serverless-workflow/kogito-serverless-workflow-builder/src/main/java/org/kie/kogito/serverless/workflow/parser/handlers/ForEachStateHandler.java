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

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ForEachNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.suppliers.ForEachCollectorActionSupplier;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.ForEachState;

public class ForEachStateHandler extends CompositeContextNodeHandler<ForEachState> {

    private final String outputVarName;

    protected ForEachStateHandler(ForEachState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
        outputVarName =
                workflowAppContext.getApplicationProperty(ServerlessWorkflowUtils.APP_PROPERTIES_BASE + ServerlessWorkflowUtils.APP_PROPERTIES_STATES_BASE + "foreach.outputVarName", "_swf_eval_temp");
    }

    @Override
    protected ForEachNodeFactory<?> makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        ForEachNodeFactory<?> result =
                factory.forEachNode(parserContext.newId()).sequential(false).waitForCompletion(true).expressionLanguage(workflow.getExpressionLang()).collectionExpression(state.getInputCollection())
                        .outputVariable(outputVarName, new ObjectDataType())
                        .metaData(Metadata.VARIABLE, ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR);
        if (state.getIterationParam() != null) {
            result.variable(state.getIterationParam(), new ObjectDataType());
            handleActions(result, state.getActions(), outputVarName, state.getIterationParam());
        } else {
            handleActions(result, state.getActions(), outputVarName);
        }
        if (state.getOutputCollection() != null) {
            result.completionAction(new ForEachCollectorActionSupplier(workflow.getExpressionLang(), state.getOutputCollection()));
        }
        return result;
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

}
