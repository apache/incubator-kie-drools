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
package org.kie.pmml.models.tree.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class IrisDataTreeTest extends AbstractPMMLTest {

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.000001);
    private static final String FILE_NAME_NO_SUFFIX = "irisTree";
    private static final String MODEL_NAME = "IrisTreeModel";
    private static final String TARGET_FIELD = "Species";
    private static final String PROBABILITY_SETOSA = "Probability_setosa";
    private static final String PROBABILITY_VERSICOLOR = "Probability_versicolor";
    private static final String PROBABILITY_VIRGINICA = "Probability_virginica";
    private static PMMLRuntime pmmlRuntime;

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String expectedResult;
    private double probabilitySetosa;
    private double probabilityVersicolor;
    private double probabilityVirginica;

    public void initIrisDataTreeTest(double sepalLength, double sepalWidth, double petalLength,
                            double petalWidth,
                            String expectedResult,
                            double probabilitySetosa,
                            double probabilityVersicolor,
                            double probabilityVirginica) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.expectedResult = expectedResult;
        this.probabilitySetosa = probabilitySetosa;
        this.probabilityVersicolor = probabilityVersicolor;
        this.probabilityVirginica = probabilityVirginica;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica", 0.0, 0.021739130434782608, 0.9782608695652174},
                {5.8, 2.6, 4.0, 1.2, "versicolor", 0.0, 0.9074074074074074, 0.09259259259259259},
                {5.7, 3.0, 4.2, 1.2, "versicolor", 0.0, 0.9074074074074074, 0.09259259259259259},
                {5.0, 3.3, 1.4, 0.2, "setosa", 1.0, 0.0, 0.0},
                {5.4, 3.9, 1.3, 0.4, "setosa", 1.0, 0.0, 0.0},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testIrisTree(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String expectedResult, double probabilitySetosa, double probabilityVersicolor, double probabilityVirginica) {
        initIrisDataTreeTest(sepalLength, sepalWidth, petalLength, petalWidth, expectedResult, probabilitySetosa, probabilityVersicolor, probabilityVirginica);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Sepal.Length", sepalLength);
        inputData.put("Sepal.Width", sepalWidth);
        inputData.put("Petal.Length", petalLength);
        inputData.put("Petal.Width", petalWidth);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_SETOSA)).isNotNull();

        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_SETOSA)).isEqualTo(probabilitySetosa);
        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_VERSICOLOR)).isNotNull();
        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VERSICOLOR)).isCloseTo(probabilityVersicolor, TOLERANCE_PERCENTAGE);
        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_VIRGINICA)).isNotNull();
        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VIRGINICA)).isCloseTo(probabilityVirginica, TOLERANCE_PERCENTAGE);
    }
}
