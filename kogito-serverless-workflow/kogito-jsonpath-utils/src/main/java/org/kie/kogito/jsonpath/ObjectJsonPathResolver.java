/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jsonpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kie.kogito.process.workitems.impl.ExpressionWorkItemResolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ObjectJsonPathResolver extends ExpressionWorkItemResolver {

    public ObjectJsonPathResolver(String jsonPathExpr, String paramName) {
        super(jsonPathExpr, paramName);
    }

    @Override
    protected Object evalExpression(Object inputModel) {
        return readValue(JsonPathUtils.evalExpr(inputModel, expression));
    }

    private Object readValue(JsonNode node) {
        switch (node.getNodeType()) {
            case NUMBER:
                if (node.isInt()) {
                    return node.asInt();
                } else if (node.isLong()) {
                    return node.asLong();
                } else {
                    return node.asDouble();
                }
            case BOOLEAN:
                return node.asBoolean();
            case NULL:
                return null;
            case ARRAY:
                return readArray((ArrayNode) node);
            case STRING:
                return node.asText();
            default:
                return node;
        }
    }

    private Object readArray(ArrayNode node) {
        Iterator<JsonNode> elements = node.elements();
        Collection<Object> result = new ArrayList<>();
        while (elements.hasNext()) {
            result.add(readValue(elements.next()));
        }
        return result;
    }
}
