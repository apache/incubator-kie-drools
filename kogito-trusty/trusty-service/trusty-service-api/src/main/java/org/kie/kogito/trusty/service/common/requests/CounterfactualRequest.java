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
package org.kie.kogito.trusty.service.common.requests;

import java.util.List;
import java.util.Objects;

import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.NamedTypedValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualRequest {

    @JsonProperty("goals")
    @NotNull(message = "goals object must be provided.")
    private List<NamedTypedValue> goals;

    @JsonProperty("searchDomains")
    @NotNull(message = "searchDomains object must be provided.")
    private List<CounterfactualSearchDomain> searchDomains;

    private CounterfactualRequest() {
    }

    public CounterfactualRequest(@NotNull List<NamedTypedValue> goals,
            @NotNull List<CounterfactualSearchDomain> searchDomains) {
        this.goals = Objects.requireNonNull(goals);
        this.searchDomains = Objects.requireNonNull(searchDomains);
    }

    public List<NamedTypedValue> getGoals() {
        return goals;
    }

    public List<CounterfactualSearchDomain> getSearchDomains() {
        return searchDomains;
    }
}
