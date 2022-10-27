/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.impl;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEventData;
import io.cloudevents.jackson.JsonCloudEventData;

public class JsonNodeCloudEventDataConverter extends AbstractCloudEventDataConverter<JsonNode> {

    private ObjectMapper objectMapper;

    public JsonNodeCloudEventDataConverter(ObjectMapper objectMapper) {
        super(JsonNode.class);
        this.objectMapper = objectMapper;
    }

    @Override
    protected Optional<JsonNode> isTargetInstanceAlready(CloudEventData value) {
        return value instanceof JsonCloudEventData ? Optional.of(((JsonCloudEventData) value).getNode()) : super.isTargetInstanceAlready(value);
    }

    @Override
    protected JsonNode toValue(CloudEventData value) throws IOException {
        return objectMapper.readTree(value.toBytes());
    }
}
