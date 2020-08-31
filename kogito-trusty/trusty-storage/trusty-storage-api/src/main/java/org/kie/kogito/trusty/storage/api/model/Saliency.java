/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.storage.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Saliency {

    public static final String FEATURE_IMPORTANCE_FIELD = "featureImportance";

    @JsonProperty(FEATURE_IMPORTANCE_FIELD)
    private List<FeatureImportance> featureImportance;

    public Saliency() {
    }

    public Saliency(List<FeatureImportance> featureImportance) {
        this.featureImportance = featureImportance;
    }

    public List<FeatureImportance> getFeatureImportance() {
        return featureImportance;
    }

    public void setFeatureImportance(List<FeatureImportance> featureImportance) {
        this.featureImportance = featureImportance;
    }
}
