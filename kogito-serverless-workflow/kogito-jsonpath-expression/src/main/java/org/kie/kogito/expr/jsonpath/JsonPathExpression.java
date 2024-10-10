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
package org.kie.kogito.expr.jsonpath;

import java.util.Map;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonPathExpression implements Expression {

    static final String LANG = "jsonpath";

    private final String expr;
    private Boolean isValid;
    private Exception validationError;

    public JsonPathExpression(String expr) {
        expr = replaceMagic(expr, ExpressionHandlerUtils.CONST_MAGIC);
        expr = replaceMagic(expr, ExpressionHandlerUtils.SECRET_MAGIC);
        expr = replaceMagic(expr, ExpressionHandlerUtils.CONTEXT_MAGIC);
        this.expr = expr;
    }

    private static final String replaceMagic(String expr, String magic) {
        magic = "$" + magic;
        return expr.replace(magic, "@." + magic);
    }

    private Configuration getConfiguration(KogitoProcessContext context) {
        return Configuration
                .builder()
                .mappingProvider(new JsonPathJacksonProvider())
                .jsonProvider(new WorkflowJacksonJsonNodeJsonProvider(context))
                .build();
    }

    private static boolean isContextAware(JsonNode context, Map<String, JsonNode> additionalVars) {
        return !additionalVars.isEmpty() && context instanceof ObjectNode;
    }

    private <T> T eval(JsonNode context, Class<T> returnClass, KogitoProcessContext processInfo) {

        Configuration jsonPathConfig = getConfiguration(processInfo);
        DocumentContext parsedContext = JsonPath.using(jsonPathConfig).parse(context);
        if (String.class.isAssignableFrom(returnClass)) {
            StringBuilder sb = new StringBuilder();
            // valid json path is $. or $[
            for (String part : expr.split("((?=\\$\\.|\\$\\[))")) {
                JsonNode partResult = parsedContext.read(part, JsonNode.class);
                sb.append(partResult.isTextual() ? partResult.asText() : partResult.toPrettyString());
            }
            return (T) sb.toString();
        } else {
            Object result = parsedContext.read(expr);
            return Boolean.class.isAssignableFrom(returnClass) && result instanceof ArrayNode ? (T) Boolean.valueOf(!((ArrayNode) result).isEmpty())
                    : JsonObjectUtils.convertValue(jsonPathConfig.mappingProvider().map(result, returnClass, jsonPathConfig), returnClass);
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
            try {
                JsonPath.compile(expr);
                isValid = true;
            } catch (JsonPathException ex) {
                validationError = ex;
                isValid = false;
            }
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

    @Override
    public Exception validationError() {
        return validationError;
    }

    @Override
    public String lang() {
        return LANG;
    }
}
