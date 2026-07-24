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
package org.kie.kogito.persistence.mongodb.model;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ModelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelUtils.class);

    public static final String ID = "id";

    public static final String MONGO_ID = "_id";

    public static final String ATTRIBUTE_DELIMITER = ".";

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private ModelUtils() {
    }

    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString())).build();

    public static ZonedDateTime instantToZonedDateTime(Long milli) {
        return Optional.ofNullable(milli).map(time -> Instant.ofEpochMilli(time).atZone(ZoneOffset.UTC)).orElse(null);
    }

    public static Long zonedDateTimeToInstant(ZonedDateTime time) {
        return Optional.ofNullable(time).map(t -> t.toInstant().toEpochMilli()).orElse(null);
    }

    public static ObjectNode documentToJsonNode(Document document) {
        return Optional.ofNullable(document).map(doc -> {
            try {
                return (ObjectNode) MAPPER.readTree(doc.toJson(jsonWriterSettings));
            } catch (JsonProcessingException ex) {
                LOGGER.error("Error trying to parse Process Variables", ex);
                return null;
            }
        }).orElse(null);
    }

    public static Document jsonNodeToDocument(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode).map(json -> Document.parse(json.toString())).orElse(null);
    }

    public static <T> T documentToObject(Document document, Class<T> type, UnaryOperator<String> converter) {
        ObjectNode node = documentToJsonNode(document);
        return Optional.ofNullable(node).map(n -> convertAttributes(node, Optional.empty(), converter))
                .map(n -> MAPPER.convertValue(n, type)).orElse(null);
    }

    static ObjectNode convertAttributes(ObjectNode node, Optional<String> parentName, UnaryOperator<String> converter) {
        ObjectNode objectNode = MAPPER.createObjectNode();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            String key = parentName.map(p -> p + "." + entry.getKey()).orElse(entry.getKey());
            String name = converter.apply(key);
            JsonNode value = entry.getValue();
            if (value.isObject()) {
                ObjectNode childNode = convertAttributes((ObjectNode) value, Optional.of(key), converter);
                objectNode.set(name, childNode);
            } else if (value.isArray()) {
                ArrayNode childArray = MAPPER.createArrayNode();
                for (JsonNode childNode : value) {
                    if (childNode.isObject()) {
                        childArray.add(convertAttributes((ObjectNode) childNode, Optional.of(key), converter));
                    } else {
                        childArray.add(childNode);
                    }
                }
                objectNode.set(name, childArray);
            } else {
                objectNode.set(name, value);
            }
        }
        return objectNode;
    }
}
