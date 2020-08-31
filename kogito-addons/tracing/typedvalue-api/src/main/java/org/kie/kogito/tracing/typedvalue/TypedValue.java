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

package org.kie.kogito.tracing.typedvalue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        defaultImpl = TypedValue.Kind.class,
        property = "kind",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UnitValue.class, name = "UNIT"),
        @JsonSubTypes.Type(value = CollectionValue.class, name = "COLLECTION"),
        @JsonSubTypes.Type(value = StructureValue.class, name = "STRUCTURE")
})
public abstract class TypedValue {

    public enum Kind {
        UNIT,
        COLLECTION,
        STRUCTURE
    }

    @JsonProperty("kind")
    private Kind kind;

    @JsonProperty("type")
    private String type;

    protected TypedValue() {
    }

    protected TypedValue(Kind kind, String type) {
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

    public CollectionValue toCollection() {
        if (!isCollection()) {
            throw new IllegalStateException(String.format("Can't convert TypedValue of kind %s to COLLECTION", kind));
        }
        return (CollectionValue) this;
    }

    @JsonIgnore
    public boolean isStructure() {
        return kind == Kind.STRUCTURE;
    }

    public StructureValue toStructure() {
        if (!isStructure()) {
            throw new IllegalStateException(String.format("Can't convert TypedValue of kind %s to STRUCTURE", kind));
        }
        return (StructureValue) this;
    }

    @JsonIgnore
    public boolean isUnit() {
        return kind == Kind.UNIT;
    }

    public UnitValue toUnit() {
        if (!isUnit()) {
            throw new IllegalStateException(String.format("Can't convert TypedValue of kind %s to UNIT", kind));
        }
        return (UnitValue) this;
    }
}