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
package org.kie.kogito.explainability.local.counterfactual;

import java.util.List;

import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.PredictionOutput;

/**
 * Represents the result of a counterfactual search.
 * Entities represent the counterfactual features and the {@link org.kie.kogito.explainability.model.PredictionOutput}
 * contains the prediction result for the counterfactual, including the prediction score, if available.
 */
public class CounterfactualResult {

    private List<CounterfactualEntity> entities;
    private List<PredictionOutput> output;

    public CounterfactualResult(List<CounterfactualEntity> entities, List<PredictionOutput> output) {
        this.entities = entities;
        this.output = output;
    }

    public List<CounterfactualEntity> getEntities() {
        return entities;
    }

    public List<PredictionOutput> getOutput() {
        return output;
    }
}
