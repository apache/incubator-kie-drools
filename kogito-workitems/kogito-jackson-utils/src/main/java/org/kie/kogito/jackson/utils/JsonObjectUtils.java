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
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonObjectUtils {

    public static Object toJavaValue(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else if (jsonNode.isInt()) {
            return jsonNode.asInt();
        } else if (jsonNode.isDouble()) {
            return jsonNode.asDouble();
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
            throw new IllegalArgumentException("Cannot convert node " + jsonNode);
        }
    }

    public static void addToNode(String name, Object value, ObjectNode dest) {
        if (value instanceof JsonNode) {
            dest.set(name, (JsonNode) value);
        } else if (value instanceof Boolean) {
            dest.put(name, (Boolean) value);
        } else if (value instanceof String) {
            dest.put(name, (String) value);
        } else if (value instanceof Short) {
            dest.put(name, (Short) value);
        } else if (value instanceof Integer) {
            dest.put(name, (Integer) value);
        } else if (value instanceof Long) {
            dest.put(name, (Long) value);
        } else if (value instanceof Float) {
            dest.put(name, (Float) value);
        } else if (value instanceof Double) {
            dest.put(name, (Double) value);
        } else if (value instanceof BigDecimal) {
            dest.put(name, (BigDecimal) value);
        } else if (value instanceof BigInteger) {
            dest.put(name, (BigInteger) value);
        } else if (value instanceof byte[]) {
            dest.put(name, (byte[]) value);
        } else if (value instanceof Iterable) {
            dest.set(name, mapToArray((Iterable<?>) value));
        } else if (value instanceof Map) {
            dest.set(name, mapToNode((Map<String, Object>) value));
        } else {
            dest.set(name, mapToNode(ObjectMapperFactory.get().convertValue(value, Map.class)));
        }
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

    public static ArrayNode mapToArray(Iterable<?> iterable, ArrayNode arrayNode) {
        for (Object item : iterable) {
            addToArray(arrayNode, item);
        }
        return arrayNode;
    }

    private static void addToArray(ArrayNode dest, Object value) {
        if (value instanceof JsonNode) {
            dest.add((JsonNode) value);
        } else if (value instanceof Boolean) {
            dest.add((Boolean) value);
        } else if (value instanceof String) {
            dest.add((String) value);
        } else if (value instanceof Short) {
            dest.add((Short) value);
        } else if (value instanceof Integer) {
            dest.add((Integer) value);
        } else if (value instanceof Long) {
            dest.add((Long) value);
        } else if (value instanceof Float) {
            dest.add((Float) value);
        } else if (value instanceof Double) {
            dest.add((Double) value);
        } else if (value instanceof BigDecimal) {
            dest.add((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            dest.add((BigInteger) value);
        } else if (value instanceof byte[]) {
            dest.add((byte[]) value);
        } else if (value instanceof Iterable) {
            dest.add(mapToArray((Iterable<?>) value));
        } else if (value instanceof Map) {
            dest.add(mapToNode((Map<String, Object>) value));
        } else {
            dest.add(mapToNode(ObjectMapperFactory.get().convertValue(value, Map.class)));
        }
    }

    private JsonObjectUtils() {
    }

}
