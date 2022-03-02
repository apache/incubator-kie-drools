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
package org.kie.kogito.explainability.model;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Dataset {

    private final List<Prediction> data;

    public Dataset(List<Prediction> data) {
        this.data = data;
    }

    public List<Prediction> getData() {
        return data;
    }

    public List<PredictionInput> getInputs() {
        return data.stream().map(Prediction::getInput).collect(Collectors.toList());
    }

    public List<PredictionOutput> getOutputs() {
        return data.stream().map(Prediction::getOutput).collect(Collectors.toList());
    }

    /**
     * Filter dataset per feature.
     *
     * Using a feature-wise predicate, filter the original dataset to contain the same number of
     * items ({@link Prediction}) but with a different set of features. Examples of usage are
     * filtering the features by name, value or type.
     * 
     * @param featureSelector A {@link Predicate<Feature>} with the filter conditions
     * @return A new {@link Dataset}
     */
    public Dataset filterByFeature(Predicate<Feature> featureSelector) {

        final List<PredictionInput> inputs = data.stream().map(prediction -> prediction.getInput().getFeatures())
                .map(features -> new PredictionInput(features.stream()
                        .filter(featureSelector).collect(Collectors.toList())))
                .collect(Collectors.toList());

        return new Dataset(IntStream
                .range(0, data.size())
                .mapToObj(i -> new SimplePrediction(inputs.get(i),
                        data.get(i).getOutput()))
                .collect(Collectors.toList()));
    }

    public Dataset filterByInput(Predicate<PredictionInput> inputSelector) {
        return new Dataset(data.stream().filter(p -> inputSelector.test(p.getInput())).collect(Collectors.toList()));
    }

    public Dataset filterByOutput(Predicate<PredictionOutput> outputSelector) {
        return new Dataset(data.stream().filter(p -> outputSelector.test(p.getOutput())).collect(Collectors.toList()));
    }

}
