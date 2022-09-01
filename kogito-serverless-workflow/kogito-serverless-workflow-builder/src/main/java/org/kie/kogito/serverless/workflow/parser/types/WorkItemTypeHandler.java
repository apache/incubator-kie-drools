/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser.types;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.jackson.utils.JsonNodeVisitor;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandler;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionParametersFactorySupplier;
import org.kie.kogito.serverless.workflow.suppliers.ObjectResolverSupplier;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kogito.workitem.rest.RestWorkItemHandler;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

public abstract class WorkItemTypeHandler implements FunctionTypeHandler {
    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow, ParserContext context, RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef, FunctionRef functionRef,
            VariableInfo varInfo) {
        return addFunctionArgs(workflow, fillWorkItemHandler(workflow, context, buildWorkItem(embeddedSubProcess, context, functionDef, varInfo.getInputVar(), varInfo.getOutputVar()), functionDef),
                functionRef);
    }

    protected abstract <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow, ParserContext context, WorkItemNodeFactory<T> node,
            FunctionDefinition functionDef);

    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> addFunctionArgs(Workflow workflow, WorkItemNodeFactory<T> node, FunctionRef functionRef) {
        JsonNode functionArgs = functionRef.getArguments();
        if (functionArgs != null) {
            processArgs(workflow, node, functionArgs, SWFConstants.MODEL_WORKFLOW_VAR);
        }
        return node;
    }

    private Map<String, Object> functionsToMap(Workflow workflow, JsonNode jsonNode) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (jsonNode != null) {
            Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> entry = iter.next();
                map.put(entry.getKey(), functionReference(workflow, JsonObjectUtils.simpleToJavaValue(entry.getValue())));
            }
        }
        return map;
    }

    private Object functionReference(Workflow workflow, Object object) {
        if (object instanceof JsonNode) {
            return JsonNodeVisitor.transformTextNode((JsonNode) object, node -> JsonObjectUtils.fromValue(ExpressionHandlerUtils.replaceExpr(workflow, node.asText())));
        } else if (object instanceof CharSequence) {
            return ExpressionHandlerUtils.replaceExpr(workflow, object.toString());
        } else {
            return object;
        }
    }

    protected final void processArgs(Workflow workflow, WorkItemNodeFactory<?> workItemFactory,
            JsonNode functionArgs, String paramName) {
        if (functionArgs.isObject()) {
            functionsToMap(workflow, functionArgs).forEach((key, value) -> processArg(workflow, key, value, workItemFactory, paramName));
        } else {
            Object object = functionReference(workflow, JsonObjectUtils.simpleToJavaValue(functionArgs));
            if (isExpression(workflow, object)) {
                workItemFactory.workParameterFactory(new ExpressionParametersFactorySupplier(workflow.getExpressionLang(), object, paramName));
            } else {
                workItemFactory.workParameter(RestWorkItemHandler.CONTENT_DATA, object);
            }
        }
    }

    private void processArg(Workflow workflow, String key, Object value, WorkItemNodeFactory<?> workItemFactory, String paramName) {
        boolean isExpr = isExpression(workflow, value);
        workItemFactory
                .workParameter(key,
                        isExpr ? new ObjectResolverSupplier(workflow.getExpressionLang(), value, paramName) : value)
                .workParameterDefinition(key,
                        DataTypeResolver.fromObject(value, isExpr));
    }

    private boolean isExpression(Workflow workflow, Object value) {
        return value instanceof CharSequence && ExpressionHandlerFactory.get(workflow.getExpressionLang(), value.toString()).isValid() || value instanceof JsonNode;
    }

    protected WorkItemNodeFactory<?> buildWorkItem(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            ParserContext parserContext,
            FunctionDefinition actionFunction,
            String inputVar,
            String outputVar) {
        return embeddedSubProcess.workItemNode(parserContext.newId())
                .inMapping(inputVar, SWFConstants.MODEL_WORKFLOW_VAR)
                .outMapping(RestWorkItemHandler.RESULT, outputVar).name(actionFunction.getName());
    }

}
