/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package  org.kie.pmml.models.clustering.compiler.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.clustering.ClusteringField;
import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.clustering.model.KiePMMLCluster;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringField;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLComparisonMeasure;
import org.kie.pmml.models.clustering.model.KiePMMLMissingValueWeights;
import org.kie.pmml.models.clustering.model.aggregate.AggregateFunction;
import org.kie.pmml.models.clustering.model.aggregate.AggregateFunctions;
import org.kie.pmml.models.clustering.model.compare.CompareFunction;
import org.kie.pmml.models.clustering.model.compare.CompareFunctions;

public class KiePMMLClusteringModelFactory {

    private KiePMMLClusteringModelFactory(){
        // Avoid instantiation
    }

    public static KiePMMLClusteringModel getKiePMMLClusteringModel(final DataDictionary dataDictionary,
                                                                       final TransformationDictionary transformationDictionary,
                                                                       final ClusteringModel model,
                                                                       final String packageName,
                                                                       final HasClassLoader hasClassLoader) {
        List<KiePMMLCluster> clusters = new ArrayList<>();
        clusters.add(new KiePMMLCluster(Stream.of(6.9125000000000005, 3.0999999999999999, 5.846874999999999, 2.13124999999999966).collect(Collectors.toList()), null, null));
        clusters.add(new KiePMMLCluster(Stream.of(6.2365853658536600, 2.8585365853658535, 4.807317073170731, 1.62195121951219433).collect(Collectors.toList()), null, null));
        clusters.add(new KiePMMLCluster(Stream.of(5.0059999999999990, 3.4180000000000006, 1.464000000000000, 0.24399999999999999).collect(Collectors.toList()), null, null));
        clusters.add(new KiePMMLCluster(Stream.of(5.5296296296296290, 2.6222222222222222, 3.940740740740741, 1.21851851851851886).collect(Collectors.toList()), null, null));

        List<KiePMMLClusteringField> clusteringFields = new ArrayList<>();
        clusteringFields.add(new KiePMMLClusteringField("sepal_length", 1.0, true, CompareFunctions.absDiff(), null));
        clusteringFields.add(new KiePMMLClusteringField("sepal_width", 1.0, true, CompareFunctions.absDiff(), null));
        clusteringFields.add(new KiePMMLClusteringField("petal_length", 1.0, true, CompareFunctions.absDiff(), null));
        clusteringFields.add(new KiePMMLClusteringField("petal_width", 1.0, true, CompareFunctions.absDiff(), null));

        KiePMMLComparisonMeasure comparisonMeasure = new KiePMMLComparisonMeasure(KiePMMLComparisonMeasure.Kind.DISTANCE, AggregateFunctions::squaredEuclidean, CompareFunctions.absDiff());

        return new KiePMMLClusteringModel(
                model.getModelName(),
                KiePMMLClusteringModel.ModelClass.CENTER_BASED,
                clusters,
                clusteringFields,
                comparisonMeasure,
                new KiePMMLMissingValueWeights(Collections.emptyList())
        );
    }

    public static Map<String, String> getKiePMMLClusteringModelSourcesMap(final DataDictionary dataDictionary,
                                                                                 final TransformationDictionary transformationDictionary,
                                                                                 final ClusteringModel model,
                                                                                 final String packageName) {
        return Collections.emptyMap();
    }
}
