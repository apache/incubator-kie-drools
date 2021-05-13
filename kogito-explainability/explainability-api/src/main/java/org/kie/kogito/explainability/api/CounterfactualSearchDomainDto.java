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
package org.kie.kogito.explainability.api;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        defaultImpl = CounterfactualSearchDomainDto.Kind.class,
        property = CounterfactualSearchDomainDto.KIND_FIELD,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CounterfactualSearchDomainUnitDto.class, name = "UNIT"),
        @JsonSubTypes.Type(value = CounterfactualSearchDomainStructureDto.class, name = "STRUCTURE")
})
public abstract class CounterfactualSearchDomainDto {

    public static final String KIND_FIELD = "kind";
    public static final String TYPE_FIELD = "type";

    public enum Kind {
        UNIT,
        COLLECTION,
        STRUCTURE
    }

    @JsonProperty(KIND_FIELD)
    @NotNull(message = "kind must be provided.")
    private CounterfactualSearchDomainDto.Kind kind;

    @JsonProperty(TYPE_FIELD)
    @NotNull(message = "type object must be provided.")
    private String type;

    protected CounterfactualSearchDomainDto() {
    }

    protected CounterfactualSearchDomainDto(@NotNull CounterfactualSearchDomainDto.Kind kind,
            @NotNull String type) {
        this.kind = Objects.requireNonNull(kind);
        this.type = Objects.requireNonNull(type);
    }

    public CounterfactualSearchDomainDto.Kind getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    @JsonIgnore
    public boolean isUnit() {
        return kind == CounterfactualSearchDomainDto.Kind.UNIT;
    }

    public CounterfactualSearchDomainUnitDto toUnit() {
        if (!isUnit()) {
            throw new IllegalStateException(String.format("Can't convert TypedValue of kind %s to UNIT", kind));
        }
        return (CounterfactualSearchDomainUnitDto) this;
    }

    @JsonIgnore
    public boolean isCollection() {
        return kind == CounterfactualSearchDomainDto.Kind.COLLECTION;
    }

    public CounterfactualSearchDomainCollectionDto toCollection() {
        if (!isCollection()) {
            throw new IllegalStateException(String.format("Can't convert TypedValue of kind %s to COLLECTION", kind));
        }
        return (CounterfactualSearchDomainCollectionDto) this;
    }

    @JsonIgnore
    public boolean isStructure() {
        return kind == CounterfactualSearchDomainDto.Kind.STRUCTURE;
    }

    public CounterfactualSearchDomainStructureDto toStructure() {
        if (!isStructure()) {
            throw new IllegalStateException(String.format("Can't convert TypedValue of kind %s to STRUCTURE", kind));
        }
        return (CounterfactualSearchDomainStructureDto) this;
    }
}
