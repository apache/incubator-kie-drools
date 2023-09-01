package org.kie.pmml.models.tree.model;

import java.util.List;

import org.kie.pmml.api.exceptions.KiePMMLValidationException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLScoreDistribution extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -8674575012261916224L;
    private final String value;
    private final int recordCount;
    private final Double confidence; // must be >= 0 and <= 1; using wrapped class because it could be nullable
    private final Double probability; // must be >= 0 and <= 1; using wrapped class because it could be nullable

    public KiePMMLScoreDistribution(
            final String name,
            final List<KiePMMLExtension> extensions,
            final String value,
            final int recordCount,
            final Double confidence,
            final Double probability) {
        super(name, extensions);
        this.value = value;
        this.recordCount = recordCount;
        this.confidence = confidence;
        this.probability = probability;
    }

    public boolean hasProbability() {
        return probability != null;
    }

    public String getValue() {
        return value;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public double getProbability() {
        if (probability == null) {
            throw new KiePMMLValidationException("Missing expected probability");
        }
       return probability;
    }

    public Double getConfidence() {
        return confidence;
    }

    public double getEvaluatedProbability(int totalRecordCount) {
        return (double)recordCount / (double)totalRecordCount;
    }

    public Double getEvaluatedConfidence(double missingValuePenalty) {
        return confidence != null ? confidence * missingValuePenalty : null;
    }
}