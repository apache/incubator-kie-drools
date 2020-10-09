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
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class LogisticRegressionTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "LogisticRegression.pmml";
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
                                  double entropy, String expectedResult,
                                  double expectedProbAuthentic,
                                  double expectedProbCounterfeit) {
        this.variance = variance;
        this.skewness = skewness;
        this.curtosis = curtosis;
        this.entropy = entropy;
        this.expectedResult = expectedResult;
        this.expectedProbAuthentic = expectedProbAuthentic;
        this.expectedProbCounterfeit = expectedProbCounterfeit;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(MODEL_NAME, FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {2.3, 6.9, 3.1, 5.1, "Authentic", 0.999999999768916, 2.310840601778274E-10},
                {1.2, 5.8, 2.6, 4.0, "Authentic", 0.999999872097169, 1.2790283098140014E-7},
                {1.2, 5.7, 3.0, 4.2, "Authentic", 0.9999999361111592, 6.388884074105009E-8},
                {0.2, 5.0, 3.3, 1.4, "Authentic", 0.9999971178698301, 2.882130169854471E-6},
                {0.4, 5.4, 3.9, 1.3, "Authentic", 0.9999997717811206, 2.2821887933271695E-7}
        });
    }

    @Test
    public void testLogisticRegression() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("variance", variance);
        inputData.put("skewness", skewness);
        inputData.put("curtosis", curtosis);
        inputData.put("entropy", entropy);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_AUTHENTIC))
                .isCloseTo(expectedProbAuthentic, TOLERANCE_PERCENTAGE);
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_COUNTERFEIT))
                .isCloseTo(expectedProbCounterfeit, TOLERANCE_PERCENTAGE);
    }
}
