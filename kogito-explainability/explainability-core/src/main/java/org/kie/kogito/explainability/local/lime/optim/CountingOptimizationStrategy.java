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
import java.util.concurrent.atomic.AtomicLong;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;

/**
 * Simple count based {@link LimeConfigOptimizationStrategy}.
 */
public class CountingOptimizationStrategy implements LimeConfigOptimizationStrategy {

    private final int epochLength;
    private final AtomicLong explanationCount = new AtomicLong();
    private final LimeOptimizationService optimizationService;

    public CountingOptimizationStrategy(int epochLength, LimeOptimizationService optimizationService) {
        this.epochLength = epochLength;
        this.optimizationService = optimizationService;
    }

    @Override
    public void maybeOptimize(
            List<Prediction> recordedPredictions, PredictionProvider model, LimeExplainer limeExplainer,
            LimeConfig executionConfig) {
        if (this.explanationCount.incrementAndGet() > epochLength) {
            this.explanationCount.set(0);
            optimizationService.submit(new LimeOptimizationRequest(executionConfig, model, recordedPredictions, limeExplainer));
        }
    }

    @Override
    public LimeConfig bestConfigFor(LimeExplainer explainer) {
        return optimizationService.getBestConfigFor(explainer);
    }
}
