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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collection;
import java.util.Map;

import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.handlers.MappingSetter;
import org.kie.kogito.serverless.workflow.parser.handlers.MappingUtils;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionParametersFactorySupplier;
import org.kie.kogito.serverless.workflow.suppliers.ObjectResolverSupplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionRef;

public abstract class WorkItemBuilder {

    protected WorkItemNodeFactory<?> addFunctionArgs(Workflow workflow, WorkItemNodeFactory<?> node, FunctionRef functionRef) {
        JsonNode functionArgs = functionRef.getArguments();
        if (functionArgs != null) {
            processArgs(workflow, node, functionArgs, SWFConstants.MODEL_WORKFLOW_VAR);
        }
        return node;
    }

    /**
     * Implementations should use this method to validate if provided function arguments are suitable
     * In case they are not they might throw an exception to interrupt build procedure or print an informative log
     * 
     * @param ref the function reference containing the arguments and the function name
     */
    protected void validateArgs(FunctionRef ref) {
        validateArgs(ref.getArguments());
    }

    protected void validateArgs(JsonNode args) {
    }

    protected WorkItemNodeFactory<?> buildWorkItem(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            ParserContext parserContext,
            String inputVar,
            String outputVar) {
        return MappingUtils.addMapping(embeddedSubProcess.workItemNode(parserContext.newId()), inputVar, outputVar);
    }

    protected final void processArgs(Workflow workflow, WorkItemNodeFactory<?> workItemFactory,
            JsonNode functionArgs, String paramName) {
        MappingUtils.processArgs(workflow, functionArgs, new MappingSetter() {
            @Override
            public void accept(String key, Object value) {
                boolean isExpr = isExpression(workflow, value);
                workItemFactory
                        .workParameter(key,
                                isExpr ? new ObjectResolverSupplier(workflow.getExpressionLang(), value, paramName) : value)
                        .workParameterDefinition(key,
                                getDataType(value, isExpr));
            }

            @Override
            public void accept(Object value) {
                boolean isExpr = isExpression(workflow, value);
                if (isExpr) {
                    workItemFactory.workParameterFactory(new ExpressionParametersFactorySupplier(workflow.getExpressionLang(), value, paramName));
                } else {
                    workItemFactory.workParameter(SWFConstants.CONTENT_DATA, value);
                }
                workItemFactory.workParameterDefinition(SWFConstants.CONTENT_DATA, getDataType(value, isExpr));
            }
        });
    }

    private static boolean isExpression(Workflow workflow, Object value) {
        return value instanceof CharSequence && ExpressionHandlerFactory.get(workflow.getExpressionLang(), value.toString()).isValid() || value instanceof JsonNode;
    }

    private static DataType getDataType(Object object, boolean isExpr) {
        if (object instanceof ObjectNode) {
            return DataTypeResolver.fromClass(Map.class);
        } else if (object instanceof ArrayNode) {
            return DataTypeResolver.fromClass(Collection.class);
        } else {
            return DataTypeResolver.fromObject(object, isExpr);
        }
    }
}
