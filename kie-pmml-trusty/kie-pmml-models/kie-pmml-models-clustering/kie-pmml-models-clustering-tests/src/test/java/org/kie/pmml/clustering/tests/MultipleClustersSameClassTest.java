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

public class MultipleClustersSameClassTest extends AbstractPMMLTest {

    private static final double DOUBLE_VALID_PERCENTAGE = 0.99999;

    private static final String FILE_NAME_NO_SUFFIX = "multipleClustersSameClass";
    private static final String MODEL_NAME = "multipleClusterSameClassModel";
    private static final String AFFINITY_FIELD = "predictedAffinity";
    private static final String CLUSTER_AFFINITY_FIELD = "predictedClusterAffinity";
    private static final String CLUSTER_ID_FIELD = "predictedValue";
    private static final String CLUSTER_NAME_FIELD = "predictedDisplayValue";

    protected static PMMLRuntime pmmlRuntime;

    private double dimension1;
    private double dimension2;
    private String classId;
    private String className;
    private double affinity;

    public void initMultipleClustersSameClassTest(double dimension1, double dimension2, String classId, String className, double affinity) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.classId = classId;
        this.className = className;
        this.affinity = affinity;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0.0, 0.0, "1", "classA", 0.0},
                {0.5, 0.0, "1", "classA", 0.25},
                {1.0, 1.0, "2", "classB", 0.0},
                {1.0, 3.0, "2", "classB", 4.0},
                {-1, -1, "3", "classA", 0.0},
                {-1.3, -1.4, "3", "classA", 0.25},
                {5.0, 5.0, "4", "classB", 0.0},
                {8.0, 9.0, "4", "classB", 25.0},
                {-3.0, 5.0, "5", "classC", 0.0},
                {-2.0, 5.0, "5", "classC", 1.0}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void test(double dimension1, double dimension2, String classId, String className, double affinity) {
        initMultipleClustersSameClassTest(dimension1, dimension2, classId, className, affinity);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Dimension1", dimension1);
        inputData.put("Dimension2", dimension2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(CLUSTER_ID_FIELD)).isEqualTo(classId);
        assertThat(pmml4Result.getResultVariables().get(CLUSTER_NAME_FIELD)).isEqualTo(className);

        assertDoubleVariable(pmml4Result, AFFINITY_FIELD, affinity);
        assertDoubleVariable(pmml4Result, CLUSTER_AFFINITY_FIELD, affinity);
    }

    private static void assertDoubleVariable(PMML4Result pmml4Result, String variableName, double expectedValue) {
        assertThat(pmml4Result.getResultVariables().get(variableName))
                .asInstanceOf(InstanceOfAssertFactories.DOUBLE)
                .isCloseTo(expectedValue, Percentage.withPercentage(DOUBLE_VALID_PERCENTAGE));
    }
}
