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
package org.kogito.workitem.rest;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;

public class RestWorkItemHandlerUtils {

    private RestWorkItemHandlerUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(RestWorkItemHandlerUtils.class);
    private static Vertx vertx;

    public static synchronized Vertx vertx() {
        if (vertx == null) {
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public static <T> T mergeObject(T target, JsonObject jsonObject) {
        return target instanceof ObjectNode ? (T) mergeJson(jsonObject, (ObjectNode) target) : mergeBean(jsonObject, target);
    }

    private static <T> T mergeBean(JsonObject src, T target) {
        try {
            PropertyDescriptor[] propertyDescritors = Introspector.getBeanInfo(target.getClass()).getPropertyDescriptors();
            src.forEach(e -> setValue(propertyDescritors, target, e.getKey(), e.getValue()));
            return target;
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("Wrong bean " + target, e);
        }
    }

    private static void setValue(PropertyDescriptor[] propertyDescritors, Object target, String name, Object value) {
        for (PropertyDescriptor descriptor : propertyDescritors) {
            if (descriptor.getName().equals(name)) {
                try {
                    descriptor.getWriteMethod().invoke(target, value);
                } catch (ReflectiveOperationException e) {
                    logger.error("Error invoking setter for {} with value {}", name, value, e);
                }
                return;
            }
        }
        logger.warn("No property found for name {}", name);
    }

    private static ObjectNode mergeJson(JsonObject src, ObjectNode target) {
        src.forEach(e -> setValue(target, e.getKey(), e.getValue()));
        return target;
    }

    private static void setValue(ObjectNode result, String key, Object value) {
        if (value instanceof Double) {
            result.put(key, (Double) value);
        } else if (value instanceof Float) {
            result.put(key, (Float) value);
        } else if (value instanceof Long) {
            result.put(key, (Long) value);
        } else if (value instanceof Integer) {
            result.put(key, (Integer) value);
        } else if (value instanceof Short) {
            result.put(key, (Short) value);
        } else if (value instanceof Boolean) {
            result.put(key, (Boolean) value);
        } else if (value instanceof String) {
            result.put(key, (String) value);
        } else if (value instanceof JsonObject) {
            result.set(key, mergeJson((JsonObject) value, result.objectNode()));
        } else if (value instanceof JsonArray) {
            ArrayNode array = result.arrayNode();
            ((JsonArray) value).forEach(v -> addValue(array, v));
            result.set(key, array);
        } else {
            logger.warn("Unrecognized data type for object {} class {}", value, value.getClass());
        }
    }

    private static void addValue(ArrayNode result, Object value) {
        if (value instanceof Double) {
            result.add((Double) value);
        } else if (value instanceof Float) {
            result.add((Float) value);
        } else if (value instanceof Long) {
            result.add((Long) value);
        } else if (value instanceof Integer) {
            result.add((Integer) value);
        } else if (value instanceof Short) {
            result.add((Short) value);
        } else if (value instanceof Boolean) {
            result.add((Boolean) value);
        } else if (value instanceof String) {
            result.add((String) value);
        } else if (value instanceof JsonObject) {
            result.add(mergeJson((JsonObject) value, result.objectNode()));
        } else if (value instanceof JsonArray) {
            ArrayNode array = result.arrayNode();
            ((JsonArray) value).forEach(v -> addValue(array, v));
            result.add(array);
        } else {
            logger.warn("Unrecognized data type for object {} class {}", value, value.getClass());
        }
    }
}
