/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
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

package org.kie.kogito.index.graphql;

import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPropertyDataFetcher extends PropertyDataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPropertyDataFetcher.class);
    
    public JsonPropertyDataFetcher(String propertyName) {
        super(propertyName);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        Object source = environment.getSource();
        if (source instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) source;
            String jsonPointer = "/" + getPropertyName();
            try {
                JsonValue value = jsonObject.getValue(jsonPointer);
                switch (value.getValueType()) {
                    case OBJECT:
                        return value.asJsonObject();
                    case ARRAY:
                        return value.asJsonArray();
                    case NUMBER:
                        return ((JsonNumber) value).numberValue();
                    case TRUE:
                    case FALSE:
                        return value.toString();
                    case STRING:
                        return ((JsonString) value).getString();
                    case NULL:
                    default:
                        return null;
                }
            } catch (JsonException ex){
                LOGGER.warn(ex.getMessage());
                return null;
            }
        }
        return super.get(environment);
    }
}
