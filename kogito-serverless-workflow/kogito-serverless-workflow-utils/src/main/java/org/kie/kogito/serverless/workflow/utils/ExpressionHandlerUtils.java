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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Optional;
import java.util.function.Function;

import org.jbpm.ruleflow.core.Metadata;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.MergeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

public class ExpressionHandlerUtils {

    private ExpressionHandlerUtils() {
    }

    private static final String EXPR_PREFIX = "${";
    private static final String EXPR_SUFFIX = "}";
    private static final String LEGACY_EXPR_PREFIX = "{{";
    private static final String LEGACY_EXPR_SUFFIX = "}}";
    private static final String FUNCTION_REFERENCE = "fn:";
    public static final String SECRET_MAGIC = "SECRET";
    public static final String CONST_MAGIC = "CONST";
    public static final String CONTEXT_MAGIC = "WORKFLOW";

    public static JsonNode getConstants(KogitoProcessContext context) {
        JsonNode node = (JsonNode) context.getProcessInstance().getProcess().getMetaData().get(Metadata.CONSTANTS);
        return node == null ? NullNode.instance : node;
    }

    public static String getSecret(String key) {
        return ConfigResolverHolder.getConfigResolver().getConfigProperty(key, String.class, null);
    }

    public static Function<String, Object> getContextFunction(KogitoProcessContext context) {
        return k -> KogitoProcessContextResolver.get().readKey(context, k);
    }

    public static String trimExpr(String expr) {
        expr = expr.trim();
        if (expr.startsWith(EXPR_PREFIX)) {
            expr = trimExpr(expr, EXPR_PREFIX, EXPR_SUFFIX);
        } else if (expr.startsWith(LEGACY_EXPR_PREFIX)) {
            expr = trimExpr(expr, LEGACY_EXPR_PREFIX, LEGACY_EXPR_SUFFIX);
        }
        return expr.trim();
    }

    private static String trimExpr(String expr, String prefix, String suffix) {
        expr = expr.substring(prefix.length());
        if (expr.endsWith(suffix)) {
            expr = expr.substring(0, expr.length() - suffix.length());
        }
        return expr;
    }

    public static String replaceExpr(Workflow workflow, final String expr) {
        if (expr != null) {
            String candidate = trimExpr(expr);
            if (candidate.startsWith(FUNCTION_REFERENCE)) {
                String functionName = candidate.substring(FUNCTION_REFERENCE.length());
                //covert reference to reference case (and delegate on stack overflow limits for checking loop reference) 
                return replaceExpr(workflow,
                        workflow.getFunctions().getFunctionDefs().stream()
                                .filter(f -> f.getType() == Type.EXPRESSION && f.getName().equals(functionName))
                                .findAny()
                                .map(FunctionDefinition::getOperation)
                                .orElseThrow(() -> new IllegalArgumentException("Cannot find function " + functionName)));
            }
            return candidate;
        }
        return expr;
    }

    public static void assign(JsonNode context, JsonNode target, JsonNode value, String expr) {
        if (context.isObject()) {
            Optional<String> varName = fallbackVarToName(expr);
            if (varName.isPresent()) {
                JsonObjectUtils.addToNode(varName.get(), MergeUtils.merge(value, target), (ObjectNode) context);
            }
        }
    }

    public static Optional<String> fallbackVarToName(String expr) {
        int indexOf = expr.lastIndexOf('.');
        return indexOf < 0 ? Optional.empty() : Optional.of(expr.substring(indexOf + 1));
    }
}
