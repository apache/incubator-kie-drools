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

import java.util.Objects;

/**
 * The importance associated to a given {@link Feature}.
 * This is usually the output of an explanation algorithm (local or global).
 */
public class FeatureImportance {

    private final Feature feature;
    private final double score;
    private final double confidence;

    public FeatureImportance(Feature feature, double score) {
        this.feature = feature;
        this.score = score;
        this.confidence = 0;
    }

    public FeatureImportance(Feature feature, double score, double confidence) {
        this.feature = feature;
        this.score = score;
        this.confidence = confidence;
    }

    public Feature getFeature() {
        return feature;
    }

    public double getScore() {
        return score;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "FeatureImportance{" +
                "feature=" + feature +
                ", score=" + score +
                ", confidence= +/-" + confidence +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureImportance other = (FeatureImportance) o;
        return this.getFeature().equals(other.getFeature())
                && (Math.abs(this.getScore() - other.getScore()) < 1e-6)
                && (Math.abs(this.getConfidence() - other.getConfidence()) < 1e-6);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature, score, confidence);
    }
}
