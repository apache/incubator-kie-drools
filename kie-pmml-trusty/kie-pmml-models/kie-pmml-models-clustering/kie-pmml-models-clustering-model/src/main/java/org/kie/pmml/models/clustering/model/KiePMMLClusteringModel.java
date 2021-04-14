/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package  org.kie.pmml.models.clustering.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.clustering.model.aggregate.KiePMMLAggregateFunction;
import org.kie.pmml.models.clustering.model.aggregate.KiePMMLAggregateFunctionImpl;
import org.kie.pmml.models.clustering.model.compare.KiePMMLCompareFunction;
import org.kie.pmml.models.clustering.model.compare.KiePMMLCompareFunctionImpl;

public abstract class KiePMMLClusteringModel extends KiePMMLModel {

    public enum ModelClass {
        CENTER_BASED,
        DISTRIBUTION_BASED
    }

    protected ModelClass modelClass;
    protected List<KiePMMLCluster> clusters = new ArrayList<>();
    protected List<KiePMMLClusteringField> clusteringFields = new ArrayList<>();
    protected KiePMMLComparisonMeasure comparisonMeasure;
    protected KiePMMLMissingValueWeights missingValueWeights;

    protected KiePMMLClusteringModel(String modelName) {
        super(modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(final Object knowledgeBase, final Map<String, Object> requestData) {
        List<String> fieldNames = clusteringFields.stream()
                .map(KiePMMLClusteringField::getField)
                .collect(Collectors.toList());

        double adjustmentFactor = computeAdjustmentFactor(fieldNames, requestData);

        KiePMMLCompareFunction.Function[] compFn = clusteringFields.stream()
                .map(cf -> toCompareFunctionFunction(cf.getCompareFunction().orElseGet(comparisonMeasure::getCompareFunction), cf))
                .toArray(KiePMMLCompareFunction.Function[]::new);

        double[] inputs = clusteringFields.stream()
                .map(KiePMMLClusteringField::getField)
                .filter(requestData::containsKey)
                .map(requestData::get)
                .map(Double.class::cast)
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[] weights = IntStream.range(0, inputs.length)
                .mapToDouble(x -> 1.0)
                .toArray();

        final KiePMMLAggregateFunction.Function aggregateFunction = toAggregateFunctionFunction(comparisonMeasure.getAggregateFunction());

        double[] aggregates = clusters.stream()
                .mapToDouble(c -> aggregateFunction.aggregate(compFn, inputs, c.getValuesArray(), weights, adjustmentFactor))
                .toArray();

        int minIndex = 0;
        double min = aggregates[minIndex];

        for (int i = 1; i < aggregates.length; i++) {
            if (aggregates[i] < min) {
                minIndex = i;
                min = aggregates[i];
            }
        }

        return minIndex + 1;
    }

    private double computeAdjustmentFactor(List<String> fieldNames, Map<String, Object> requestData) {
        double numerator = 1.0;
        double denumerator = 1.0;

        for (int i = 0; i < fieldNames.size(); i++) {
            double weight = missingValueWeightFor(i);
            double nonMissingFactor = requestData.containsKey(fieldNames.get(i)) ? 1.0 : 0.0;

            numerator *= weight;
            denumerator *= weight * nonMissingFactor;
        }

        return numerator / denumerator;
    }

    private double missingValueWeightFor(int fieldNumber) {
        return Optional.ofNullable(missingValueWeights)
                .map(KiePMMLMissingValueWeights::getValues)
                .filter(v -> v.size() >= fieldNumber)
                .map(v -> v.get(fieldNumber))
                .orElse(1.0);
    }

    private KiePMMLAggregateFunction.Function toAggregateFunctionFunction(KiePMMLAggregateFunction compareFunction) {
        switch (compareFunction) {
            case EUCLIDEAN:
                return KiePMMLAggregateFunctionImpl::euclidean;
            case SQUARED_EUCLIDEAN:
                return KiePMMLAggregateFunctionImpl::squaredEuclidean;
        }
        throw new IllegalStateException("Unknown or unsupported compare function: " + compareFunction);
    }

    private KiePMMLCompareFunction.Function toCompareFunctionFunction(KiePMMLCompareFunction compareFunction, KiePMMLClusteringField clusteringField) {
        switch (compareFunction) {
            case ABS_DIFF:
                return KiePMMLCompareFunctionImpl.absDiff();
            case GAUSS_SIM:
                double similarityScale = clusteringField.getSimilarityScale()
                        .orElseThrow(() -> new IllegalStateException("\"gaussSim\" compare function used in field with no similarity scale: \"" + clusteringField.getField() + "\""));
                return KiePMMLCompareFunctionImpl.gaussSim(similarityScale);
            case DELTA:
                return KiePMMLCompareFunctionImpl.delta();
            case EQUAL:
                return KiePMMLCompareFunctionImpl.equal();
            case TABLE:
                return KiePMMLCompareFunctionImpl.table();
        }
        throw new IllegalStateException("Unknown compare function: " + compareFunction);
    }
}
