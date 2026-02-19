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
package org.kie.kogito.index.graphql;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

public class JsonPropertyDataFetcher implements DataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPropertyDataFetcher.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static TypeReference stringListTypeRef = new TypeReference<List<String>>() {
    };
    private static TypeReference numberListTypeRef = new TypeReference<List<Number>>() {
    };
    private static TypeReference booleanListTypeRef = new TypeReference<List<Boolean>>() {
    };

    @Override
    public Object get(DataFetchingEnvironment environment) {
        String property = environment.getField().getName();
        Object source = environment.getSource();
        if (source instanceof JsonNode) {
            JsonNode jsonObject = (JsonNode) source;
            try {
                JsonNode value = jsonObject.findValue(property);
                if (value == null) {
                    return null;
                }
                switch (value.getNodeType()) {
                    case OBJECT:
                    case POJO:
                        return value;
                    case ARRAY:
                        if (!value.isNull() && !value.isEmpty()) {
                            switch (value.get(0).getNodeType()) {
                                case STRING:
                                    return mapper.readerFor(stringListTypeRef).readValue(value);
                                case NUMBER:
                                    return mapper.readerFor(numberListTypeRef).readValue(value);
                                case BOOLEAN:
                                    return mapper.readerFor(booleanListTypeRef).readValue(value);
                            }
                        }
                        return value;
                    case NUMBER:
                        return value.numberValue();
                    case BOOLEAN:
                        return value.asBoolean();
                    case STRING:
                        return value.asText();
                    case NULL:
                    case MISSING:
                    default:
                        return null;
                }
            } catch (Exception ex) {
                LOGGER.warn(ex.getMessage());
                return null;
            }
        }
        // Fallback to PropertyDataFetcher for non-JsonNode sources (e.g. POJOs in PostgreSQL).
        // This allows JsonPropertyDataFetcher to be used as the default data fetcher for all
        // domain types regardless of the storage backend.
        return PropertyDataFetcher.fetching(property).get(environment);
    }
}
