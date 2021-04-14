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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CounterfactualDomainRange extends CounterfactualDomain {

    public static final String LOWER_BOUND = "lowerBound";
    public static final String UPPER_BOUND = "upperBound";

    @JsonProperty(LOWER_BOUND)
    private JsonNode lowerBound;

    @JsonProperty(UPPER_BOUND)
    private JsonNode upperBound;

    public CounterfactualDomainRange() {
    }

    public CounterfactualDomainRange(JsonNode lowerBound, JsonNode upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public Type getType() {
        return Type.RANGE;
    }

    public JsonNode getLowerBound() {
        return this.lowerBound;
    }

    public JsonNode getUpperBound() {
        return this.upperBound;
    }

    //-------------
    // Test methods
    //-------------

    public void setLowerBound(JsonNode lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(JsonNode upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return "DomainRange{" +
                "lowerBound=" + lowerBound.asText() +
                ", upperBound=" + upperBound.asText() +
                "}";
    }
}
