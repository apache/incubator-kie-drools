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
public class OrderApprovalRegressionTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "OrderApproval.pmml";

    private static final String MODEL_NAME = "OrderApprovalRegression";
    private static final String TARGET_FIELD = "approval";
    private static final String PROBABILITY_FALSE = "probability(false)";
    private static final String PROBABILITY_TRUE = "probability(true)";

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.001);
    private static PMMLRuntime pmmlRuntime;

    private double category;
    private double urgency;
    private double targetPrice;
    private double price;
    private String expectedResult;
    private double expectedProbTrue;
    private double expectedProbFalse;

    public OrderApprovalRegressionTest(double category, double urgency, double targetPrice,
                                       double price, String expectedResult,
                                       double expectedProbTrue,
                                       double expectedProbFalse) {
        this.category = category;
        this.urgency = urgency;
        this.targetPrice = targetPrice;
        this.price = price;
        this.expectedResult = expectedResult;
        this.expectedProbTrue = expectedProbTrue;
        this.expectedProbFalse = expectedProbFalse;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(MODEL_NAME, FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "true", 0.9999999999997606, 2.3936408410918375E-13},
                {5.8, 2.6, 4.0, 1.2, "true", 0.99999999994975, 5.0250026362164135E-11},
                {5.7, 3.0, 4.2, 1.2, "true", 0.99999999997361, 2.639000129533997E-11},
                {5.0, 3.3, 1.4, 0.2, "true", 0.9999999998162474, 1.837525687164998E-10},
                {5.4, 3.9, 1.3, 0.4, "true", 0.9999999999906577, 9.342304707615767E-12},
                {0.0, 0.0, 0.0, 0.0, "false", 0.011016039943412057, 0.988983960056588},
        });
    }

    @Test
    public void testOrderApprovalRegression() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("category", category);
        inputData.put("urgency", urgency);
        inputData.put("targetPrice", targetPrice);
        inputData.put("price", price);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_TRUE))
                .isCloseTo(expectedProbTrue, TOLERANCE_PERCENTAGE);
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_FALSE))
                .isCloseTo(expectedProbFalse, TOLERANCE_PERCENTAGE);
    }
}
