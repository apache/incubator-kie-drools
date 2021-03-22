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

package org.kie.pmml.mining.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class RandomForestClassifierMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "RandomForestClassifier.pmml";
    private static final String MODEL_NAME = "RandomForestClassifier";
    private static final String TARGET_FIELD = "Approved";
    private static PMMLRuntime pmmlRuntime;

    private double age;
    private double debt;
    private double yearsEmployed;
    private double income;
    private int expectedResult;

    public RandomForestClassifierMiningTest(double age,
                                            double debt,
                                            double yearsEmployed,
                                            double income,
                                            int expectedResult) {
        this.age = age;
        this.debt = debt;
        this.yearsEmployed = yearsEmployed;
        this.income = income;
        this.expectedResult = expectedResult;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {40.83, 3.5, 0.5, 0, 0},
                {32.25, 1.5, 0.25, 122, 0},
                {28.17, 0.585, 0.04, 1004, 1},
                {29.75, 0.665, 0.25, 0, 0}
        });
    }

    @Test
    public void testMixedMining() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Age", age);
        inputData.put("Debt", debt);
        inputData.put("YearsEmployed", yearsEmployed);
        inputData.put("Income", income);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
