package org.kie.pmml.models.clustering.model.compare;

import java.util.function.BiFunction;

public enum KiePMMLCompareFunction {
    ABS_DIFF,
    GAUSS_SIM,
    DELTA,
    EQUAL,
    TABLE;

    public interface Function extends BiFunction<Double, Double, Double> {
    }
}
