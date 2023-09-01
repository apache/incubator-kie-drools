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
import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

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

    private static final List<KiePMMLClusteringField> TEST_FIELDS = new ArrayList<>(2);
    private static final KiePMMLCompareFunction TEST_COMPARE_FN = KiePMMLCompareFunction.ABS_DIFF;
    private static final Double[] TEST_INPUTS = new Double[2];
    private static final double[] TEST_SEEDS = new double[2];
    private static final double TEST_ADJUST = 1.0;

    static {
        TEST_FIELDS.add(new KiePMMLClusteringField("test1", 1.0, true, null, null));
        TEST_FIELDS.add(new KiePMMLClusteringField("test2", 1.0, true, null, null));
        TEST_INPUTS[0] = 5.0;
        TEST_INPUTS[1] = 3.0;
        TEST_SEEDS[0] = 1.0;
        TEST_SEEDS[1] = 6.0;
    }

    @Test
    void testNames() {
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
    void testApply() {
        assertThat(EUCLIDEAN.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isEqualTo(euclidean(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST), DOUBLE_OFFSET);
        assertThat(SQUARED_EUCLIDEAN.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isEqualTo(squaredEuclidean(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST), DOUBLE_OFFSET);
        assertThatThrownBy(() -> CHEBYCHEV.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> CITY_BLOCK.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> MINKOWSKI.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> SIMPLE_MATCHING.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> JACCARD.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> TANIMOTO.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> BINARY_SIMILARITY.apply(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testEuclidean() {
        assertThat(euclidean(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST)).isEqualTo(5.0, DOUBLE_OFFSET);
    }

    @Test
    void testSquaredEuclidean() {
        assertThat(squaredEuclidean(TEST_FIELDS, TEST_COMPARE_FN, TEST_INPUTS, TEST_SEEDS, TEST_ADJUST)).isEqualTo(25.0, DOUBLE_OFFSET);
    }

}
