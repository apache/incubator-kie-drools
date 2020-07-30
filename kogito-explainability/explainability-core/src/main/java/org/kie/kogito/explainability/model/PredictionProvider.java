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
 * A provider of predictions.
 * This can be any model, service or function, like (local / remote) DMN, PMML services or any other ML model.
 */
public interface PredictionProvider {

    /**
     * Perform a batch of predictions, given a batch of inputs.
     * @param inputs the input batch
     * @return a batch of prediction outputs
     */
    List<PredictionOutput> predict(List<PredictionInput> inputs);

}
