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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.jackson.utils.JsonNodeVisitor;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.suppliers.ExpressionParametersFactorySupplier;
import org.kie.kogito.serverless.workflow.suppliers.ObjectResolverSupplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionRef;

public abstract class WorkItemBuilder {

    private static final String RESULT = "Result";

    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> addFunctionArgs(Workflow workflow, WorkItemNodeFactory<T> node, FunctionRef functionRef) {
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
        return embeddedSubProcess.workItemNode(parserContext.newId())
                .inMapping(inputVar, SWFConstants.MODEL_WORKFLOW_VAR)
                .outMapping(RESULT, outputVar);
    }

    protected final void processArgs(Workflow workflow, WorkItemNodeFactory<?> workItemFactory,
            JsonNode functionArgs, String paramName) {
        if (functionArgs.isObject()) {
            functionsToMap(workflow, functionArgs).forEach((key, value) -> processArg(workflow, key, value, workItemFactory, paramName));
        } else {
            Object object = functionReference(workflow, JsonObjectUtils.simpleToJavaValue(functionArgs));
            boolean isExpr = isExpression(workflow, object);
            if (isExpr) {
                workItemFactory.workParameterFactory(new ExpressionParametersFactorySupplier(workflow.getExpressionLang(), object, paramName));
            } else {
                workItemFactory.workParameter(SWFConstants.CONTENT_DATA, object);
            }
            workItemFactory.workParameterDefinition(SWFConstants.CONTENT_DATA, getDataType(object, isExpr));
        }
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

    private void processArg(Workflow workflow, String key, Object value, WorkItemNodeFactory<?> workItemFactory, String paramName) {
        boolean isExpr = isExpression(workflow, value);
        workItemFactory
                .workParameter(key,
                        isExpr ? new ObjectResolverSupplier(workflow.getExpressionLang(), value, paramName) : value)
                .workParameterDefinition(key,
                        getDataType(value, isExpr));
    }

    DataType getDataType(Object object, boolean isExpr) {
        if (object instanceof ObjectNode) {
            return DataTypeResolver.fromClass(Map.class);
        } else if (object instanceof ArrayNode) {
            return DataTypeResolver.fromClass(Collection.class);
        } else {
            return DataTypeResolver.fromObject(object, isExpr);
        }
    }

    private boolean isExpression(Workflow workflow, Object value) {
        return value instanceof CharSequence && ExpressionHandlerFactory.get(workflow.getExpressionLang(), value.toString()).isValid() || value instanceof JsonNode;
    }
}
