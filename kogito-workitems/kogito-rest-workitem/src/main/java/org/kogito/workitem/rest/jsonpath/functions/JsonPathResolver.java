/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kogito.workitem.rest.jsonpath.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.kogito.workitem.rest.RestWorkItemHandlerParamResolver;

public class JsonPathResolver implements RestWorkItemHandlerParamResolver {

    private static final Configuration jsonPathConfig = Configuration
        .builder()
        .mappingProvider(new JacksonMappingProvider())
        .jsonProvider(new JacksonJsonNodeJsonProvider())
        .build();

    private String jsonPathExpr;

    public JsonPathResolver(String jsonPathExpr) {
        this.jsonPathExpr = jsonPathExpr;
    }

    @Override
    public Object apply(Object context) {
        JsonNode node = JsonPath
            .using(jsonPathConfig)
            .parse(context)
            .read(jsonPathExpr, JsonNode.class);
        return readValue(node);
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
            default:
            case STRING:
                return node.asText();
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
