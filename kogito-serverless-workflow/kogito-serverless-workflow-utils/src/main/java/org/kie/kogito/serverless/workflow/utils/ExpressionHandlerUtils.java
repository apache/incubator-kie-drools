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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jbpm.ruleflow.core.Metadata;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.MergeUtils;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

public class ExpressionHandlerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionHandlerUtils.class);

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

    public static Optional<String> getOptionalSecret(String key) {
        return ConfigResolverHolder.getConfigResolver().getConfigProperty(key, String.class);
    }

    public static String getSecret(String key) {
        return getOptionalSecret(key).orElse(null);
    }

    public static Function<String, Object> getContextFunction(KogitoProcessContext context) {
        return k -> KogitoProcessContextResolver.get().readKey(context, k);
    }

    public static JsonNode transform(JsonNode node, Object inputModel, KogitoProcessContext context, String language) {
        Expression expr = ExpressionHandlerFactory.get(language, node.asText());
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Expression: {}, valid: {}", expr.asString(), expr.isValid());
            }
            return expr.isValid() ? expr.eval(inputModel, JsonNode.class, context) : node;
        } catch (Exception ex) {
            logger.info("Error evaluating expression, returning original text {}", node);
            return node;
        }
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
        JsonNode merged = MergeUtils.merge(value, target);
        if (context.isObject()) {
            ObjectNode root = (ObjectNode) context;
            StringBuilder sb = new StringBuilder();
            List<String> properties = new ArrayList<>();
            for (int i = expr.length() - 1; i >= 0; i--) {
                char c = expr.charAt(i);
                if (Character.isJavaIdentifierPart(c)) {
                    sb.insert(0, expr.charAt(i));
                } else if (c == '.') {
                    if (sb.length() == 0) {
                        break;
                    }
                    properties.add(0, sb.toString());
                    sb = new StringBuilder();
                } else {
                    if (sb.length() > 0) {
                        properties.add(0, sb.toString());
                    }
                    break;
                }
            }
            if (!properties.isEmpty()) {
                int size = properties.size() - 1;
                for (int i = 0; i < size; i++) {
                    root = addObjectNode(root, properties.get(i));
                }
                root.set(properties.get(size), merged);
            }
        }
    }

    private static ObjectNode addObjectNode(ObjectNode target, String propName) {
        if (target.has(propName)) {
            JsonNode childNode = target.get(propName);
            if (childNode.isObject()) {
                return (ObjectNode) childNode;
            }
        }
        ObjectNode newNode = target.objectNode();
        target.set(propName, newNode);
        return newNode;
    }
}
