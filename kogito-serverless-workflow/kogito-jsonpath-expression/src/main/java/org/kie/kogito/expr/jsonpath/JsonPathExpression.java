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
        if (String.class.isAssignableFrom(returnClass)) {
            StringBuilder sb = new StringBuilder();
            for (String part : ExpressionHandlerUtils.prepareExpr(expr, Optional.ofNullable(processInfo)).split("((?=\\$))")) {
                JsonNode partResult = parsedContext.read(part, JsonNode.class);
                sb.append(partResult.isTextual() ? partResult.asText() : partResult.toPrettyString());
            }
            return (T) sb.toString();
        } else {
            Object result = parsedContext.read(ExpressionHandlerUtils.prepareExpr(expr, Optional.ofNullable(processInfo)));
            return Boolean.class.isAssignableFrom(returnClass) && result instanceof ArrayNode ? (T) Boolean.valueOf(!((ArrayNode) result).isEmpty())
                    : jsonPathConfig.mappingProvider().map(result, returnClass, jsonPathConfig);
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
    public boolean isValid() {
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
