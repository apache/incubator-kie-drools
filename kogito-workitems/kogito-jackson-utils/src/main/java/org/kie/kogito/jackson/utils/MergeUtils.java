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
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class MergeUtils {
    /**
     * Merge two JSON documents.
     *
     * @param src JsonNode to be merged
     * @param target JsonNode to merge to
     */
    public static JsonNode merge(JsonNode src, JsonNode target) {
        if (target.isArray()) {
            return ((ArrayNode) target).add(src);
        } else {
            if (src.isArray()) {
                src = ObjectMapperFactory.get().createObjectNode().set("response", src);
            }
            return merge(src, target, false);
        }
    }

    public static JsonNode merge(JsonNode src, JsonNode target, boolean skipDuplicates) {

        Iterator<Map.Entry<String, JsonNode>> fields = src.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode subNode = entry.getValue();
            switch (subNode.getNodeType()) {
                case OBJECT:
                    writeObject(entry, target);
                    break;
                case ARRAY:
                    writeArray(entry, target, skipDuplicates);
                    break;
                case STRING:
                    updateObject(target, new TextNode(entry.getValue().textValue()), entry);
                    break;
                case NUMBER:
                    updateObject(target, new IntNode(entry.getValue().intValue()), entry);
                    break;
                case BOOLEAN:
                    updateObject(target, BooleanNode.valueOf(entry.getValue().booleanValue()), entry);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized data type {} " + subNode.getNodeType());
            }
        }
        return target;
    }

    private static void writeObject(Map.Entry<String, JsonNode> srcEntry, JsonNode target) {
        boolean isNewBlock = true;
        Iterator<Map.Entry<String, JsonNode>> mergedIterator = target.fields();
        while (mergedIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = mergedIterator.next();
            if (entry.getKey().equals(srcEntry.getKey())) {
                merge(srcEntry.getValue(), entry.getValue());
                isNewBlock = false;
            }
        }
        if (isNewBlock) {
            ((ObjectNode) target).replace(srcEntry.getKey(), srcEntry.getValue());
        }
    }

    private static void writeArray(Map.Entry<String, JsonNode> srcEntry, JsonNode target, boolean skipDuplicates) {
        JsonNode targetNode = target.get(srcEntry.getKey());
        ArrayNode targetArrayNode;
        Set<JsonNode> existingNodes;
        if (targetNode instanceof ArrayNode) {
            targetArrayNode = (ArrayNode) targetNode;
            existingNodes = new HashSet<>();
            if (skipDuplicates) {
                targetArrayNode.forEach(existingNodes::add);
            }
        } else {
            targetArrayNode = JsonNodeFactory.instance.arrayNode();
            if (targetNode != null) {
                existingNodes = skipDuplicates ? Collections.singleton(targetNode) : Collections.emptySet();
                targetArrayNode.add(targetNode);
            } else {
                existingNodes = Collections.emptySet();
            }
        }
        if (srcEntry.getValue() != targetArrayNode) {
            srcEntry.getValue().forEach(node -> addNotDuplicate(targetArrayNode, node, existingNodes));
            ((ObjectNode) target).replace(srcEntry.getKey(), targetArrayNode);
        }
    }

    private static void addNotDuplicate(ArrayNode array, JsonNode node, Set<JsonNode> existingNodes) {
        if (!existingNodes.contains(node)) {
            array.add(node);
        }
    }

    private static void updateObject(JsonNode target,
            ValueNode value,
            Map.Entry<String, JsonNode> src) {
        boolean newEntry = true;
        Iterator<Map.Entry<String, JsonNode>> iter = target.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            if (entry.getKey().equals(src.getKey())) {
                newEntry = false;
                entry.setValue(value);
            }
        }
        if (newEntry) {
            ((ObjectNode) target).replace(src.getKey(), src.getValue());
        }
    }

    private MergeUtils() {
    }

}
