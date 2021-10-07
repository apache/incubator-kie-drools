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
package org.kogito.workitem.openapi;

import java.io.IOException;

import org.kie.kogito.process.workitems.impl.OpenApiResultHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeResultHandler implements OpenApiResultHandler {

    private final ObjectMapper mapper;

    public JsonNodeResultHandler() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public Object apply(Object inputModel, JsonNode response) {
        return this.merge(response, fromModel(inputModel));
    }

    private JsonNode fromModel(Object inputModel) {
        if (inputModel instanceof JsonNode) {
            return (JsonNode) inputModel;
        }
        if (inputModel instanceof String) {

            try {
                return mapper.readTree((String) inputModel);
            } catch (JsonProcessingException e) {
                // fallback to valueToTree
            }
        }

        return mapper.valueToTree(inputModel);
    }

    public Object merge(JsonNode src, JsonNode dest) {
        if (dest.isArray()) {
            return ((ArrayNode) dest).add(src);
        }
        final ObjectReader reader = this.mapper.readerForUpdating(dest);
        try {
            if (src.isArray()) {
                ObjectNode node = (ObjectNode) reader.createObjectNode();
                node.set("response", src);
                return reader.readValue(node);
            }
            return reader.readValue(src);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to merge input model and JSON response: " + src, e);
        }
    }
}
