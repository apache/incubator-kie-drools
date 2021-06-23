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

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.vertx.core.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestWorkItemHandlerUtilsTest {

    private static class Person {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testMergeBean() {
        Person person = new Person();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("id", 26);
        jsonObject.put("name", "pepe");
        person = RestWorkItemHandlerUtils.mergeObject(person, jsonObject);
        assertEquals(26, person.getId());
        assertEquals("pepe", person.getName());
    }

    @Test
    public void testMergeJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        JsonObject jsonObject = new JsonObject(
                "{\"id\":26,\"name\":\"pepe\",\"array\":[1,\"manolo\",true,3.5, 10000000000],\"objectArray\":[{\"item\":\"javierito\"}],\"miles\":3.4,\"married\":true,\"population\":1000000}");
        objectNode = RestWorkItemHandlerUtils.mergeObject(objectNode, jsonObject);
        assertEquals(26, objectNode.get("id").asInt());
        assertEquals("pepe", objectNode.get("name").asText());
        assertEquals(3.4f, objectNode.get("miles").asDouble(), 0.1f);
        assertEquals(true, objectNode.get("married").asBoolean());
        assertEquals(1000000L, objectNode.get("population").asLong());
        ArrayNode arrayNode = (ArrayNode) objectNode.get("array");
        assertEquals(5, arrayNode.size());
        assertEquals(1, arrayNode.get(0).asInt());
        assertEquals("manolo", arrayNode.get(1).asText());
        assertEquals(true, arrayNode.get(2).asBoolean());
        assertEquals(3.5f, arrayNode.get(3).asDouble(), 0.1f);
        assertEquals(10000000000L, arrayNode.get(4).asLong());
        ArrayNode objectArrayNode = (ArrayNode) objectNode.get("objectArray");
        assertEquals(1, objectArrayNode.size());
        assertEquals("javierito", ((ObjectNode) objectArrayNode.get(0)).get("item").asText());
    }

}
