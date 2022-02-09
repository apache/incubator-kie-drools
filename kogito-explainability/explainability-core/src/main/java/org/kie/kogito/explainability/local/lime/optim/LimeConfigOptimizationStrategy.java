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
 * Strategy used to decide when and how to perform LIME hyperparameter optimization.
 * <p>
 * See also {@link LimeOptimizationService}
 */
public interface LimeConfigOptimizationStrategy {

    /**
     * Callback used to decide whether an optimization should be performed given the current: list of recorded
     * predictions, model, execution config, LIME explaier.
     *
     * @param recordedPredictions the list of recently recorded predictions
     * @param model the system whose predictions need to be explained
     * @param limeExplainer the LIME explainer used to produce explanations
     * @param executionConfig the execution config used in LIME explainer
     */
    void maybeOptimize(List<Prediction> recordedPredictions, PredictionProvider model,
            LimeExplainer limeExplainer, LimeConfig executionConfig);

    /**
     * Obtain the best config for a given LIME explainer.
     *
     * @param explainer a LIME explainer
     * @return the best config available
     */
    LimeConfig bestConfigFor(LimeExplainer explainer);
}
