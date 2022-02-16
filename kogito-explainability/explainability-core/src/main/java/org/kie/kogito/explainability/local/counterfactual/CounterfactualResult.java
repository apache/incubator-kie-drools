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
import java.util.UUID;

import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.PredictionOutput;

/**
 * Represents the result of a counterfactual search.
 * Entities represent the counterfactual features and the {@link org.kie.kogito.explainability.model.PredictionOutput}
 * contains the prediction result for the counterfactual, including the prediction score, if available.
 */
public class CounterfactualResult {

    private List<CounterfactualEntity> entities;
    private List<PredictionOutput> output;
    private List<Feature> features;
    private boolean valid;

    private UUID solutionId;
    private UUID executionId;
    private long sequenceId;

    public CounterfactualResult(List<CounterfactualEntity> entities,
            List<Feature> features,
            List<PredictionOutput> output,
            boolean valid,
            UUID solutionId,
            UUID executionId,
            long sequenceId) {
        this.entities = entities;
        this.features = features;
        this.output = output;
        this.valid = valid;
        this.solutionId = solutionId;
        this.executionId = executionId;
        this.sequenceId = sequenceId;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public List<CounterfactualEntity> getEntities() {
        return entities;
    }

    public List<PredictionOutput> getOutput() {
        return output;
    }

    public boolean isValid() {
        return valid;
    }

    public UUID getSolutionId() {
        return solutionId;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public long getSequenceId() {
        return sequenceId;
    }
}
