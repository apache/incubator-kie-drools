package org.kie.pmml.models.clustering.model;

import java.util.Optional;

public class KiePMMLClusteringField {

    private final String field;
    private final Double fieldWeight;
    private final Boolean isCenterField;
    private final Optional<KiePMMLCompareFunction> compareFunction;
    private final Optional<Double> similarityScale;

    public KiePMMLClusteringField(String field, Double fieldWeight, Boolean isCenterField, KiePMMLCompareFunction compareFunction, Double similarityScale) {
        this.field = field;
        this.fieldWeight = fieldWeight;
        this.isCenterField = isCenterField;
        this.compareFunction = Optional.ofNullable(compareFunction);
        this.similarityScale = Optional.ofNullable(similarityScale);
    }

    public String getField() {
        return field;
    }

    public Double getFieldWeight() {
        return fieldWeight;
    }

    public Boolean getCenterField() {
        return isCenterField;
    }

    public Optional<KiePMMLCompareFunction> getCompareFunction() {
        return compareFunction;
    }

    public Optional<Double> getSimilarityScale() {
        return similarityScale;
    }
}
