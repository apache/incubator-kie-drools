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

import java.util.Collections;
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

    public static JsonNode merge(JsonNode src, JsonNode target, boolean skipDuplicates) {
        if (target == null || target.isNull()) {
            return src;
        } else if (target.isArray()) {
            return mergeArray(src, (ArrayNode) target, skipDuplicates);
        } else if (target.isObject()) {
            return mergeObject(src, (ObjectNode) target, skipDuplicates);
        } else {
            if (src.isArray()) {
                ArrayNode srcArray = (ArrayNode) src;
                insert(srcArray, target, getExistingNodes(srcArray, skipDuplicates));
            }
            return src;
        }
    }

    private static ObjectNode mergeObject(JsonNode src, ObjectNode target, boolean skipDuplicates) {
        if (src.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> mergedIterator = src.fields();
            while (mergedIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = mergedIterator.next();
                JsonNode found = target.get(entry.getKey());
                target.set(entry.getKey(), found != null ? merge(entry.getValue(), found, skipDuplicates) : entry.getValue());
            }
        } else if (!src.isNull()) {
            target.set("response", src);
        }
        return target;
    }

    private static JsonNode mergeArray(JsonNode src, ArrayNode target, boolean skipDuplicates) {
        if (src != target) {
            Set<JsonNode> existingNodes = getExistingNodes(target, skipDuplicates);
            if (src.isArray()) {
                ((ArrayNode) src).forEach(node -> add(target, node, existingNodes));
            } else {
                add(target, src, existingNodes);
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

    private static Set<JsonNode> getExistingNodes(ArrayNode arrayNode, boolean skipDuplicates) {
        Set<JsonNode> existingNodes = Collections.emptySet();
        if (skipDuplicates) {
            existingNodes = new HashSet<>();
            arrayNode.forEach(existingNodes::add);
        }
        return existingNodes;
    }

    private MergeUtils() {
    }

}
