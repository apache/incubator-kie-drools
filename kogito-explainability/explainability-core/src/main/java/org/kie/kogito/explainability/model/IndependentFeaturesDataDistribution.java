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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Data distribution based on list of {@code FeatureDistributions}.
 */
public class IndependentFeaturesDataDistribution implements DataDistribution {

    private final List<FeatureDistribution> featureDistributions;

    public IndependentFeaturesDataDistribution(List<FeatureDistribution> featureDistributions) {
        this.featureDistributions = Collections.unmodifiableList(featureDistributions);
    }

    @Override
    public PredictionInput sample() {
        List<Feature> features = new ArrayList<>(featureDistributions.size());
        for (FeatureDistribution featureDistribution : featureDistributions) {
            Feature feature = featureDistribution.getFeature();
            features.add(FeatureFactory.copyOf(feature, featureDistribution.sample()));
        }
        return new PredictionInput(features);
    }

    @Override
    public List<PredictionInput> sample(int sampleSize) {
        List<PredictionInput> inputs = new ArrayList<>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            inputs.add(sample());
        }
        return inputs;
    }

    @Override
    public List<PredictionInput> getAllSamples() {
        List<Collection<Feature>> featureEnumerations = new ArrayList<>(featureDistributions.size());
        for (FeatureDistribution featureDistribution : featureDistributions) {
            List<Value<?>> allValues = featureDistribution.getAllSamples();
            List<Feature> currentFeatures = new ArrayList<>(allValues.size());
            Feature feature = featureDistribution.getFeature();
            for (Value<?> v : allValues) {
                Feature f = FeatureFactory.copyOf(feature, v);
                currentFeatures.add(f);
            }
            featureEnumerations.add(currentFeatures);
        }
        Collection<List<Feature>> combinedFeaturesList = cartesianProduct(featureEnumerations);
        List<PredictionInput> inputs = new ArrayList<>(combinedFeaturesList.size());
        for (List<Feature> features : combinedFeaturesList) {
            inputs.add(new PredictionInput(features));
        }
        return inputs;
    }

    static <T> Collection<List<T>> cartesianProduct(List<Collection<T>> valueEnumerations) {
        Collection<List<T>> combinedValues = new ArrayList<>();
        if (!valueEnumerations.isEmpty()) {
            getElementsAtDepth(valueEnumerations, combinedValues, 0, new ArrayList<>());
        }
        return combinedValues;
    }

    private static <T> void getElementsAtDepth(List<Collection<T>> valueEnumerations, Collection<List<T>> combinedValues, int depth,
            List<T> currentItem) {
        if (depth == valueEnumerations.size()) {
            combinedValues.add(currentItem);
        } else {
            Collection<T> currentCollection = valueEnumerations.get(depth);
            for (T element : currentCollection) {
                List<T> copy = new ArrayList<>(currentItem);
                copy.add(element);
                getElementsAtDepth(valueEnumerations, combinedValues, depth + 1, copy);
            }
        }
    }

    @Override
    public List<FeatureDistribution> asFeatureDistributions() {
        return featureDistributions;
    }
}
