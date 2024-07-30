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
package org.kie.kogito.serverless.workflow.parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.DEFS_PREFIX;

class JsonSchemaReader {

    JsonSchemaReader() {
    }

    private static class Counter {
        private int count;

        public int next() {
            return count++;
        }
    }

    private static class JsonSchema {
        private final String id;
        private final JsonNode node;

        public JsonSchema(ObjectNode node, Counter counter) {
            this.node = node;
            this.id = getId(node, counter);
        }

        private static String getId(JsonNode schemaContent, Counter counter) {
            JsonNode title = schemaContent.get("title");
            return title != null ? title.asText() + "_" + counter.next() : "nested_" + counter.next();
        }
    }

    static JsonNode read(String baseURI, byte[] content) {
        try {
            ObjectNode node = ObjectMapperFactory.get().readValue(content, ObjectNode.class);
            JsonNode id = node.get("$id");
            if (id != null) {
                baseURI = id.asText();
            }
            Objects.requireNonNull(baseURI, "BaseURI must not be null");
            Map<String, JsonSchema> schemas = new HashMap<>();
            Counter counter = new Counter();
            replaceRefsWithDefs(node, baseURI, schemas, counter);
            if (!schemas.isEmpty()) {
                node.set("$defs", fillDefs(schemas));
            }
            return node;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static JsonNode fillDefs(Map<String, JsonSchema> schemas) {
        ObjectNode node = ObjectMapperFactory.get().createObjectNode();
        schemas.values().stream().collect(Collectors.toMap(v -> v.id, v -> v.node)).forEach(node::set);
        return node;
    }

    private static void replaceRefsWithDefs(JsonNode node, String baseURI, Map<String, JsonSchema> schemas, Counter counter) {
        if (node.isArray()) {
            node.elements().forEachRemaining(n -> replaceRefsWithDefs(n, baseURI, schemas, counter));
        } else if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            JsonNode refNode = objectNode.get("$ref");
            if (refNode != null) {
                JsonSchema schema = schemas.computeIfAbsent(refNode.asText(), schemaRef -> readSchema(schemaRef, baseURI, counter));
                objectNode.put("$ref", DEFS_PREFIX + schema.id);
            } else {
                objectNode.elements().forEachRemaining(n -> replaceRefsWithDefs(n, baseURI, schemas, counter));
            }
        }
    }

    private static JsonSchema readSchema(String schemaRef, String baseURI, Counter counter) {
        try {
            return new JsonSchema(
                    ObjectMapperFactory.get().readValue(URIContentLoaderFactory.readAllBytes(URIContentLoaderFactory.builder(schemaRef).withBaseURI(baseURI)), ObjectNode.class), counter);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
