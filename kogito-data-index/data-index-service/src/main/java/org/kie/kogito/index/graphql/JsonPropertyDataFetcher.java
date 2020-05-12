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

import com.fasterxml.jackson.databind.JsonNode;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPropertyDataFetcher implements DataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPropertyDataFetcher.class);

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
                    case ARRAY:
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
        return null;
    }
}
