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

public class MultipleCategoricalVariablesRegressionTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MultipleRegression";
    private static final String MODEL_NAME = "CategoricalVariablesRegression";
    private static final String TARGET_FIELD = "result";
    private static PMMLRuntime pmmlRuntime;

    private String x;
    private String y;

    public void initMultipleCategoricalVariablesRegressionTest(String x, String y) {
        this.x = x;
        this.y = y;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"red", "classA"}, {"green", "classA"}, {"blue", "classA"}, {"orange", "classA"}, {"yellow", "classA"},
                {"red", "classB"}, {"green", "classB"}, {"blue", "classB"}, {"orange", "classB"}, {"yellow", "classB"},
                {"red", "classC"}, {"green", "classC"}, {"blue", "classC"}, {"orange", "classC"}, {"yellow", "classC"}
        });
    }

    private static double regressionFunction(String x, String y) {
        final Map<String, Double> categoriesMapX = new HashMap<>();
        categoriesMapX.put("red", 5.5);
        categoriesMapX.put("green", 15.0);
        categoriesMapX.put("blue", 12.0);
        categoriesMapX.put("orange", 5.5);
        categoriesMapX.put("yellow", -100.25);

        final Map<String, Double> categoriesMapY = new HashMap<>();
        categoriesMapY.put("classA", 0.0);
        categoriesMapY.put("classB", 20.0);
        categoriesMapY.put("classC", 40.0);

        return categoriesMapX.get(x) + categoriesMapY.get(y) - 22.1;
    }

    @MethodSource("data")
    @ParameterizedTest
    void testCategoricalVariablesRegression(String x, String y) {
        initMultipleCategoricalVariablesRegressionTest(x, y);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", x);
        inputData.put("y", y);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result).isNotNull();
        assertThat(pmml4Result.getResultVariables()).containsKey(TARGET_FIELD);
        assertThat((Double) pmml4Result.getResultVariables().get(TARGET_FIELD))
                .isEqualTo(regressionFunction(x, y));
    }
}
