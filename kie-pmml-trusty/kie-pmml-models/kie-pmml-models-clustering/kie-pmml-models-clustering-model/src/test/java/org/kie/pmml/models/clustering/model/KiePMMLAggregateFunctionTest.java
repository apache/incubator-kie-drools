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

package org.kie.pmml.models.clustering.model;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.pmml.api.utils.EnumUtils.enumByName;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.BINARY_SIMILARITY;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.CHEBYCHEV;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.CITY_BLOCK;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.EUCLIDEAN;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.JACCARD;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.MINKOWSKI;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.SIMPLE_MATCHING;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.SQUARED_EUCLIDEAN;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.TANIMOTO;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.euclidean;
import static org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction.squaredEuclidean;

public class KiePMMLAggregateFunctionTest {

    private static final Offset<Double> DOUBLE_OFFSET = Offset.offset(0.000000001);

    @Test
    public void testNames() {
        assertThat(enumByName(KiePMMLAggregateFunction.class, "euclidean")).isEqualTo(EUCLIDEAN);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "squaredEuclidean")).isEqualTo(SQUARED_EUCLIDEAN);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "chebychev")).isEqualTo(CHEBYCHEV);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "cityBlock")).isEqualTo(CITY_BLOCK);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "minkowski")).isEqualTo(MINKOWSKI);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "simpleMatching")).isEqualTo(SIMPLE_MATCHING);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "jaccard")).isEqualTo(JACCARD);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "tanimoto")).isEqualTo(TANIMOTO);
        assertThat(enumByName(KiePMMLAggregateFunction.class, "binarySimilarity")).isEqualTo(BINARY_SIMILARITY);
    }

    @Test
    public void testApply() {
        List<KiePMMLClusteringField> testFields = new ArrayList<>(2);
        testFields.add(new KiePMMLClusteringField("test1", 1.0, true, null, null));
        testFields.add(new KiePMMLClusteringField("test2", 1.0, true, null, null));

        KiePMMLCompareFunction testCompFn = KiePMMLCompareFunction.ABS_DIFF;

        Double[] testInputs = new Double[2];
        testInputs[0] = 5.0;
        testInputs[1] = 3.0;

        double[] testSeeds = new double[2];
        testSeeds[0] = 1.0;
        testSeeds[1] = 6.0;

        double testAdjust = 1.0;

        assertThat(EUCLIDEAN.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isEqualTo(euclidean(testFields, testCompFn, testInputs, testSeeds, testAdjust), DOUBLE_OFFSET);
        assertThat(SQUARED_EUCLIDEAN.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isEqualTo(squaredEuclidean(testFields, testCompFn, testInputs, testSeeds, testAdjust), DOUBLE_OFFSET);
        assertThatThrownBy(() -> CHEBYCHEV.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> CITY_BLOCK.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> MINKOWSKI.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> SIMPLE_MATCHING.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> JACCARD.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> TANIMOTO.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> BINARY_SIMILARITY.apply(testFields, testCompFn, testInputs, testSeeds, testAdjust))
                .isInstanceOf(UnsupportedOperationException.class);
    }

}
