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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualExplainabilityRequest {

    public static final String EXECUTION_ID_FIELD = "executionId";
    public static final String COUNTERFACTUAL_ID_FIELD = "counterfactualId";
    public static final String COUNTERFACTUAL_GOALS = "goals";
    public static final String COUNTERFACTUAL_SEARCH_DOMAINS = "searchDomains";

    @JsonProperty(EXECUTION_ID_FIELD)
    @NotNull(message = "executionId must be provided.")
    private String executionId;

    @JsonProperty(COUNTERFACTUAL_ID_FIELD)
    @NotNull(message = "counterfactualId must be provided.")
    private String counterfactualId;

    @JsonProperty(COUNTERFACTUAL_GOALS)
    @NotNull(message = "goals object must be provided.")
    private Collection<TypedVariableWithValue> goals;

    @JsonProperty(COUNTERFACTUAL_SEARCH_DOMAINS)
    @NotNull(message = "searchDomains object must be provided.")
    private Collection<CounterfactualSearchDomain> searchDomains;

    public CounterfactualExplainabilityRequest() {
    }

    public CounterfactualExplainabilityRequest(@NotNull String executionId,
            @NotNull String counterfactualId) {
        this(executionId, counterfactualId, new ArrayList<>(), new ArrayList<>());
    }

    public CounterfactualExplainabilityRequest(@NotNull String executionId,
            @NotNull String counterfactualId,
            @NotNull Collection<TypedVariableWithValue> goals,
            @NotNull Collection<CounterfactualSearchDomain> searchDomains) {
        this.executionId = Objects.requireNonNull(executionId);
        this.counterfactualId = Objects.requireNonNull(counterfactualId);
        this.goals = Objects.requireNonNull(goals);
        this.searchDomains = Objects.requireNonNull(searchDomains);
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getCounterfactualId() {
        return counterfactualId;
    }

    public Collection<TypedVariableWithValue> getGoals() {
        return goals;
    }

    public Collection<CounterfactualSearchDomain> getSearchDomains() {
        return searchDomains;
    }

    //-------------
    // Test methods
    //-------------
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public void setCounterfactualId(String counterfactualId) {
        this.counterfactualId = counterfactualId;
    }

    public void setGoals(Collection<TypedVariableWithValue> goals) {
        this.goals = goals;
    }

    public void setSearchDomains(Collection<CounterfactualSearchDomain> searchDomains) {
        this.searchDomains = searchDomains;
    }

}
