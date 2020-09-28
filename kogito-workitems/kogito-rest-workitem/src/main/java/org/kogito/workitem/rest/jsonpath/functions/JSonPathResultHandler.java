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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.kogito.workitem.rest.RestWorkItemHandlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSonPathResultHandler implements RestWorkItemHandlerResult {

    private static final Logger logger = LoggerFactory.getLogger(JSonPathResultHandler.class);

    @Override
    public Object apply(Object inputParameter, JsonObject node) {
        return transform(node, (ObjectNode) inputParameter);
    }

    private ObjectNode transform(JsonObject src, ObjectNode target) {
        src.forEach(e -> setValue(target, e.getKey(), e.getValue()));
        return target;
    }

    private void setValue(ObjectNode result, String key, Object value) {
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
            result.set(key, transform((JsonObject) value, result.objectNode()));
        } else if (value instanceof JsonArray) {
            ArrayNode array = result.arrayNode();
            ((JsonArray) value).forEach(v -> addValue(array, v));
            result.set(key, array);
        } else {
            logger.warn("Unrecognized data type for object {} class {}", value, value.getClass());
        }
    }

    private void addValue(ArrayNode result, Object value) {
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
            result.add(transform((JsonObject) value, result.objectNode()));
        } else if (value instanceof JsonArray) {
            ArrayNode array = result.arrayNode();
            ((JsonArray) value).forEach(v -> addValue(array, v));
            result.add(array);
        } else {
            logger.warn("Unrecognized data type for object {} class {}", value, value.getClass());
        }
    }
}
