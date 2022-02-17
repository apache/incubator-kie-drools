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
package org.kie.kogito.serverless.workflow;

import java.util.Iterator;
import java.util.Map.Entry;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.process.expr.ExpressionWorkItemResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeResolver extends ExpressionWorkItemResolver {

    public JsonNodeResolver(String exprLang, String jsonPathExpr, String paramName) {
        super(exprLang, jsonPathExpr, paramName);
    }

    @Override
    protected Object evalExpression(Object inputModel, KogitoProcessContext context) {
        return processInputModel(inputModel, ExpressionHandlerFactory.get(language, expression), context);
    }

    private JsonNode processInputModel(final Object inputModel, Expression exprHandler, KogitoProcessContext context) {
        return exprHandler.isValid() ? exprHandler.eval(inputModel, JsonNode.class, context) : processInputModel(inputModel, parse(exprHandler.asString()), context);
    }

    private JsonNode processInputModel(final Object inputModel, JsonNode node, KogitoProcessContext context) {
        if (node.isObject()) {
            final ObjectNode processedDefinition = ObjectMapperFactory.get().createObjectNode();
            final Iterator<Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                final Entry<String, JsonNode> jsonField = fields.next();
                processedDefinition.set(jsonField.getKey(), processInputModel(inputModel, jsonField.getValue(), context));
            }
            return processedDefinition;
        } else if (node.isArray()) {
            final ArrayNode processedDefinition = ObjectMapperFactory.get().createArrayNode();
            ((ArrayNode) node).forEach(item -> processedDefinition.add(processInputModel(inputModel, item, context)));
            return processedDefinition;
        } else if (node.isTextual()) {
            Expression expr = ExpressionHandlerFactory.get(language, node.asText());
            return expr.isValid() ? expr.eval(inputModel, JsonNode.class, context) : node;
        } else {
            return node;
        }
    }

    private JsonNode parse(String exprStr) {
        try {
            return ObjectMapperFactory.get().readTree(exprStr);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse input model from ordinary String to Json tree", e);
        }
    }
}
