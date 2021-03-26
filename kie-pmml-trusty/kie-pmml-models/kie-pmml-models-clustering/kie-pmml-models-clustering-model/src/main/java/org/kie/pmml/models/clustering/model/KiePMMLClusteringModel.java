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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.clustering.model.aggregate.AggregateFunction;
import org.kie.pmml.models.clustering.model.compare.CompareFunction;
import org.kie.pmml.models.clustering.model.compare.CompareFunctions;

public class KiePMMLClusteringModel extends KiePMMLModel {

    private static final CompareFunction DEFAULT_COMPARE_FN = CompareFunctions.absDiff();

    private AggregateFunction aggregateFn;
    private CompareFunction compareFn;
    private List<Cluster> clusters = new ArrayList<>();
    private List<ClusteringField> clusteringFields = new ArrayList<>();

    public KiePMMLClusteringModel(String modelName, AggregateFunction aggregateFn) {
        this(modelName, aggregateFn, null);
    }

    public KiePMMLClusteringModel(String modelName, AggregateFunction aggregateFn, CompareFunction compareFn) {
        super(modelName, Collections.emptyList());
        this.aggregateFn = aggregateFn;
        this.compareFn = Optional.ofNullable(compareFn).orElse(DEFAULT_COMPARE_FN);
    }

    @Override
    public Object evaluate(final Object knowledgeBase, final Map<String, Object> requestData) {
        // TODO
//        throw new UnsupportedOperationException();
        System.out.println("--> knowledgeBase: " + knowledgeBase);
        System.out.println("--> requestData..: " + requestData);

        CompareFunction[] compFn = clusteringFields.stream()
                .map(cf -> cf.getCompareFunction().orElse(compareFn))
                .toArray(CompareFunction[]::new);

        double[] inputs = clusteringFields.stream()
                .map(ClusteringField::getField)
                .filter(requestData::containsKey)
                .map(requestData::get)
                .map(Double.class::cast)
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[] weights = IntStream.range(0, inputs.length)
                .mapToDouble(x -> 1.0)
                .toArray();

        double[] aggregates = clusters.stream()
                .mapToDouble(c -> aggregateFn.aggregate(compFn, inputs, c.seeds, weights, 1.0))
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

    @Override
    public Map<String, Object> getOutputFieldsMap() {
        // TODO
//        throw new UnsupportedOperationException();
        return Collections.emptyMap();
    }

    protected void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }

    protected void addClusteringField(ClusteringField field) {
        clusteringFields.add(field);
    }

    public static class Cluster {
        private final double[] seeds;

        public Cluster(double[] seeds) {
            this.seeds = seeds;
        }

        public double[] getSeeds() {
            return seeds;
        }

        public static Cluster of(double... seeds) {
            return new Cluster(seeds);
        }
    }

    public static class ClusteringField {
        private final String field;
        private final CompareFunction compareFn;

        public ClusteringField(String field, CompareFunction compareFn) {
            this.field = field;
            this.compareFn = compareFn;
        }

        public String getField() {
            return field;
        }

        public Optional<CompareFunction> getCompareFunction() {
            return Optional.ofNullable(compareFn);
        }

        public static ClusteringField of(String field) {
            return new ClusteringField(field, null);
        }

        public static ClusteringField of(String field, CompareFunction compareFunction) {
            return new ClusteringField(field, compareFunction);
        }
    }

}
