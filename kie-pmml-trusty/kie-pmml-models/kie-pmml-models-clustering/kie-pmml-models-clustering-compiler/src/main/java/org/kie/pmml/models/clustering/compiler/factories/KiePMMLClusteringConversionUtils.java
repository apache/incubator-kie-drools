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
package org.kie.pmml.models.clustering.compiler.factories;

import java.util.HashMap;
import java.util.Map;

import org.dmg.pmml.BinarySimilarity;
import org.dmg.pmml.Chebychev;
import org.dmg.pmml.CityBlock;
import org.dmg.pmml.CompareFunction;
import org.dmg.pmml.ComparisonMeasure;
import org.dmg.pmml.Euclidean;
import org.dmg.pmml.Jaccard;
import org.dmg.pmml.Measure;
import org.dmg.pmml.Minkowski;
import org.dmg.pmml.SimpleMatching;
import org.dmg.pmml.SquaredEuclidean;
import org.dmg.pmml.Tanimoto;
import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLCompareFunction;
import org.kie.pmml.models.clustering.model.KiePMMLComparisonMeasure;

import static org.kie.pmml.api.utils.EnumUtils.enumByName;

public class KiePMMLClusteringConversionUtils {

    public static final Map<Class<? extends Measure>, KiePMMLAggregateFunction> AGGREGATE_FN_MAP = new HashMap<>(9);

    static {
        AGGREGATE_FN_MAP.put(Euclidean.class, KiePMMLAggregateFunction.EUCLIDEAN);
        AGGREGATE_FN_MAP.put(SquaredEuclidean.class, KiePMMLAggregateFunction.SQUARED_EUCLIDEAN);
        AGGREGATE_FN_MAP.put(Chebychev.class, KiePMMLAggregateFunction.CHEBYCHEV);
        AGGREGATE_FN_MAP.put(CityBlock.class, KiePMMLAggregateFunction.CITY_BLOCK);
        AGGREGATE_FN_MAP.put(Minkowski.class, KiePMMLAggregateFunction.MINKOWSKI);
        AGGREGATE_FN_MAP.put(SimpleMatching.class, KiePMMLAggregateFunction.SIMPLE_MATCHING);
        AGGREGATE_FN_MAP.put(Jaccard.class, KiePMMLAggregateFunction.JACCARD);
        AGGREGATE_FN_MAP.put(Tanimoto.class, KiePMMLAggregateFunction.TANIMOTO);
        AGGREGATE_FN_MAP.put(BinarySimilarity.class, KiePMMLAggregateFunction.BINARY_SIMILARITY);
    }

    public static KiePMMLAggregateFunction aggregateFunctionFrom(Measure input) {
        for (Map.Entry<Class<? extends Measure>, KiePMMLAggregateFunction> entry : AGGREGATE_FN_MAP.entrySet()) {
            if (entry.getKey().isInstance(input)) {
                return entry.getValue();
            }
        }
        throw new IllegalStateException("Invalid aggregate function of class " + input.getClass());
    }

    public static KiePMMLCompareFunction compareFunctionFrom(CompareFunction input) {
        return enumByName(KiePMMLCompareFunction.class, input.value());
    }

    public static KiePMMLComparisonMeasure.Kind comparisonMeasureKindFrom(ComparisonMeasure.Kind input) {
        return enumByName(KiePMMLComparisonMeasure.Kind.class, input.value());
    }

    public static KiePMMLClusteringModel.ModelClass modelClassFrom(ClusteringModel.ModelClass input) {
        return enumByName(KiePMMLClusteringModel.ModelClass.class, input.value());
    }

    private KiePMMLClusteringConversionUtils() {
    }

}
