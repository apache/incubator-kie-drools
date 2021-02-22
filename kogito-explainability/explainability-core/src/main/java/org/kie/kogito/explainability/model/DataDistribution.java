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
package org.kie.kogito.explainability.model;

import java.util.List;

/**
 * Information about distribution of data (e.g. used for training a model).
 */
public interface DataDistribution {

    /**
     * Sample a {@code PredictionInput} from this distribution.
     * 
     * @return a prediction input
     */
    PredictionInput sample();

    /**
     * Sample a number of {@code PredictionInputs} from this distribution.
     * 
     * @return a list of prediction inputs
     */
    List<PredictionInput> sample(int sampleSize);

    /**
     * Get all the {@code PredictionInputs} from this distribution.
     * 
     * @return a list of prediction inputs
     */
    List<PredictionInput> getAllSamples();

    /**
     * Get separate (independent) distributions for each feature.
     * 
     * @return a list of feature distributions
     */
    List<FeatureDistribution> asFeatureDistributions();
}
