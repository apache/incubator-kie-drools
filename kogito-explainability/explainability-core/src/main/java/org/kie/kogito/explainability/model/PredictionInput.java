package org.kie.kogito.explainability.model;

import java.util.List;

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
}
