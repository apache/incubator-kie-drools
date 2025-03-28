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
package org.kie.pmml.clustering.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class EuclideanDistanceTest extends AbstractPMMLTest {

    private static final double DOUBLE_VALID_PERCENTAGE = 0.99999;

    private static final String FILE_NAME_NO_SUFFIX = "euclideanDistance";
    private static final String MODEL_NAME = "euclidianDistance";
    private static final String CLUSTER_ID_FIELD = "predictedValue";
    private static final String AFFINITY_FIELD = "predictedAffinity";

    protected static PMMLRuntime pmmlRuntime;

    private double dimension1;
    private double dimension2;
    private String classId;
    private double affinity;

    public void initEuclideanDistanceTest(double dimension1, double dimension2, String classId, double affinity) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.classId = classId;
        this.affinity = affinity;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-1.0, -1.0, "1", 1.4142135623730951},
                {7.0, 8.0, "2", 3.605551275463989},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void test(double dimension1, double dimension2, String classId, double affinity) {
        initEuclideanDistanceTest(dimension1, dimension2, classId, affinity);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Dimension1", dimension1);
        inputData.put("Dimension2", dimension2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        assertThat(pmml4Result.getResultVariables().get(CLUSTER_ID_FIELD)).isEqualTo(classId);
        assertThat(pmml4Result.getResultVariables().get(AFFINITY_FIELD))
                .asInstanceOf(InstanceOfAssertFactories.DOUBLE)
                .isCloseTo(affinity, Percentage.withPercentage(DOUBLE_VALID_PERCENTAGE));
    }
}
