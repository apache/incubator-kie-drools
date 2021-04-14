/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

public abstract class TypedVariable<T extends TypedVariable<?>> {

    public static final String KIND_FIELD = "kind";
    public static final String NAME_FIELD = "name";
    public static final String TYPE_REF_FIELD = "typeRef";
    public static final String VALUE_FIELD = "value";
    public static final String COMPONENTS_FIELD = "components";

    @JsonProperty(KIND_FIELD)
    private TypedValue.Kind kind;

    @JsonProperty(NAME_FIELD)
    private String name;

    @JsonProperty(TYPE_REF_FIELD)
    private String typeRef;

    @JsonProperty(COMPONENTS_FIELD)
    private Collection<T> components;

    public TypedVariable() {
    }

    public TypedVariable(TypedValue.Kind kind, String name, String typeRef, Collection<T> components) {
        this.kind = kind;
        this.name = name;
        this.typeRef = typeRef;
        this.components = components;
    }

    public TypedValue.Kind getKind() {
        return kind;
    }

    public void setKind(TypedValue.Kind kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
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

    public Collection<T> getComponents() {
        return components;
    }

    public void setComponents(Collection<T> components) {
        this.components = components;
    }
}
