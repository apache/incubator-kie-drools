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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.MergeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.workflow.Constants;

public class ExpressionHandlerUtils {

    private ExpressionHandlerUtils() {
    }

    private static final String EXPR_PREFIX = "${";
    private static final String EXPR_SUFFIX = "}";
    private static final String LEGACY_EXPR_PREFIX = "{{";
    private static final String LEGACY_EXPR_SUFFIX = "}}";
    private static final String FUNCTION_REFERENCE = "fn:";
    protected static final String SECRET_MAGIC = "$SECRET.";
    protected static final String CONST_MAGIC = "$CONST.";
    protected static final String CONTEXT_MAGIC = "$WORKFLOW.";
    private static final Collection<String> MAGIC_WORDS = Arrays.asList(SECRET_MAGIC, CONST_MAGIC, CONTEXT_MAGIC);

    public static String prepareExpr(String expr, Optional<KogitoProcessContext> context) {
        expr = replaceMagic(expr, SECRET_MAGIC, ExpressionHandlerUtils::getSecret);
        return context.isPresent() ? replaceMagic(expr, CONTEXT_MAGIC, key -> KogitoProcessContextResolver.get().readKey(context.get(), key)) : expr;
    }

    public static Collection<String> getMagicWords() {
        return MAGIC_WORDS;
    }

    private static String getSecret(String key) {
        return ConfigResolverHolder.getConfigResolver().getConfigProperty(key, String.class, null);
    }

    private static Object getConstant(String key, Constants constants) {
        Objects.requireNonNull(constants, "Constants has not been specified, key " + key + "cannot be replaced");
        JsonNode result = constants.getConstantsDef();
        for (String name : key.split("\\.")) {
            result = result.get(name);
        }
        return JsonObjectUtils.toJavaValue(result);
    }

    private static <T extends Object> String replaceMagic(String expr, String magic, Function<String, T> replacer) {
        int indexOf;
        while ((indexOf = expr.indexOf(magic)) != -1) {
            String key = extractKey(expr, indexOf + magic.length());
            T value = replacer.apply(key);
            if (value != null) {
                expr = expr.replace(magic + key, value.toString());
            } else {
                break;
            }
        }
        return expr;
    }

    private static String extractKey(String expr, int indexOf) {
        StringBuilder sb = new StringBuilder();
        for (int i = indexOf; i < expr.length(); i++) {
            char ch = expr.charAt(i);
            if (Character.isJavaIdentifierPart(ch) || ch == '.') {
                sb.append(ch);
            } else {
                break;
            }
        }
        return sb.toString();
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
            return replaceMagic(candidate, CONST_MAGIC, key -> getConstant(key, workflow.getConstants()));
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
