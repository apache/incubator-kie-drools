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
package org.kie.kogito.explainability.local.lime;

import java.util.List;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;

public class LimeInputs {

    private final boolean classification;
    private final List<Feature> features;
    private final Output targetOutput;
    private final List<PredictionInput> perturbedInputs;
    private final List<Output> perturbedOutputs;

    public LimeInputs(boolean classification, List<Feature> features, Output targetOutput,
                      List<PredictionInput> perturbedInputs, List<Output> perturbedOutputs) {
        this.classification = classification;
        this.features = features;
        this.targetOutput = targetOutput;
        this.perturbedInputs = perturbedInputs;
        this.perturbedOutputs = perturbedOutputs;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public List<PredictionInput> getPerturbedInputs() {
        return perturbedInputs;
    }

    public List<Output> getPerturbedOutputs() {
        return perturbedOutputs;
    }

    public Output getTargetOutput() {
        return targetOutput;
    }

    public boolean isClassification() {
        return classification;
    }
}
