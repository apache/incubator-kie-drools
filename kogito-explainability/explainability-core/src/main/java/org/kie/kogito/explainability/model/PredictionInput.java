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
import java.util.Objects;
import java.util.Optional;

/**
 * The inputs to a {@link PredictionProvider}.
 * A prediction input is composed by one or more {@link Feature}s.
 */
public class PredictionInput {

    private final List<Feature> features;

    public PredictionInput(List<Feature> features) {
        this.features = features;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public Optional<Feature> getFeatureByName(String name) {
        return features.stream()
                .filter(feature -> name.equalsIgnoreCase(feature.getName()))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PredictionInput that = (PredictionInput) o;
        return Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(features);
    }

    @Override
    public String toString() {
        return "PredictionInput{" +
                "features=" + features +
                '}';
    }
}
