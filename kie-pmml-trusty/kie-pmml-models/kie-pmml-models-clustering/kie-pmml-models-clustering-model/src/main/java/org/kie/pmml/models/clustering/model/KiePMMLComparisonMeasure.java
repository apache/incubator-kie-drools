package org.kie.pmml.models.clustering.model;

import org.kie.pmml.api.enums.Named;

public class KiePMMLComparisonMeasure {

    public enum Kind implements Named {
        DISTANCE("distance"),
        SIMILARITY("similarity");

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private final Kind kind;
    private final KiePMMLAggregateFunction aggregateFunction;
    private final KiePMMLCompareFunction compareFunction;

    public KiePMMLComparisonMeasure(Kind kind, KiePMMLAggregateFunction aggregateFunction, KiePMMLCompareFunction compareFunction) {
        this.kind = kind;
        this.aggregateFunction = aggregateFunction;
        this.compareFunction = compareFunction;
    }

    public Kind getKind() {
        return kind;
    }

    public KiePMMLAggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    public KiePMMLCompareFunction getCompareFunction() {
        return compareFunction;
    }
}
