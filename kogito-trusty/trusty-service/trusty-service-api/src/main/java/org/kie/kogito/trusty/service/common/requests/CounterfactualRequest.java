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
package org.kie.kogito.trusty.service.common.requests;

import java.util.List;

import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualRequest {

    @JsonProperty("goals")
    private List<TypedVariableWithValue> goals;

    @JsonProperty("searchDomains")
    private List<CounterfactualSearchDomain> searchDomains;

    private CounterfactualRequest() {
    }

    public CounterfactualRequest(List<TypedVariableWithValue> goals,
            List<CounterfactualSearchDomain> searchDomains) {
        this.goals = goals;
        this.searchDomains = searchDomains;
    }

    public List<TypedVariableWithValue> getGoals() {
        return goals;
    }

    public List<CounterfactualSearchDomain> getSearchDomains() {
        return searchDomains;
    }
}
