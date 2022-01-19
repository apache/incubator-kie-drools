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
package org.kie.kogito.jackson.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JsonObjectUtils {
    /*
     * Implementation note:
     * Although we can use directly ObjectMapper.convertValue for implementing fromValue and toJavaValue methods,
     * the performance gain of avoiding an intermediate buffer is so tempting that we cannot avoid it
     */

    public static JsonNode fromValue(Object value) {
        if (value == null) {
            return NullNode.instance;
        } else if (value instanceof JsonNode) {
            return (JsonNode) value;
        } else if (value instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) value);
        } else if (value instanceof String) {
            return new TextNode((String) value);
        } else if (value instanceof Short) {
            return new ShortNode((Short) value);
        } else if (value instanceof Integer) {
            return new IntNode((Integer) value);
        } else if (value instanceof Long) {
            return new LongNode((Long) value);
        } else if (value instanceof Float) {
            return new FloatNode((Float) value);
        } else if (value instanceof Double) {
            return new DoubleNode((Double) value);
        } else if (value instanceof BigDecimal) {
            return DecimalNode.valueOf((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return BigIntegerNode.valueOf((BigInteger) value);
        } else if (value instanceof byte[]) {
            return BinaryNode.valueOf((byte[]) value);
        } else if (value instanceof Iterable) {
            return mapToArray((Iterable<?>) value);
        } else if (value instanceof Map) {
            return mapToNode((Map<String, Object>) value);
        } else {
            return ObjectMapperFactory.get().convertValue(value, JsonNode.class);
        }
    }

    public static Object toJavaValue(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        } else if (jsonNode.isTextual()) {
            return jsonNode.asText();
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else if (jsonNode.isInt()) {
            return jsonNode.asInt();
        } else if (jsonNode.isDouble()) {
            return jsonNode.asDouble();
        } else if (jsonNode.isNumber()) {
            return jsonNode.numberValue();
        } else if (jsonNode.isArray()) {
            Collection result = new ArrayList<>();
            for (JsonNode item : ((ArrayNode) jsonNode)) {
                result.add(toJavaValue(item));
            }
            return result;
        } else if (jsonNode.isObject()) {
            Map<String, Object> result = new HashMap<>();
            jsonNode.fields().forEachRemaining(iter -> result.put(iter.getKey(), toJavaValue(iter.getValue())));
            return result;
        } else {
            return ObjectMapperFactory.get().convertValue(jsonNode, Object.class);
        }
    }

    public static void addToNode(String name, Object value, ObjectNode dest) {
        dest.set(name, fromValue(value));
    }

    private static ObjectNode mapToNode(Map<String, Object> value) {
        ObjectNode objectNode = ObjectMapperFactory.get().createObjectNode();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            addToNode(entry.getKey(), entry.getValue(), objectNode);
        }
        return objectNode;
    }

    private static ArrayNode mapToArray(Iterable<?> iterable) {
        return mapToArray(iterable, ObjectMapperFactory.get().createArrayNode());
    }

    private static ArrayNode mapToArray(Iterable<?> iterable, ArrayNode arrayNode) {
        for (Object item : iterable) {
            arrayNode.add(fromValue(item));
        }
        return arrayNode;
    }

    private JsonObjectUtils() {
    }

}
