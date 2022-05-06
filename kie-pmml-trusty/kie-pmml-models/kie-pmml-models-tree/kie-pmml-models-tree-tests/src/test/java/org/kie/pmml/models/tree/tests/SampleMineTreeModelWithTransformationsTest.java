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

package org.kie.pmml.models.tree.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SampleMineTreeModelWithTransformationsTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "SampleMineTreeModelWithTransformations.pmml";
    private static final String MODEL_NAME = "SampleMineTreeModelWithTransformations";
    private static final String TARGET_FIELD = "decision";
    private static final String OUT_DER_TEMPERATURE = "out_der_temperature";
    private static final String OUT_DER_FUN_HUMIDITY_APPLY = "out_der_fun_humidity_apply";
    private static final String OUT_DER_CONSTANT = "out_der_constant";
    private static final String CONSTANT = "constant";
    private static final String WEATHERDECISION = "weatherdecision";
    private static final String OUT_NORMDISCRETE_FIELD = "out_normdiscrete_field";
    private static final String OUT_DISCRETIZE_FIELD = "out_discretize_field";
    private static final String OUT_MAPVALUED_FIELD = "out_mapvalued_field";
    private static final String OUT_TEXT_INDEX_NORMALIZATION_FIELD = "out_text_index_normalization_field";
    private static final String TEXT_INPUT = "Testing the app for a few days convinced me the interfaces are " +
            "excellent!";

    private static PMMLRuntime pmmlRuntime;

    private double temperature;
    private double humidity;
    private String expectedResult;

    public SampleMineTreeModelWithTransformationsTest(double temperature, double humidity, String expectedResult) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.expectedResult = expectedResult;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {30.0, 10.0, "sunglasses"},
                {5.0, 70.0, "umbrella"},
                {10.0, 15.0, "nothing"}
        });
    }

    @Test
    public void testSampleMineTreeModelWithTransformations() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("temperature", temperature);
        inputData.put("humidity", humidity);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", 34.1);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_TEMPERATURE)).isEqualTo(temperature);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_HUMIDITY_APPLY)).isEqualTo(humidity);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CONSTANT)).isEqualTo(CONSTANT);
        assertThat(pmml4Result.getResultVariables().get(WEATHERDECISION)).isEqualTo(expectedResult);
        assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isNotNull();
        if (expectedResult.equals("umbrella")) {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo("1.0");
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo("0.0");
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isNotNull();
        if (temperature > 4.2 && temperature < 9.8) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("abc");
        } else if (temperature >= 15.4 && temperature < 32.1) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("def");
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("defaultValue");
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isNotNull();
        String expected;
        switch (expectedResult) {
            case "sunglasses":
                expected = "sun";
                break;
            case "umbrella":
                expected = "rain";
                break;
            case "nothing":
                expected = "dunno";
                break;
            default:
                throw new Exception("Unexpected expectedResult " + expectedResult);
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isEqualTo(expected);
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isEqualTo(1.0);
    }

    @Test(expected = KiePMMLException.class)
    public void testSampleMineTreeModelWithTransformationsWithoutRequired() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("temperature", temperature);
        inputData.put("humidity", humidity);
        inputData.put("text_input", TEXT_INPUT);
        evaluate(pmmlRuntime, inputData, MODEL_NAME);
    }

    @Test
    public void testSampleMineTreeModelWithTransformationsConvertible() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("temperature", String.valueOf(temperature));
        inputData.put("humidity",String.valueOf(humidity));
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", "34.1");
        assertThat(evaluate(pmmlRuntime, inputData, MODEL_NAME)).isNotNull();
    }

    @Test(expected = KiePMMLException.class)
    public void testSampleMineTreeModelWithTransformationsNotConvertible() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("temperature", temperature);
        inputData.put("humidity", humidity);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", true);
        evaluate(pmmlRuntime, inputData, MODEL_NAME);
    }

    @Test(expected = KiePMMLException.class)
    public void testSampleMineTreeModelWithTransformationsInvalidValue() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("temperature", temperature);
        inputData.put("humidity", humidity);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", 4.1);
        evaluate(pmmlRuntime, inputData, MODEL_NAME);
    }
}
