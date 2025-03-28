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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RegressionNormalizationLogitTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "RegressionNormalizationLogit";
    private static final String MODEL_NAME = "RegressionNormalizationLogit";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private double x;
    private double y;

    public void initRegressionNormalizationLogitTest(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0}, {-1, 2}, {0.5, -2.5}, {3, 1}, {25, 50},
                {-100, 250}, {-100.1, 800}, {-8, 12.5}, {-1001.1, -500.2}, {-1701, 508}
        });
    }

    private static double normalizedRegressionFunction(double x, double y) {
        final double regressionValue = 2 * x + y + 5;
        return 1 / (1 + Math.exp(-regressionValue));
    }

    @MethodSource("data")
    @ParameterizedTest
    void testNormalizationMethodsRegression(double x, double y) throws Exception {
        initRegressionNormalizationLogitTest(x, y);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", x);
        inputData.put("y", y);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result).isNotNull();
        assertThat(pmml4Result.getResultVariables()).containsKey(TARGET_FIELD);
        assertThat((Double) pmml4Result.getResultVariables().get(TARGET_FIELD))
                .isEqualTo(normalizedRegressionFunction(x, y));
    }
}
