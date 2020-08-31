/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExplainabilityResult {

    public static final String EXECUTION_ID_FIELD = "executionId";
    public static final String SALIENCIES_FIELD = "saliencies";

    @JsonProperty(EXECUTION_ID_FIELD)
    private String executionId;

    @JsonProperty(SALIENCIES_FIELD)
    private Map<String, Saliency> saliencies;

    public ExplainabilityResult() {
    }

    public ExplainabilityResult(String executionId, Map<String, Saliency> saliencies) {
        this.executionId = executionId;
        this.saliencies = saliencies;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Map<String, Saliency> getSaliencies() {
        return saliencies;
    }

    public void setSaliencies(Map<String, Saliency> saliencies) {
        this.saliencies = saliencies;
    }
}
