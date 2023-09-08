/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.clustering.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.Named;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.IsInterpreted;
import org.kie.pmml.commons.model.KiePMMLModel;

public class KiePMMLClusteringModel extends KiePMMLModel implements IsInterpreted {

    private static final long serialVersionUID = 2845884699009576755L;

    public enum ModelClass implements Named {
        CENTER_BASED("centerBased"),
        DISTRIBUTION_BASED("distributionBased");

        private final String name;

        ModelClass(String name) {
            this.name = name;
        }

        public static ModelClass byName(String name) {
            return Arrays.stream(ModelClass.values())
                    .filter(value -> name.equals(value.name))
                    .findFirst()
                    .orElseThrow(() -> new KieEnumException("Failed to find ModelClass with name: " + name));
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private ModelClass modelClass;
    private List<KiePMMLCluster> clusters = new ArrayList<>();
    private List<KiePMMLClusteringField> clusteringFields = new ArrayList<>();
    private KiePMMLComparisonMeasure comparisonMeasure;
    private KiePMMLMissingValueWeights missingValueWeights;

    private KiePMMLClusteringModel(String fileName, String modelName) {
        super(fileName, modelName, Collections.emptyList());
    }

    public static Builder builder(String fileName, String name, MINING_FUNCTION miningFunction) {
        return new Builder(fileName, name, miningFunction);
    }

    public ModelClass getModelClass() {
        return modelClass;
    }

    public List<KiePMMLCluster> getClusters() {
        return Collections.unmodifiableList(clusters);
    }

    public List<KiePMMLClusteringField> getClusteringFields() {
        return Collections.unmodifiableList(clusteringFields);
    }

    public KiePMMLComparisonMeasure getComparisonMeasure() {
        return comparisonMeasure;
    }

    public KiePMMLMissingValueWeights getMissingValueWeights() {
        return missingValueWeights;
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        double adjustmentFactor = computeAdjustmentFactor(requestData);

        Double[] inputs = new Double[clusteringFields.size()];
        for (int i = 0; i < clusteringFields.size(); i++) {
            String fieldName = clusteringFields.get(i).getField();
            inputs[i] = requestData.containsKey(fieldName) ? convertToDouble(requestData.get(fieldName)) : null;
        }

        double[] aggregates = new double[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            aggregates[i] = comparisonMeasure.getAggregateFunction()
                    .apply(clusteringFields, comparisonMeasure.getCompareFunction(), inputs,
                           clusters.get(i).getValuesArray(), adjustmentFactor);
        }

        final int selectedIndex = findMinIndex(aggregates);
        final KiePMMLCluster selectedCluster = clusters.get(selectedIndex);
        final int selectedEntityId = selectedIndex + 1;

        selectedCluster.getName().ifPresent(context::setPredictedDisplayValue);
        context.setEntityId(selectedEntityId);
        context.setAffinity(aggregates[selectedIndex]);

        return selectedCluster.getId().orElseGet(() -> Integer.toString(selectedEntityId));
    }

    static Double convertToDouble(Object toConvert) {
        if (!(toConvert instanceof Number )) {
            throw new IllegalArgumentException("Input data must be declared and sent as Number, received " + toConvert);
        }
        return (Double) DATA_TYPE.DOUBLE.getActualValue(toConvert);
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

        if (denumerator != 0) {
            return numerator / denumerator;
        } else {
            throw new KiePMMLException("Division by 0! Denumerator value is 0.");
        }
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

    public static class Builder extends KiePMMLModel.Builder<KiePMMLClusteringModel> {

        private Builder(String fileName, String name, MINING_FUNCTION miningFunction) {
            super("Clustering-", PMML_MODEL.CLUSTERING_MODEL, miningFunction, () -> new KiePMMLClusteringModel(fileName, name));
        }

        public Builder withModelClass(ModelClass modelClass) {
            toBuild.modelClass = modelClass;
            return this;
        }

        public Builder withClusters(List<KiePMMLCluster> clusters) {
            if (clusters != null) {
                toBuild.clusters.addAll(clusters);
            }
            return this;
        }

        public Builder withClusteringFields(List<KiePMMLClusteringField> clusteringFields) {
            if (clusteringFields != null) {
                toBuild.clusteringFields.addAll(clusteringFields);
            }
            return this;
        }

        public Builder withComparisonMeasure(KiePMMLComparisonMeasure comparisonMeasure) {
            toBuild.comparisonMeasure = comparisonMeasure;
            return this;
        }

        public Builder withMissingValueWeights(KiePMMLMissingValueWeights missingValueWeights) {
            toBuild.missingValueWeights = missingValueWeights;
            return this;
        }

    }

}
