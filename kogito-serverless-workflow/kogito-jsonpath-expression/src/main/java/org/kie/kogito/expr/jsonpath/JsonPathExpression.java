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
package org.kie.kogito.expr.jsonpath;

import java.util.Optional;
import java.util.regex.Pattern;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.getMagicWords;

public class JsonPathExpression implements Expression {

    private static final Configuration jsonPathConfig = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build();

    private static final Pattern jsonPathRegexPattern = Pattern.compile(getPatternString());

    private static final String getPatternString() {
        StringBuilder sb = new StringBuilder("^((\\$\\[).*|(\\$\\.).*");
        for (String magicWord : getMagicWords()) {
            sb.append("|(" + magicWord.replace("$", "\\$").replace(".", "\\.") + ").*");
        }
        sb.append(')');
        return sb.toString();
    }

    private final String expr;
    private final ParseContext jsonPath;
    private Boolean isValid;

    public JsonPathExpression(String expr) {
        jsonPath = JsonPath.using(jsonPathConfig);
        this.expr = expr;
    }

    private <T> T eval(JsonNode context, Class<T> returnClass, KogitoProcessContext processInfo) {
        DocumentContext parsedContext = jsonPath.parse(context);
        String replacedExpr = ExpressionHandlerUtils.prepareExpr(expr, Optional.ofNullable(processInfo), JsonPathExpressionHandler::inject);
        /*
         * Handling the case where the expression is $SECRET, $CONSTANT or WORKFLOW (after a replacement that has changed something, the expression is not longer a selector)
         * In this case we should just return the expr string cast to the desired return type
         */
        if (!replacedExpr.equals(expr) && !jsonPathRegexPattern.matcher(replacedExpr).matches()) {
            if (replacedExpr.startsWith("'")) {
                replacedExpr = replacedExpr.substring(1, replacedExpr.length() - 1);
            }
            if (String.class.isAssignableFrom(returnClass)) {
                return (T) replacedExpr;
            } else if (Boolean.class.equals(returnClass)) {
                return (T) Boolean.valueOf(replacedExpr);
            } else {
                return (T) new TextNode(replacedExpr);
            }
        } else {
            /*
             * Handling the case where the expr is a valid selector (or it is expected to be).
             * Messages (expected output string) are special cases that requires splitting the string in chunks to analyze each potential expression separately
             */
            if (String.class.isAssignableFrom(returnClass)) {
                StringBuilder sb = new StringBuilder();
                for (String part : replacedExpr.split("((?=\\$))")) {
                    JsonNode partResult = parsedContext.read(part, JsonNode.class);
                    sb.append(partResult.isTextual() ? partResult.asText() : partResult.toPrettyString());
                }
                return (T) sb.toString();
            } else {
                Object result = parsedContext.read(ExpressionHandlerUtils.prepareExpr(expr, Optional.ofNullable(processInfo), JsonPathExpressionHandler::inject));
                return Boolean.class.isAssignableFrom(returnClass) && result instanceof ArrayNode ? (T) Boolean.valueOf(!((ArrayNode) result).isEmpty())
                        : jsonPathConfig.mappingProvider().map(result, returnClass, jsonPathConfig);
            }
        }
    }

    private void assign(JsonNode context, Object value, KogitoProcessContext processInfo) {
        JsonNode target;
        try {
            target = eval(context, JsonNode.class, processInfo);
        } catch (PathNotFoundException ex) {
            target = NullNode.instance;
        }
        ExpressionHandlerUtils.assign(context, target, (JsonNode) value, expr);
    }

    @Override
    public boolean isValid(Optional<KogitoProcessContext> context) {
        if (isValid == null) {
            isValid = jsonPathRegexPattern.matcher(expr).matches();
        }
        return isValid;
    }

    @Override
    public <T> T eval(Object target, Class<T> returnClass, KogitoProcessContext context) {
        return eval(JsonObjectUtils.fromValue(target), returnClass, context);
    }

    @Override
    public void assign(Object target, Object value, KogitoProcessContext context) {
        assign(JsonObjectUtils.fromValue(target), JsonObjectUtils.fromValue(value), context);
    }

    @Override
    public String asString() {
        return expr;
    }
}
