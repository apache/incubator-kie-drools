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
package org.kie.kogito.trusty.service.common.responses;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualResultsResponse extends CounterfactualExplainabilityRequest {

    public static final String SOLUTIONS_FIELD = "solutions";

    @JsonProperty(SOLUTIONS_FIELD)
    @NotNull(message = "solutions object must be provided.")
    private List<CounterfactualExplainabilityResult> solutions;

    public CounterfactualResultsResponse() {
    }

    public CounterfactualResultsResponse(@NotNull String executionId,
            @NotBlank String serviceUrl,
            @NotNull ModelIdentifier modelIdentifier,
            @NotNull String counterfactualId,
            @NotNull Collection<NamedTypedValue> originalInputs,
            @NotNull Collection<NamedTypedValue> goals,
            @NotNull Collection<CounterfactualSearchDomain> searchDomains,
            Long maxRunningTimeSeconds,
            @NotNull List<CounterfactualExplainabilityResult> solutions) {
        super(executionId,
                serviceUrl,
                modelIdentifier,
                counterfactualId,
                originalInputs,
                goals,
                searchDomains,
                maxRunningTimeSeconds);
        this.solutions = Objects.requireNonNull(solutions);
    }

    public List<CounterfactualExplainabilityResult> getSolutions() {
        return solutions;
    }
}
