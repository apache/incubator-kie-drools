/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.storage.api.model;

import java.util.Collection;

import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class TypedVariableWithValue extends TypedVariable<TypedVariableWithValue> {

    @JsonProperty("value")
    private JsonNode value;

    public static TypedVariableWithValue buildCollection(String name, String typeRef, Collection<TypedVariableWithValue> components) {
        return new TypedVariableWithValue(TypedValue.Kind.COLLECTION, name, typeRef, null, components);
    }

    public static TypedVariableWithValue buildStructure(String name, String typeRef, Collection<TypedVariableWithValue> components) {
        return new TypedVariableWithValue(TypedValue.Kind.STRUCTURE, name, typeRef, null, components);
    }

    public static TypedVariableWithValue buildUnit(String name, String typeRef, JsonNode value) {
        return new TypedVariableWithValue(TypedValue.Kind.UNIT, name, typeRef, value, null);
    }

    public TypedVariableWithValue() {
        super();
    }

    public TypedVariableWithValue(TypedValue.Kind kind, String name, String typeRef, JsonNode value, Collection<TypedVariableWithValue> components) {
        super(kind, name, typeRef, components);
        this.value = value;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }
}
