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

package org.kie.kogito.tracing.decision.event.variable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        defaultImpl = TypedVariable.Kind.class,
        property = "kind",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UnitVariable.class, name = "UNIT"),
        @JsonSubTypes.Type(value = CollectionVariable.class, name = "COLLECTION"),
        @JsonSubTypes.Type(value = StructureVariable.class, name = "STRUCTURE")
})
public abstract class TypedVariable {

    public enum Kind {
        UNIT,
        COLLECTION,
        STRUCTURE
    }

    @JsonProperty("kind")
    private Kind kind;

    @JsonProperty("type")
    private String type;

    protected TypedVariable() {
    }

    protected TypedVariable(Kind kind, String type) {
        this.kind = kind;
        this.type = type;
    }

    public Kind getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    @JsonIgnore
    public boolean isCollection() {
        return kind == Kind.COLLECTION;
    }

    public CollectionVariable toCollection() {
        if (!isCollection()) {
            throw new IllegalStateException(String.format("Can't convert TypedVariable of kind %s to COLLECTION", kind));
        }
        return (CollectionVariable) this;
    }

    @JsonIgnore
    public boolean isStructure() {
        return kind == Kind.STRUCTURE;
    }

    public StructureVariable toStructure() {
        if (!isStructure()) {
            throw new IllegalStateException(String.format("Can't convert TypedVariable of kind %s to STRUCTURE", kind));
        }
        return (StructureVariable) this;
    }

    @JsonIgnore
    public boolean isUnit() {
        return kind == Kind.UNIT;
    }

    public UnitVariable toUnit() {
        if (!isUnit()) {
            throw new IllegalStateException(String.format("Can't convert TypedVariable of kind %s to UNIT", kind));
        }
        return (UnitVariable) this;
    }
}