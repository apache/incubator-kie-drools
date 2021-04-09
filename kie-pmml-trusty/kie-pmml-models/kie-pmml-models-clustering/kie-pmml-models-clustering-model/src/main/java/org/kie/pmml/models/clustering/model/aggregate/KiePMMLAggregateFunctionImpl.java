package org.kie.pmml.models.clustering.model.aggregate;

import java.util.stream.IntStream;

import org.kie.pmml.models.clustering.model.compare.KiePMMLCompareFunction;

public class KiePMMLAggregateFunctionImpl {

    public static double euclidean(KiePMMLCompareFunction.Function[] compFn, double[] inputs, double[] seeds, double[] weights, double adjust) {
        return Math.sqrt(squaredEuclidean(compFn, inputs, seeds, weights, adjust));
    }

    public static double squaredEuclidean(KiePMMLCompareFunction.Function[] compFn, double[] inputs, double[] seeds, double[] weights, double adjust) {
        return adjust * IntStream.range(0, inputs.length)
                .mapToDouble(i -> weights[i] * Math.pow(compFn[i].apply(inputs[i], seeds[i]), 2.0))
                .sum();
    }

    private KiePMMLAggregateFunctionImpl() {
        // not allowed for util class
    }

}
