package org.kie.pmml.models.clustering.model.aggregate;

import java.util.stream.IntStream;

import org.kie.pmml.models.clustering.model.compare.CompareFunction;

public class AggregateFunctions {

    public static double euclidean(CompareFunction[] compFn, double[] inputs, double[] seeds, double[] weights, double adjust) {
        return Math.sqrt(squaredEuclidean(compFn, inputs, seeds, weights, adjust));
    }

    public static double squaredEuclidean(CompareFunction[] compFn, double[] inputs, double[] seeds, double[] weights, double adjust) {
        return adjust * IntStream.range(0, inputs.length)
                .mapToDouble(i -> weights[i] * Math.pow(compFn[i].compare(inputs[i], seeds[i]), 2.0))
                .sum();
    }

    private AggregateFunctions() {
        // not allowed for util class
    }

}
