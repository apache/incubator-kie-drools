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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = CounterfactualDomain.TYPE)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CounterfactualDomainRange.class, name = CounterfactualDomain.RANGE),
        @JsonSubTypes.Type(value = CounterfactualDomainCategorical.class, name = CounterfactualDomain.CATEGORICAL)
})
public abstract class CounterfactualDomain {

    public static final String TYPE = "type";
    public static final String CATEGORICAL = "categorical";
    public static final String RANGE = "range";

    @JsonProperty(TYPE)
    @NotNull(message = "type object must be provided.")
    @SuppressWarnings("unused")
    protected Type type = getType();

    public abstract Type getType();

    public enum Type {
        CATEGORICAL,
        RANGE
    }

    //-------------
    // Test methods
    //-------------

    public void setType(Type type) {
        this.type = type;
    }
}
