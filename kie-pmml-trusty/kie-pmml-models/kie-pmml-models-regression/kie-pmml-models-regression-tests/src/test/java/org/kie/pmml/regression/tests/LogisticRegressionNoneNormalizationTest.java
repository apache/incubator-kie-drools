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
package org.kie.pmml.regression.tests;

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

public class LogisticRegressionNoneNormalizationTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "LogisticRegressionNoneNormalization";
    private static final String MODEL_NAME = "LogisticRegressionNoneNormalization";
    private static final String TARGET_FIELD = "Species";
    private static final String PROBABILITY_SETOSA_FIELD = "Probability_setosa";
    private static final String PROBABILITY_VERSICOLOR_FIELD = "Probability_versicolor";
    private static final String PROBABILITY_VIRGINICA_FIELD = "Probability_virginica";

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.001);
    private static PMMLRuntime pmmlRuntime;

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String expectedResult;
    private double expectedSetosaProbability;
    private double expectedVersicolorProbability;
    private double expectedVirginicaProbability;

    public void initLogisticRegressionNoneNormalizationTest(double sepalLength, double sepalWidth, double petalLength,
                                                   double petalWidth, String expectedResult, double expectedSetosaProbability,
                                                   double expectedVersicolorProbability,
                                                   double expectedVirginicaProbability) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.expectedResult = expectedResult;
        this.expectedSetosaProbability = expectedSetosaProbability;
        this.expectedVersicolorProbability = expectedVersicolorProbability;
        this.expectedVirginicaProbability = expectedVirginicaProbability;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica", 0.04871813160275851, 0.04509592640753013, 0.9061859419897114},
                {5.8, 2.6, 4.0, 1.2, "versicolor", 0.16500427922560845, 0.5910742380929204, 0.24392148268147118},
                {5.4, 3.9, 1.3, 0.4, "setosa", 1.1068470023312305, -0.18052700039608738, 0.07367999806485692},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testLogisticRegressionWithNormalization(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String expectedResult, double expectedSetosaProbability, double expectedVersicolorProbability, double expectedVirginicaProbability) throws Exception {
        initLogisticRegressionNoneNormalizationTest(sepalLength, sepalWidth, petalLength, petalWidth, expectedResult, expectedSetosaProbability, expectedVersicolorProbability, expectedVirginicaProbability);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Sepal.Length", sepalLength);
        inputData.put("Sepal.Width", sepalWidth);
        inputData.put("Petal.Length", petalLength);
        inputData.put("Petal.Width", petalWidth);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);

        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_SETOSA_FIELD))
                .isCloseTo(expectedSetosaProbability, TOLERANCE_PERCENTAGE);
        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VERSICOLOR_FIELD))
                .isCloseTo(expectedVersicolorProbability, TOLERANCE_PERCENTAGE);
        assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VIRGINICA_FIELD))
                .isCloseTo(expectedVirginicaProbability, TOLERANCE_PERCENTAGE);
    }
}
