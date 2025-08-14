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
package org.kie.kogito.jackson.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;

public class FunctionBaseJsonNode extends BaseJsonNode {

    private static final long serialVersionUID = 1L;

    @Override
    public JsonToken asToken() {
        return JsonToken.START_OBJECT;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public <T extends JsonNode> T deepCopy() {
        return (T) this;
    }

    @Override
    public JsonNode get(int index) {
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode path(String fieldName) {
        return get(fieldName);
    }

    @Override
    public JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    @Override
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingProperty());
    }

    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.OBJECT;
    }

    @Override
    public String asText() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public JsonNode findValue(String fieldName) {
        return get(fieldName);
    }

    @Override
    public JsonNode findParent(String fieldName) {
        return null;
    }

    @Override
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        foundSoFar.add(findValue(fieldName));
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        foundSoFar.add(findValue(fieldName).asText());
        return foundSoFar;
    }

    @Override
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        foundSoFar.add(findParent(fieldName));
        return foundSoFar;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException {
        // not serialize 
    }

    @Override
    public void serializeWithType(JsonGenerator jgen,
            SerializerProvider provider,
            TypeSerializer typeSer) throws IOException {
        // not serialize 
    }

}