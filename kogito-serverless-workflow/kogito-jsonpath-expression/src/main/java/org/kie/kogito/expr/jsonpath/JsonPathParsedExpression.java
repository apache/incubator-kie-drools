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

import org.kie.kogito.process.workitems.impl.expr.ParsedExpression;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class JsonPathParsedExpression implements ParsedExpression {

    private static final Configuration jsonPathConfig = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build();

    private String expr;
    private ParseContext jsonPath;

    public JsonPathParsedExpression(String expr) {
        jsonPath = JsonPath
                .using(jsonPathConfig);
        this.expr = expr;
    }

    @Override
    public <T> T eval(Object context, Class<T> returnClass) {
        DocumentContext parsedContext = jsonPath.parse(context);
        if (String.class.isAssignableFrom(returnClass)) {
            StringBuilder sb = new StringBuilder();
            for (String part : expr.split("((?=\\$))")) {
                sb.append(parsedContext.read(part, String.class));
            }
            return (T) sb.toString();
        } else {
            Object result = parsedContext.read(expr);
            return Boolean.class.isAssignableFrom(returnClass) && result instanceof ArrayNode ? (T) Boolean.valueOf(!((ArrayNode) result).isEmpty())
                    : jsonPathConfig.mappingProvider().map(result, returnClass, jsonPathConfig);
        }
    }

}
