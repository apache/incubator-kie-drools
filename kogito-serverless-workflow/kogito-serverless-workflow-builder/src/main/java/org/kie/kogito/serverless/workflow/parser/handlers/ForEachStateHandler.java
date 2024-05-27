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

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ForEachNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.suppliers.CollectorActionSupplier;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.ForEachState;

import static org.jbpm.workflow.instance.node.ForEachNodeInstance.TEMP_OUTPUT_VAR;
import static org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser.DEFAULT_WORKFLOW_VAR;
import static org.kie.kogito.serverless.workflow.utils.KogitoProcessContextResolver.FOR_EACH_OUTPUT_VARIABLE;

public class ForEachStateHandler extends CompositeContextNodeHandler<ForEachState> {

    protected ForEachStateHandler(ForEachState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    protected MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        ForEachNodeFactory<?> result =
                factory.forEachNode(parserContext.newId()).sequential(false).waitForCompletion(true).collectionExpression(state.getInputCollection())
                        .outputVariable(FOR_EACH_OUTPUT_VARIABLE, new ObjectDataType(JsonNode.class))
                        .metaData(Metadata.VARIABLE, DEFAULT_WORKFLOW_VAR)
                        .tempVariable(TEMP_OUTPUT_VAR, new ObjectDataType(JsonNode.class));
        if (state.getIterationParam() != null) {
            result.variable(state.getIterationParam(), new ObjectDataType(JsonNode.class));
        }
        if (state.getOutputCollection() != null) {
            result.completionAction(new CollectorActionSupplier(workflow.getExpressionLang(), state.getOutputCollection(), DEFAULT_WORKFLOW_VAR, TEMP_OUTPUT_VAR));
        }
        handleActions(result, state.getActions(), FOR_EACH_OUTPUT_VARIABLE, false);
        handleErrors(factory, result);
        return new MakeNodeResult(result);
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

}
