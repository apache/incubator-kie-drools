package org.kie.pmml.models.clustering.model.aggregate;

import org.kie.pmml.models.clustering.model.compare.CompareFunction;

public interface AggregateFunction {
    double aggregate(CompareFunction[] compare, double[] inputs, double[] seeds, double[] weights, double adjust);
}
