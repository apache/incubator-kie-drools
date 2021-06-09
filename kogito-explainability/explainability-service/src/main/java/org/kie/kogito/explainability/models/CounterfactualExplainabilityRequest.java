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
package org.kie.kogito.explainability.models;

import java.util.Map;

import org.kie.kogito.explainability.api.CounterfactualSearchDomainDto;
import org.kie.kogito.tracing.typedvalue.TypedValue;

public class CounterfactualExplainabilityRequest extends BaseExplainabilityRequest {

    private final String counterfactualId;
    private final Map<String, TypedValue> originalInputs;
    private final Map<String, TypedValue> goals;
    private final Map<String, CounterfactualSearchDomainDto> searchDomains;

    public CounterfactualExplainabilityRequest(String executionId,
            String counterfactualId,
            String serviceUrl,
            ModelIdentifier modelIdentifier,
            Map<String, TypedValue> originalInputs,
            Map<String, TypedValue> goals,
            Map<String, CounterfactualSearchDomainDto> searchDomains) {
        super(executionId, serviceUrl, modelIdentifier);
        this.counterfactualId = counterfactualId;
        this.originalInputs = originalInputs;
        this.goals = goals;
        this.searchDomains = searchDomains;
    }

    public String getCounterfactualId() {
        return counterfactualId;
    }

    public Map<String, TypedValue> getOriginalInputs() {
        return originalInputs;
    }

    public Map<String, TypedValue> getGoals() {
        return goals;
    }

    public Map<String, CounterfactualSearchDomainDto> getSearchDomains() {
        return searchDomains;
    }
}
