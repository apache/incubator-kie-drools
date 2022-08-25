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

import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeConverter implements Function<String, JsonNode> {

    private Supplier<ObjectMapper> objectMapper;

    public JsonNodeConverter() {
        this(ObjectMapperFactory::get);
    }

    public JsonNodeConverter(Supplier<ObjectMapper> mapper) {
        this.objectMapper = mapper;
    }

    @Override
    public JsonNode apply(String t) {
        try {
            return objectMapper.get().readTree(t);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid value for json node " + t);
        }
    }

}
