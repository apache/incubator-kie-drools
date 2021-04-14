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

package org.kie.pmml.models.clustering.compiler.factories;

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

public class KiePMMLClusteringConversionUtils {

    public static KiePMMLAggregateFunction aggregateFunctionFrom(Measure input) {
        if (input instanceof Euclidean) {
            return KiePMMLAggregateFunction.EUCLIDEAN;
        }
        if (input instanceof SquaredEuclidean) {
            return KiePMMLAggregateFunction.SQUARED_EUCLIDEAN;
        }
        if (input instanceof Chebychev) {
            return KiePMMLAggregateFunction.CHEBYCHEV;
        }
        if (input instanceof CityBlock) {
            return KiePMMLAggregateFunction.CITY_BLOCK;
        }
        if (input instanceof Minkowski) {
            return KiePMMLAggregateFunction.MINKOWSKI;
        }
        if (input instanceof SimpleMatching) {
            return KiePMMLAggregateFunction.SIMPLE_MATCHING;
        }
        if (input instanceof Jaccard) {
            return KiePMMLAggregateFunction.JACCARD;
        }
        if (input instanceof Tanimoto) {
            return KiePMMLAggregateFunction.TANIMOTO;
        }
        if (input instanceof BinarySimilarity) {
            return KiePMMLAggregateFunction.BINARY_SIMILARITY;
        }
        throw new IllegalStateException("Invalid aggregate function of class " + input.getClass());
    }

    public static KiePMMLCompareFunction compareFunctionFrom(CompareFunction input) {
        switch (input) {
            case ABS_DIFF:
                return KiePMMLCompareFunction.ABS_DIFF;
            case GAUSS_SIM:
                return KiePMMLCompareFunction.GAUSS_SIM;
            case DELTA:
                return KiePMMLCompareFunction.DELTA;
            case EQUAL:
                return KiePMMLCompareFunction.EQUAL;
            case TABLE:
                return KiePMMLCompareFunction.TABLE;
        }
        throw new IllegalStateException("Invalid compare function " + input);
    }

    public static KiePMMLComparisonMeasure.Kind comparisonMeasureKindFrom(ComparisonMeasure.Kind input) {
        switch (input) {
            case DISTANCE:
                return KiePMMLComparisonMeasure.Kind.DISTANCE;
            case SIMILARITY:
                return KiePMMLComparisonMeasure.Kind.SIMILARITY;
        }
        throw new IllegalStateException("Invalid comparison measure kind " + input);
    }

    public static KiePMMLClusteringModel.ModelClass modelClassFrom(ClusteringModel.ModelClass input) {
        switch (input) {
            case CENTER_BASED:
                return KiePMMLClusteringModel.ModelClass.CENTER_BASED;
            case DISTRIBUTION_BASED:
                return KiePMMLClusteringModel.ModelClass.DISTRIBUTION_BASED;
        }
        throw new IllegalStateException("Invalid model class " + input);
    }

    private KiePMMLClusteringConversionUtils() {
    }

}
