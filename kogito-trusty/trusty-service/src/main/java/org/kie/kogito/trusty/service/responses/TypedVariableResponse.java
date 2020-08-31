/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.responses;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.cloudevents.json.Json;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

import static org.kie.kogito.tracing.typedvalue.TypedValue.Kind.STRUCTURE;

public class TypedVariableResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("typeRef")
    private String typeRef;

    @JsonProperty("value")
    private JsonNode value;

    @JsonProperty("components")
    private List<JsonNode> components;

    private TypedVariableResponse() {
    }

    public TypedVariableResponse(String name, String typeRef, JsonNode value, List<JsonNode> components) {
        this.name = name;
        this.typeRef = typeRef;
        this.value = value;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public JsonNode getValue() {
        return value;
    }

    public List<JsonNode> getComponents() {
        return components;
    }

    public static TypedVariableResponse from(DecisionInput input) {
        return input != null ? from(input.getValue()) : null;
    }

    public static TypedVariableResponse from(TypedVariable value) {
        if (value == null) {
            return null;
        }

        switch (value.getKind()) {
            case COLLECTION:
                return fromCollection(value);
            case STRUCTURE:
                return fromStructure(value);
            case UNIT:
                return fromUnit(value);
        }

        throw new IllegalStateException(String.format("TypedVariable of kind %s can't be converted to TypedVariableResponse", value.getKind()));
    }

    private static TypedVariableResponse fromCollection(TypedVariable value) {
        boolean isCollectionOfStructures = value.getComponents() != null && value.getComponents().stream().anyMatch(t -> t.getKind() == STRUCTURE);

        // create array of all the values of the components
        // to be placed in the "value" field of the response
        // only if this **is not** a collection of structures
        JsonNode responseValue = (isCollectionOfStructures || value.getComponents() == null)
                ? null
                : value.getComponents().stream()
                        .map(TypedVariableResponse::fromUnit)
                        .map(TypedVariableResponse::getValue)
                        .collect(Json.MAPPER::createArrayNode, ArrayNode::add, ArrayNode::addAll);

        // create a list of lists of variables with all the values of the sub-components
        // to be placed in the "components" field of the response
        // only if this **is** a collection of structures
        List<JsonNode> responseComponents = (!isCollectionOfStructures || value.getComponents() == null)
                ? null
                : value.getComponents().stream()
                        .map(TypedVariableResponse::fromStructure)
                        .map(r -> r.getComponents().stream().collect(Json.MAPPER::createArrayNode, ArrayNode::add, ArrayNode::addAll))
                        .collect(Collectors.toList());

        return new TypedVariableResponse(value.getName(), value.getTypeRef(), responseValue, responseComponents);
    }

    private static TypedVariableResponse fromStructure(TypedVariable value) {
        List<JsonNode> components = value.getComponents() == null
                ? null
                : value.getComponents().stream().map(TypedVariableResponse::from).<JsonNode>map(Json.MAPPER::valueToTree).collect(Collectors.toList());
        return new TypedVariableResponse(value.getName(), value.getTypeRef(), null, components);
    }

    private static TypedVariableResponse fromUnit(TypedVariable value) {
        return new TypedVariableResponse(value.getName(), value.getTypeRef(), value.getValue(), null);
    }
}
