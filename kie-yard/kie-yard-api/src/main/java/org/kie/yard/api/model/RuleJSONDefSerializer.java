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
package org.kie.yard.api.model;

import jakarta.json.*;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class RuleJSONDefSerializer
        implements JsonbSerializer<Rule>,
        JsonbDeserializer<Rule> {

    private int rowNumber = 1;

    @Override
    public Rule deserialize(JsonParser parser, DeserializationContext deserializationContext, Type type) {

        final JsonValue value = parser.getValue();
        if (value instanceof JsonObject object) {
            final JsonValue when = object.get("when");
            final WhenThenRule whenThenRule = new WhenThenRule(rowNumber++);
            whenThenRule.setWhen(getItems(when));
            final JsonValue then = value.asJsonObject().get("then");
            whenThenRule.setThen(getItem(then));
            return whenThenRule;
        } else if (value instanceof JsonArray array) {
            return new InlineRule(rowNumber++, getItems(array));
        } else {
            throw new IllegalArgumentException("Unknown rule format.");
        }
    }

    @Override
    public void serialize(Rule o, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        // Not needed, we never serialize.
    }

    private List<Comparable> getItems(final JsonValue jsonValue) {
        final List<Comparable> result = new ArrayList<>();
        if (jsonValue instanceof JsonArray array) {
            for (JsonValue item : array) {
                result.add(getItem(item));
            }
        } else {
            result.add(getItem(jsonValue));
        }
        return result;
    }

    private Comparable getItem(JsonValue jsonValue) {
        if (jsonValue instanceof JsonNumber number) {
            return number.bigDecimalValue();
        } else if (jsonValue.getValueType() == JsonValue.ValueType.FALSE) {
            return false;
        } else if (jsonValue.getValueType() == JsonValue.ValueType.TRUE) {
            return true;
        } else if (jsonValue instanceof JsonString string) {
            return string.getString();
        } else {
            return jsonValue.toString();
        }
    }

}
