package org.kie.pmml.models.clustering.model;

import java.util.List;

public class KiePMMLMissingValueWeights {

    private final List<Double> values;

    public KiePMMLMissingValueWeights(List<Double> values) {
        this.values = values;
    }

    public List<Double> getValues() {
        return values;
    }
}
