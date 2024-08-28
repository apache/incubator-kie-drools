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
package org.kie.kogito.jackson.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MergeUtils {
    /**
     * Merge two JSON documents.
     *
     * @param src JsonNode to be merged
     * @param target JsonNode to merge to
     */
    public static JsonNode merge(JsonNode src, JsonNode target) {
        return merge(src, target, false);
    }

    public static JsonNode merge(JsonNode src, JsonNode target, boolean mergeArray) {
        if (target == null || target.isNull() || target.isObject() && target.isEmpty() && src != null && !src.isNull()) {
            return src;
        } else if (target.isArray()) {
            return mergeArray(src, (ArrayNode) target, mergeArray);
        } else if (target.isObject()) {
            return mergeObject(src, (ObjectNode) target, mergeArray);
        } else {
            if (src.isArray()) {
                ArrayNode srcArray = (ArrayNode) src;
                insert(srcArray, target, getExistingNodes(srcArray));
            } else if (src.isObject()) {
                ((ObjectNode) src).set("_target", target);
            }
            return src;
        }
    }

    private static ObjectNode mergeObject(JsonNode src, ObjectNode target, boolean mergeArray) {
        if (src.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> mergedIterator = src.fields();
            while (mergedIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = mergedIterator.next();
                JsonNode found = target.get(entry.getKey());
                target.set(entry.getKey(), found != null ? merge(entry.getValue(), found, mergeArray) : entry.getValue());
            }
        } else if (!src.isNull()) {
            target.set("response", src);
        }
        return target;
    }

    private static JsonNode mergeArray(JsonNode src, ArrayNode target, boolean mergeArray) {
        if (src != target) {
            if (src.isArray()) {
                if (mergeArray) {
                    ((ArrayNode) src).forEach(node -> add(target, node, getExistingNodes(target)));
                } else {
                    return src;
                }
            } else {
                add(target, src, getExistingNodes(target));
            }
        }
        return target;
    }

    private static void add(ArrayNode array, JsonNode node, Set<JsonNode> existingNodes) {
        if (!existingNodes.contains(node)) {
            array.add(node);
        }
    }

    private static void insert(ArrayNode array, JsonNode node, Set<JsonNode> existingNodes) {
        if (!existingNodes.contains(node)) {
            array.insert(0, node);
        }
    }

    private static Set<JsonNode> getExistingNodes(ArrayNode arrayNode) {
        Set<JsonNode> existingNodes = new HashSet<>();
        arrayNode.forEach(existingNodes::add);
        return existingNodes;
    }

    private MergeUtils() {
    }

}
