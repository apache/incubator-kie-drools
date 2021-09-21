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

package org.kie.pmml.mining.tests;

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
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class MiningModelMixedProbabilitiesTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "MiningModelMixedProbabilities.pmml";
    private static final String MODEL_NAME = "MiningModelMixedProbabilities";
    private static final String TARGET_FIELD = "APPROVED";
    private static final String PROBABILITY_0 = "probability(0)";
    private static final String PROBABILITY_1 = "probability(1)";
    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.000001);
    private final String AGE = "AGE";
    private final String AMT_INCOME_TOTAL = "AMT_INCOME_TOTAL";
    private final String CNT_CHILDREN = "CNT_CHILDREN";
    private final String DAYS_EMPLOYED = "DAYS_EMPLOYED";
    private final String FLAG_OWN_REALTY = "FLAG_OWN_REALTY";
    private final String FLAG_WORK_PHONE = "FLAG_WORK_PHONE";
    private final String FLAG_OWN_CAR = "FLAG_OWN_CAR";

    private double age;
    private double amtIncomeTotal;
    private double cntChildren;
    private double daysEmployed;
    private double flagOwnRealty;
    private double flagWorkPhone;
    private double flagOwnCar;
    private double probability0;
    private double probability1;

    private static PMMLRuntime pmmlRuntime;

    private double expectedResult;

    public MiningModelMixedProbabilitiesTest(double age,
                                             double amtIncomeTotal,
                                             double cntChildren,
                                             double daysEmployed,
                                             double flagOwnRealty,
                                             double flagWorkPhone,
                                             double flagOwnCary,
                                             double expectedResult,
                                             double probability0,
                                             double probability1) {
        this.age = age;
        this.amtIncomeTotal = amtIncomeTotal;
        this.cntChildren = cntChildren;
        this.daysEmployed = daysEmployed;
        this.flagOwnRealty = flagOwnRealty;
        this.flagWorkPhone = flagWorkPhone;
        this.flagOwnCar = flagOwnCary;
        this.expectedResult = expectedResult;
        this.probability0 = probability0;
        this.probability1 = probability1;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {18.0, 0.0, 2.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.1952380952380952, 0.8047619047619049},
                {20.0, 30000.55, 0.23, 100.0, 1.0, 1.0, 0.0, 1.0, 0.42857142857142855, 0.57142857142857155},
        });
    }

    @Test
    public void testMiningModelMixedProbabilities() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(AGE, age);
        inputData.put(AMT_INCOME_TOTAL, amtIncomeTotal);
        inputData.put(CNT_CHILDREN, cntChildren);
        inputData.put(DAYS_EMPLOYED, daysEmployed);
        inputData.put(FLAG_OWN_REALTY, flagOwnRealty);
        inputData.put(FLAG_WORK_PHONE, flagWorkPhone);
        inputData.put(FLAG_OWN_CAR, flagOwnCar);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_0)).isNotNull();
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_0)).isCloseTo(probability0, TOLERANCE_PERCENTAGE);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_1)).isNotNull();
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_1)).isCloseTo(probability1, TOLERANCE_PERCENTAGE);
    }
}
