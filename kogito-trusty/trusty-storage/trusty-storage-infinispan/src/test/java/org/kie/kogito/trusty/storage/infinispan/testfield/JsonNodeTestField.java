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
package org.kie.kogito.trusty.storage.infinispan.testfield;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeTestField<M> extends StringTestField<M> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public JsonNodeTestField(String fieldName, JsonNode fieldValue, Function<M, JsonNode> getter, BiConsumer<M, JsonNode> setter) {
        super(fieldName, stringFromJson(fieldValue), obj -> stringFromJson(getter.apply(obj)), (obj, value) -> setter.accept(obj, jsonFromString(value)));
    }

    private static JsonNode jsonFromString(String value) {
        try {
            return AbstractMarshaller.jsonFromString(MAPPER, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String stringFromJson(JsonNode value) {
        try {
            return AbstractMarshaller.stringFromJson(MAPPER, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
