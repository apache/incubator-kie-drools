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
package org.kie.kogito.codegen.json;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilsTest {

    @Test
    public void testFullMerge() {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node1 = createJson(mapper, createJson(mapper, "numbers", Arrays.asList(1, 2, 3)));
        JsonNode node2 = createJson(mapper, createJson(mapper, "numbers", Arrays.asList(4, 5, 6)));
        JsonNode node3 = createJson(mapper, mapper.createObjectNode().put("number", 1));
        JsonNode node4 = createJson(mapper, mapper.createObjectNode().put("boolean", false));
        JsonNode node5 = createJson(mapper, mapper.createObjectNode().put("string", "javier"));

        JsonNode result = mapper.createObjectNode();
        result = JsonUtils.merge(node1, result);
        result = JsonUtils.merge(node2, result);
        result = JsonUtils.merge(node3, result);
        result = JsonUtils.merge(node4, result);
        result = JsonUtils.merge(node5, result);

        assertThat(result.size()).isOne();
        JsonNode merged = result.get("merged");
        assertThat(merged.size()).isEqualTo(4);
        JsonNode numbers = merged.get("numbers");
        assertThat(numbers).isInstanceOf(ArrayNode.class);
        ArrayNode numbersNode = (ArrayNode) numbers;
        assertThat(numbersNode.size()).isEqualTo(6);
        assertThat(numbersNode.get(0).asInt()).isOne();
        assertThat(numbersNode.get(1).asInt()).isEqualTo(2);
        assertThat(numbersNode.get(2).asInt()).isEqualTo(3);
        assertThat(numbersNode.get(3).asInt()).isEqualTo(4);
        assertThat(numbersNode.get(4).asInt()).isEqualTo(5);
        assertThat(numbersNode.get(5).asInt()).isEqualTo(6);
        assertThat(merged.get("boolean").asBoolean()).isFalse();
        assertThat(merged.get("string").asText()).isEqualTo("javier");
        assertThat(merged.get("number").asInt()).isOne();
    }

    @Test
    public void testArrayIntoObjectMerge() {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode src = createJson(mapper, createJson(mapper, "property", Arrays.asList(1, 2, 3)));
        JsonNode target = createJson(mapper, mapper.createObjectNode().put("property", 4));

        target = JsonUtils.merge(src, target);

        assertThat(target.size()).isOne();
        JsonNode merged = target.get("merged");
        assertThat(merged.size()).isOne();
        JsonNode property = merged.get("property");
        assertThat(property).isInstanceOf(ArrayNode.class);
        ArrayNode propertyNode = (ArrayNode) property;
        assertThat(propertyNode.size()).isEqualTo(4);
        assertThat(propertyNode.get(0).asInt()).isEqualTo(4);
        assertThat(propertyNode.get(1).asInt()).isOne();
        assertThat(propertyNode.get(2).asInt()).isEqualTo(2);
        assertThat(propertyNode.get(3).asInt()).isEqualTo(3);
    }

    @Test
    public void testArrayIntoNewPropertyMerge() {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode src = createJson(mapper, createJson(mapper, "property", Arrays.asList(1, 2, 3)));
        JsonNode target = createJson(mapper, mapper.createObjectNode());

        target = JsonUtils.merge(src, target);

        assertThat(target.size()).isOne();
        JsonNode merged = target.get("merged");
        assertThat(merged.size()).isOne();
        JsonNode property = merged.get("property");
        assertThat(property).isInstanceOf(ArrayNode.class);
        ArrayNode propertyNode = (ArrayNode) property;
        assertThat(propertyNode.size()).isEqualTo(3);
        assertThat(propertyNode.get(0).asInt()).isOne();
        assertThat(propertyNode.get(1).asInt()).isEqualTo(2);
        assertThat(propertyNode.get(2).asInt()).isEqualTo(3);
    }

    private JsonNode createJson(ObjectMapper mapper, String name, Collection<Integer> integers) {
        ArrayNode node = mapper.createArrayNode();
        for (int i : integers) {
            node.add(i);
        }
        return mapper.createObjectNode().set(name, node);
    }

    private JsonNode createJson(ObjectMapper mapper, JsonNode node) {
        return mapper.createObjectNode().set("merged", node);
    }
}
