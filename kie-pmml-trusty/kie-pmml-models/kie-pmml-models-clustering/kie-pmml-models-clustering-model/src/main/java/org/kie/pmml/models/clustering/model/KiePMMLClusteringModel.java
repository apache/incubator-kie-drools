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

import org.kie.pmml.api.enums.Named;
import org.kie.pmml.commons.model.KiePMMLModel;

public abstract class KiePMMLClusteringModel extends KiePMMLModel {

    public enum ModelClass implements Named {
        CENTER_BASED("centerBased"),
        DISTRIBUTION_BASED("distributionBased");

        private final String name;

        ModelClass(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
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
        double adjustmentFactor = computeAdjustmentFactor(requestData);

        Double[] inputs = new Double[clusteringFields.size()];
        for (int i = 0; i < clusteringFields.size(); i++) {
            String fieldName = clusteringFields.get(i).getField();
            inputs[i] = requestData.containsKey(fieldName) ? (Double) requestData.get(fieldName) : null;
        }

        double[] aggregates = new double[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            aggregates[i] = comparisonMeasure.getAggregateFunction()
                    .apply(clusteringFields, comparisonMeasure.getCompareFunction(), inputs, clusters.get(i).getValuesArray(), adjustmentFactor);
        }

        final int selectedIndex = findMinIndex(aggregates);
        final KiePMMLCluster selectedCluster = clusters.get(selectedIndex);
        final int selectedEntityId = selectedIndex + 1;

        selectedCluster.getName().ifPresent(this::setPredictedDisplayValue);
        setEntityId(selectedEntityId);
        setAffinity(aggregates[selectedIndex]);

        return clusters.get(selectedIndex).getId().orElseGet(() -> Integer.toString(selectedEntityId));
    }

    private double computeAdjustmentFactor(Map<String, Object> requestData) {
        double numerator = 1.0;
        double denumerator = 1.0;

        for (int i = 0; i < clusteringFields.size(); i++) {
            double weight = missingValueWeightFor(i);
            double nonMissingFactor = requestData.containsKey(clusteringFields.get(i).getField()) ? 1.0 : 0.0;

            numerator *= weight;
            denumerator *= weight * nonMissingFactor;
        }

        return numerator / denumerator;
    }

    private int findMinIndex(double[] values) {
        int minIndex = 0;
        double min = values[minIndex];
        for (int i = 1; i < values.length; i++) {
            if (values[i] < min) {
                minIndex = i;
                min = values[i];
            }
        }
        return minIndex;
    }

    private double missingValueWeightFor(int fieldNumber) {
        return Optional.ofNullable(missingValueWeights)
                .map(KiePMMLMissingValueWeights::getValues)
                .filter(v -> v.size() >= fieldNumber)
                .map(v -> v.get(fieldNumber))
                .orElse(1.0);
    }

}
