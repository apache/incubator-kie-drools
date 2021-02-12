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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.kie.kogito.tracing.typedvalue.TypedValue.Kind;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypedVariable {

    public static final String KIND_FIELD = "kind";
    public static final String NAME_FIELD = "name";
    public static final String TYPE_REF_FIELD = "typeRef";
    public static final String VALUE_FIELD = "value";
    public static final String COMPONENTS_FIELD = "components";

    @JsonProperty(KIND_FIELD)
    private Kind kind;

    @JsonProperty(NAME_FIELD)
    private String name;

    @JsonProperty(TYPE_REF_FIELD)
    private String typeRef;

    @JsonProperty(VALUE_FIELD)
    private JsonNode value;

    @JsonProperty(COMPONENTS_FIELD)
    private Collection<TypedVariable> components;

    public TypedVariable() {
    }

    public TypedVariable(Kind kind, String name, String typeRef, JsonNode value, Collection<TypedVariable> components) {
        this.kind = kind;
        this.name = name;
        this.typeRef = typeRef;
        this.value = value;
        this.components = components;
    }

    public Kind getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }

    public Collection<TypedVariable> getComponents() {
        return components;
    }

    public void setComponents(Collection<TypedVariable> components) {
        this.components = components;
    }

    public static TypedVariable buildCollection(String name, String typeRef, Collection<TypedVariable> components) {
        return new TypedVariable(Kind.COLLECTION, name, typeRef, null, components);
    }

    public static TypedVariable buildStructure(String name, String typeRef, Collection<TypedVariable> components) {
        return new TypedVariable(Kind.STRUCTURE, name, typeRef, null, components);
    }

    public static TypedVariable buildUnit(String name, String typeRef, JsonNode value) {
        return new TypedVariable(Kind.UNIT, name, typeRef, value, null);
    }
}
