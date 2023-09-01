package org.kie.pmml.models.clustering.model;

import java.util.List;

import org.kie.pmml.api.enums.Named;

public enum KiePMMLAggregateFunction implements Named {
    EUCLIDEAN("euclidean"),
    SQUARED_EUCLIDEAN("squaredEuclidean"),
    CHEBYCHEV("chebychev"),
    CITY_BLOCK("cityBlock"),
    MINKOWSKI("minkowski"),
    SIMPLE_MATCHING("simpleMatching"),
    JACCARD("jaccard"),
    TANIMOTO("tanimoto"),
    BINARY_SIMILARITY("binarySimilarity");

    private final String name;

    KiePMMLAggregateFunction(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public double apply(List<KiePMMLClusteringField> fields, KiePMMLCompareFunction defaultCompare, Double[] inputs, double[] seeds, double adjust) {
        switch (this) {
            case EUCLIDEAN:
                return euclidean(fields, defaultCompare, inputs, seeds, adjust);
            case SQUARED_EUCLIDEAN:
                return squaredEuclidean(fields, defaultCompare, inputs, seeds, adjust);
            case CHEBYCHEV:
            case CITY_BLOCK:
            case MINKOWSKI:
            case SIMPLE_MATCHING:
            case JACCARD:
            case TANIMOTO:
            case BINARY_SIMILARITY:
                throw new UnsupportedOperationException(this + " aggregate function not implemented");
        }
        throw new IllegalStateException("Unknown aggregate function: " + this);
    }

    static double euclidean(List<KiePMMLClusteringField> fields, KiePMMLCompareFunction defaultCompare, Double[] inputs, double[] seeds, double adjust) {
        return Math.sqrt(squaredEuclidean(fields, defaultCompare, inputs, seeds, adjust));
    }

    static double squaredEuclidean(List<KiePMMLClusteringField> fields, KiePMMLCompareFunction defaultCompare, Double[] inputs, double[] seeds, double adjust) {
        double sum = 0.0;
        for (int i = 0; i < fields.size(); i++) {
            if (inputs[i] != null) {
                KiePMMLClusteringField field = fields.get(i);
                double weight = field.getFieldWeight();
                KiePMMLCompareFunction compare = field.getCompareFunction().orElse(defaultCompare);
                sum += weight * Math.pow(compare.apply(field, inputs[i], seeds[i]), 2.0);
            }
        }
        return sum * adjust;
    }
}
