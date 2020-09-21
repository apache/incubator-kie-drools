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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class PredictorTermRegressionTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "PredictorTermRegression.pmml";
    private static final String MODEL_NAME = "PredictorTermRegression";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private double x;
    private double y;
    private double z;

    public PredictorTermRegressionTest(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(MODEL_NAME, FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 0}, {-1, 2, 3}, {0.5, -2.5, 4}, {3, 1, 2}, {25, 50, 15},
                {-100, 250, -10}, {-100.1, 800, 105}, {-8, 12.5, 230}, {-1001, -500, 8}, {-1701, 508, 9}
        });
    }

    private static double regressionFunction(double x, double y, double z) {
        return 2 * x + y + 5 * z * z + 4 * y * z - 2.5 * x * y * z + 5;
    }

    @Test
    public void testPredictorTermRegression() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", x);
        inputData.put("y", y);
        inputData.put("z", z);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables()).containsKey(TARGET_FIELD);
        Assertions.assertThat((Double) pmml4Result.getResultVariables().get(TARGET_FIELD))
                .isEqualTo(regressionFunction(x, y, z));
    }
}
