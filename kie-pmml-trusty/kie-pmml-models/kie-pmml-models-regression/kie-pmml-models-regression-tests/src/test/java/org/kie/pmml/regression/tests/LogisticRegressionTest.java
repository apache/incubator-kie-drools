/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.regression.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;

@RunWith(Parameterized.class)
public class LogisticRegressionTest extends AbstractPMMLRegressionTest {

    private static final String MODEL_NAME = "LogisticRegression";
    private static final String TARGET_FIELD = "class";
    private static final String PROBABILITY_AUTHENTIC = "probability(Authentic)";
    private static final String PROBABILITY_COUNTERFEIT = "probability(Counterfeit)";

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.001);
    private static PMMLRuntime pmmlRuntime;

    private double variance;
    private double skewness;
    private double curtosis;
    private double entropy;
    private String expectedResult;
    private double expectedProbAuthentic;
    private double expectedProbCounterfeit;

    public LogisticRegressionTest(double variance, double skewness, double curtosis,
                                  double petalWidth, String expectedResult,
                                  double expectedProbAuthentic,
                                  double expectedProbCounterfeit) {
        this.variance = variance;
        this.skewness = skewness;
        this.curtosis = curtosis;
        this.entropy = petalWidth;
        this.expectedResult = expectedResult;
        this.expectedProbAuthentic = expectedProbAuthentic;
        this.expectedProbCounterfeit = expectedProbCounterfeit;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(MODEL_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {2.3, 6.9, 3.1, 5.1, "Authentic", 0.9999999999999969, 3.1271452352700172E-15},
                {1.2, 5.8, 2.6, 4.0, "Authentic", 0.9999999999977859, 2.214170674606705E-12},
                {1.2, 5.7, 3.0, 4.2, "Authentic", 0.9999999999990105, 9.89558801361823E-13},
                {0.2, 5.0, 3.3, 1.4, "Authentic", 0.9999999980426516, 1.9573484459863236E-9},
                {0.4, 5.4, 3.9, 1.3, "Authentic", 0.9999999997410439, 2.589560996869738E-10}
        });
    }

    @Test
    public void testLogisticRegressionIrisData() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("variance", variance);
        inputData.put("skewness", skewness);
        inputData.put("curtosis", curtosis);
        inputData.put("entropy", entropy);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);

        // TODO {gcardosi} TO BE FIXED WITH DROOLS-5453
//        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_AUTHENTIC))
//                .isCloseTo(expectedProbAuthentic, TOLERANCE_PERCENTAGE);
//        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_COUNTERFEIT))
//                .isCloseTo(expectedProbCounterfeit, TOLERANCE_PERCENTAGE);
    }
}
