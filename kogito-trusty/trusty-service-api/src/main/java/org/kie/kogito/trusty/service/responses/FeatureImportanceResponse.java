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
package org.kie.kogito.trusty.service.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureImportanceResponse {

    @JsonProperty("featureName")
    private String featureName;

    @JsonProperty("featureScore")
    private Double featureScore;

    private FeatureImportanceResponse() {
    }

    public FeatureImportanceResponse(String featureName, Double featureScore) {
        this.featureName = featureName;
        this.featureScore = featureScore;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getFeatureScore() {
        return featureScore;
    }
}
