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

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.pmml.api.utils.EnumUtils.enumByName;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.ABS_DIFF;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.DELTA;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.EQUAL;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.GAUSS_SIM;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.TABLE;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.absDiff;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.delta;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.equal;
import static org.kie.pmml.models.clustering.model.KiePMMLCompareFunction.gaussSim;

public class KiePMMLCompareFunctionTest {

    private static final Offset<Double> DOUBLE_OFFSET = Offset.offset(0.000000001);

    private static final double TEST_X = 2.0;
    private static final double TEST_Y = 1.0;
    private static final double TEST_SIMILARITY_SCALE = 1.0;
    private static final KiePMMLClusteringField TEST_FIELD =
            new KiePMMLClusteringField("test", 1.0, true, ABS_DIFF, TEST_SIMILARITY_SCALE);

    @Test
    void testNames() {
        assertThat(enumByName(KiePMMLCompareFunction.class, "absDiff")).isEqualTo(ABS_DIFF);
        assertThat(enumByName(KiePMMLCompareFunction.class, "gaussSim")).isEqualTo(GAUSS_SIM);
        assertThat(enumByName(KiePMMLCompareFunction.class, "delta")).isEqualTo(DELTA);
        assertThat(enumByName(KiePMMLCompareFunction.class, "equal")).isEqualTo(EQUAL);
        assertThat(enumByName(KiePMMLCompareFunction.class, "table")).isEqualTo(TABLE);
    }

    @Test
    void testApply() {
        assertThat(ABS_DIFF.apply(TEST_FIELD, TEST_X, TEST_Y)).isEqualTo(absDiff(TEST_X, TEST_Y), DOUBLE_OFFSET);
        assertThat(GAUSS_SIM.apply(TEST_FIELD, TEST_X, TEST_Y)).isEqualTo(gaussSim(TEST_X, TEST_Y, TEST_SIMILARITY_SCALE), DOUBLE_OFFSET);
        assertThat(DELTA.apply(TEST_FIELD, TEST_X, TEST_Y)).isEqualTo(delta(TEST_X, TEST_Y), DOUBLE_OFFSET);
        assertThat(EQUAL.apply(TEST_FIELD, TEST_X, TEST_Y)).isEqualTo(equal(TEST_X, TEST_Y), DOUBLE_OFFSET);
        assertThatThrownBy(() -> TABLE.apply(TEST_FIELD, TEST_X, TEST_Y)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testAbsDiff() {
        assertThat(absDiff(10.0, 25.0)).isEqualTo(15.0, DOUBLE_OFFSET);
        assertThat(absDiff(25.0, 10.0)).isEqualTo(15.0, DOUBLE_OFFSET);
    }

    @Test
    void testGaussSim() {
        assertThat(gaussSim(4.0, 2.0, 1.0)).isEqualTo(1.0 / 16.0, DOUBLE_OFFSET);
        assertThat(gaussSim(3.0, 2.0, Math.sqrt(2.0))).isEqualTo(1.0 / Math.sqrt(2.0), DOUBLE_OFFSET);
    }

    @Test
    void testDelta() {
        assertThat(delta(10.0, 25.0)).isEqualTo(1.0, DOUBLE_OFFSET);
        assertThat(delta(25.0, 10.0)).isEqualTo(1.0, DOUBLE_OFFSET);
        assertThat(delta(10.0, 10.0)).isEqualTo(0.0, DOUBLE_OFFSET);
    }

    @Test
    void testEqual() {
        assertThat(equal(10.0, 25.0)).isEqualTo(0.0, DOUBLE_OFFSET);
        assertThat(equal(25.0, 10.0)).isEqualTo(0.0, DOUBLE_OFFSET);
        assertThat(equal(10.0, 10.0)).isEqualTo(1.0, DOUBLE_OFFSET);
    }

}
