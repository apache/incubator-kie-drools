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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SaliencyModel {

    public static final String OUTCOME_ID_FIELD = "outcomeId";
    public static final String OUTCOME_NAME_FIELD = "outcomeName";
    public static final String FEATURE_IMPORTANCE_FIELD = "featureImportance";

    @JsonProperty(OUTCOME_ID_FIELD)
    private String outcomeId;

    @JsonProperty(OUTCOME_NAME_FIELD)
    private String outcomeName;

    @JsonProperty(FEATURE_IMPORTANCE_FIELD)
    private List<FeatureImportanceModel> featureImportance;

    public SaliencyModel() {
    }

    public SaliencyModel(String outcomeId, String outcomeName, List<FeatureImportanceModel> featureImportance) {
        this.outcomeId = outcomeId;
        this.outcomeName = outcomeName;
        this.featureImportance = featureImportance;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
    }

    public String getOutcomeName() {
        return outcomeName;
    }

    public void setOutcomeName(String outcomeName) {
        this.outcomeName = outcomeName;
    }

    public List<FeatureImportanceModel> getFeatureImportance() {
        return featureImportance;
    }

    public void setFeatureImportance(List<FeatureImportanceModel> featureImportance) {
        this.featureImportance = featureImportance;
    }
}
