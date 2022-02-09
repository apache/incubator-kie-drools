/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.List;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;

/**
 * A request for LIME hyperparameter optimization.
 */
public class LimeOptimizationRequest {

    private final LimeConfig limeConfig;
    private final PredictionProvider predictionProvider;
    private final List<Prediction> predictions;
    private final LimeExplainer explainer;

    public LimeOptimizationRequest(LimeConfig limeConfig, PredictionProvider predictionProvider, List<Prediction> predictions, LimeExplainer explainer) {
        this.limeConfig = limeConfig;
        this.predictionProvider = predictionProvider;
        this.predictions = predictions;
        this.explainer = explainer;
    }

    public LimeConfig getLimeConfig() {
        return limeConfig;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public PredictionProvider getPredictionProvider() {
        return predictionProvider;
    }

    public LimeExplainer getExplainer() {
        return explainer;
    }
}
