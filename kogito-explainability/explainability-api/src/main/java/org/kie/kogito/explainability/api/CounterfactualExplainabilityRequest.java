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
package org.kie.kogito.explainability.api;

import java.util.Collection;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualExplainabilityRequest extends BaseExplainabilityRequest {

    public static final String EXPLAINABILITY_TYPE_NAME = "Counterfactual";
    public static final String COUNTERFACTUAL_ID_FIELD = "counterfactualId";
    public static final String COUNTERFACTUAL_ORIGINAL_INPUTS_FIELD = "originalInputs";
    public static final String COUNTERFACTUAL_GOALS_FIELD = "goals";
    public static final String COUNTERFACTUAL_SEARCH_DOMAINS_FIELD = "searchDomains";
    public static final String MAX_RUNNING_TIME_SECONDS_FIELD = "maxRunningTimeSeconds";

    @JsonProperty(COUNTERFACTUAL_ID_FIELD)
    @NotNull(message = "counterfactualId must be provided.")
    private String counterfactualId;

    @JsonProperty(COUNTERFACTUAL_ORIGINAL_INPUTS_FIELD)
    @NotNull(message = "originalInputs object must be provided.")
    private Collection<NamedTypedValue> originalInputs;

    @JsonProperty(COUNTERFACTUAL_GOALS_FIELD)
    @NotNull(message = "goals object must be provided.")
    private Collection<NamedTypedValue> goals;

    @JsonProperty(COUNTERFACTUAL_SEARCH_DOMAINS_FIELD)
    @NotNull(message = "searchDomains object must be provided.")
    private Collection<CounterfactualSearchDomain> searchDomains;

    @JsonProperty(MAX_RUNNING_TIME_SECONDS_FIELD)
    @NotNull(message = "maxRunningTimeSeconds must be provided.")
    private Long maxRunningTimeSeconds;

    public CounterfactualExplainabilityRequest() {
    }

    public CounterfactualExplainabilityRequest(@NotNull String executionId,
            @NotBlank String serviceUrl,
            @NotNull ModelIdentifier modelIdentifier,
            @NotNull String counterfactualId,
            @NotNull Collection<NamedTypedValue> originalInputs,
            @NotNull Collection<NamedTypedValue> goals,
            @NotNull Collection<CounterfactualSearchDomain> searchDomains,
            Long maxRunningTimeSeconds) {
        super(executionId, serviceUrl, modelIdentifier);
        this.counterfactualId = Objects.requireNonNull(counterfactualId);
        this.originalInputs = Objects.requireNonNull(originalInputs);
        this.goals = Objects.requireNonNull(goals);
        this.searchDomains = Objects.requireNonNull(searchDomains);
        this.maxRunningTimeSeconds = maxRunningTimeSeconds;
    }

    public String getCounterfactualId() {
        return counterfactualId;
    }

    public Collection<NamedTypedValue> getOriginalInputs() {
        return originalInputs;
    }

    public Collection<NamedTypedValue> getGoals() {
        return goals;
    }

    public Collection<CounterfactualSearchDomain> getSearchDomains() {
        return searchDomains;
    }

    public Long getMaxRunningTimeSeconds() {
        return maxRunningTimeSeconds;
    }

}
