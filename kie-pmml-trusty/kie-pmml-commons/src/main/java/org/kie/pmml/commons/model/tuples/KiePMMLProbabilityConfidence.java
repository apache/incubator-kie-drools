package org.kie.pmml.commons.model.tuples;

/**
 * Class to represent a <b>probability/confidence</b> tuple
 */
public class KiePMMLProbabilityConfidence {

    private final double probability;
    private final Double confidence;

    public KiePMMLProbabilityConfidence(double probability, Double confidence) {
        this.probability = probability;
        this.confidence = confidence;
    }

    public double getProbability() {
        return probability;
    }

    public Double getConfidence() {
        return confidence;
    }
}