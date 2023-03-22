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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
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
            return fromString((String) value);
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
            return ObjectMapperFactory.listenerAware().convertValue(value, JsonNode.class);
        }
    }

    public static JsonNode fromString(String value) {
        String trimmedValue = value.trim();
        if (trimmedValue.startsWith("{") && trimmedValue.endsWith("}")) {
            try {
                return ObjectMapperFactory.listenerAware().readTree(trimmedValue);
            } catch (IOException ex) {
                // ignore and return test node
            }
        }
        return new TextNode(value);
    }

    private static Object toJavaValue(ObjectNode node) {
        Map<String, Object> result = new HashMap<>();
        node.fields().forEachRemaining(iter -> result.put(iter.getKey(), toJavaValue(iter.getValue())));
        return result;
    }

    private static Collection toJavaValue(ArrayNode node) {
        Collection result = new ArrayList<>();
        for (JsonNode item : node) {
            result.add(internalToJavaValue(item, JsonObjectUtils::toJavaValue, JsonObjectUtils::toJavaValue));
        }
        return result;
    }

    public static Object toJavaValue(JsonNode jsonNode) {
        return internalToJavaValue(jsonNode, JsonObjectUtils::toJavaValue, JsonObjectUtils::toJavaValue);
    }

    public static <T> T convertValue(Object obj, Class<T> returnType) {
        if (returnType.isInstance(obj)) {
            return returnType.cast(obj);
        } else if (obj instanceof JsonNode) {
            return convertValue((JsonNode) obj, returnType);
        } else {
            return ObjectMapperFactory.listenerAware().convertValue(obj, returnType);
        }
    }

    public static <T> T convertValue(JsonNode jsonNode, Class<T> returnType) {
        Object obj;
        if (Boolean.class.isAssignableFrom(returnType)) {
            obj = jsonNode.asBoolean();
        } else if (Integer.class.isAssignableFrom(returnType)) {
            obj = jsonNode.asInt();
        } else if (Double.class.isAssignableFrom(returnType)) {
            obj = jsonNode.asDouble();
        } else if (Long.class.isAssignableFrom(returnType)) {
            obj = jsonNode.asLong();
        } else if (String.class.isAssignableFrom(returnType)) {
            obj = jsonNode.asText();
        } else {
            obj = ObjectMapperFactory.listenerAware().convertValue(jsonNode, returnType);
        }
        return returnType.cast(obj);
    }

    public static Object simpleToJavaValue(JsonNode jsonNode) {
        return internalToJavaValue(jsonNode, node -> node, node -> node);
    }

    private static Object internalToJavaValue(JsonNode jsonNode, Function<ObjectNode, Object> objectFunction, Function<ArrayNode, Object> arrayFunction) {
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
            return arrayFunction.apply((ArrayNode) jsonNode);
        } else if (jsonNode.isObject()) {
            return objectFunction.apply((ObjectNode) jsonNode);
        } else {
            return ObjectMapperFactory.get().convertValue(jsonNode, Object.class);
        }
    }

    public static String toString(JsonNode node) throws JsonProcessingException {
        return ObjectMapperFactory.get().writeValueAsString(node);
    }

    public static void addToNode(String name, Object value, ObjectNode dest) {
        dest.set(name, fromValue(value));
    }

    private static ObjectNode mapToNode(Map<String, Object> value) {
        ObjectNode objectNode = ObjectMapperFactory.listenerAware().createObjectNode();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            addToNode(entry.getKey(), entry.getValue(), objectNode);
        }
        return objectNode;
    }

    private static ArrayNode mapToArray(Iterable<?> iterable) {
        return mapToArray(iterable, ObjectMapperFactory.listenerAware().createArrayNode());
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
