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
package org.kie.pmml.mining.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class MixedMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MiningModel_Mixed";
    private static final String MODEL_NAME = "MixedMining";
    private static final String TARGET_FIELD = "categoricalResult";
    private static final String NUMBER_OF_CLAIMS = "Number of Claims";
    private static final String OUT_DER_FUN_OCCUPATION = "out_der_fun_occupation";
    private static final String OUT_RESIDENCESTATE = "out_residenceState";
    private static final String OUT_FUN_OCCUPATION_REFERRED = "out_fun_occupation_referred";
    private static final String CONSTANT_OCCUPATION = "CONSTANT_OCCUPATION";
    private static final String OUT_NORMDISCRETE_FIELD = "out_normdiscrete_field";
    private static final String OUT_DISCRETIZE_FIELD = "out_discretize_field";
    private static final String OUT_MAPVALUED_FIELD = "out_mapvalued_field";
    private static final String OUT_TEXT_INDEX_NORMALIZATION_FIELD = "out_text_index_normalization_field";
    private static final String TEXT_INPUT = "Testing the app for a few days convinced me the interfaces are " +
            "excellent!";

    private static PMMLRuntime pmmlRuntime;

    private String categoricalX;
    private String categoricalY;
    private double age;
    private String occupation;
    private String residenceState;
    private boolean validLicense;
    private double expectedResult;

    public void initMixedMiningTest(String categoricalX,
                           String categoricalY,
                           double age,
                           String occupation,
                           String residenceState,
                           boolean validLicense,
                           double expectedResult) {
        this.categoricalX = categoricalX;
        this.categoricalY = categoricalY;
        this.age = age;
        this.occupation = occupation;
        this.residenceState = residenceState;
        this.validLicense = validLicense;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"red", "classA", 25.0, "ASTRONAUT", "AP", true, 17.0},
                {"blue", "classA", 2.3, "PROGRAMMER", "KN", true, 36.0},
                {"yellow", "classC", 333.56, "INSTRUCTOR", "TN", false, -58.0},
                {"orange", "classB", 0.12, "ASTRONAUT", "KN", true, 33.0},
                {"green", "classC", 122.12, "TEACHER", "TN", false, 123.0},
                {"green", "classB", 11.33, "INSTRUCTOR", "AP", false, 76.0},
                {"orange", "classB", 423.2, "SKYDIVER", "KN", true, 57.0},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMining(String categoricalX, String categoricalY, double age, String occupation, String residenceState, boolean validLicense, double expectedResult) throws Exception {
        initMixedMiningTest(categoricalX, categoricalY, age, occupation, residenceState, validLicense, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("categoricalX", categoricalX);
        inputData.put("categoricalY", categoricalY);
        inputData.put("age", age);
        inputData.put("occupation", occupation);
        inputData.put("residenceState", residenceState);
        inputData.put("validLicense", validLicense);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", 34.1);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        assertThat(pmml4Result.getResultVariables().get(NUMBER_OF_CLAIMS)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(NUMBER_OF_CLAIMS)).isEqualTo(expectedResult);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_OCCUPATION)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_OCCUPATION)).isEqualTo(occupation);
        assertThat(pmml4Result.getResultVariables().get(OUT_RESIDENCESTATE)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_RESIDENCESTATE)).isEqualTo(residenceState);
        assertThat(pmml4Result.getResultVariables().get(OUT_FUN_OCCUPATION_REFERRED)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_FUN_OCCUPATION_REFERRED)).isEqualTo(CONSTANT_OCCUPATION);
        assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isNotNull();
        if (occupation.equals("SKYDIVER")) {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo("1.0");
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo("0.0");
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isNotNull();
        if (age > 4.2 && age < 30.5) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("abc");
        } else if (age >= 114 && age < 250) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("def");
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("defaultValue");
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isNotNull();
        String expected;
        switch (categoricalX) {
            case "red":
                expected = "der";
                break;
            case "green":
                expected = "neerg";
                break;
            case "blue":
                expected = "eulb";
                break;
            case "orange":
                expected = "egnaro";
                break;
            case "yellow":
                expected = "wolley";
                break;
            default:
                throw new Exception("Unexpected categoricalX " + categoricalX);
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isEqualTo(expected);
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isEqualTo(1.0);
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMiningWithoutRequired(String categoricalX, String categoricalY, double age, String occupation, String residenceState, boolean validLicense, double expectedResult) {
        initMixedMiningTest(categoricalX, categoricalY, age, occupation, residenceState, validLicense, expectedResult);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("categoricalX", categoricalX);
            inputData.put("categoricalY", categoricalY);
            inputData.put("age", age);
            inputData.put("occupation", occupation);
            inputData.put("residenceState", residenceState);
            inputData.put("validLicense", validLicense);
            inputData.put("text_input", TEXT_INPUT);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMiningConvertible(String categoricalX, String categoricalY, double age, String occupation, String residenceState, boolean validLicense, double expectedResult) {
        initMixedMiningTest(categoricalX, categoricalY, age, occupation, residenceState, validLicense, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("categoricalX", categoricalX);
        inputData.put("categoricalY", categoricalY);
        inputData.put("age", String.valueOf(age));
        inputData.put("occupation", occupation);
        inputData.put("residenceState", residenceState);
        inputData.put("validLicense", String.valueOf(validLicense));
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", "34.1");
        assertThat(evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME)).isNotNull();
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMiningNotConvertible(String categoricalX, String categoricalY, double age, String occupation, String residenceState, boolean validLicense, double expectedResult) {
        initMixedMiningTest(categoricalX, categoricalY, age, occupation, residenceState, validLicense, expectedResult);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("categoricalX", categoricalX);
            inputData.put("categoricalY", categoricalY);
            inputData.put("age", age);
            inputData.put("occupation", occupation);
            inputData.put("residenceState", residenceState);
            inputData.put("validLicense", validLicense);
            inputData.put("text_input", TEXT_INPUT);
            inputData.put("input3", true);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMiningInvalidValue(String categoricalX, String categoricalY, double age, String occupation, String residenceState, boolean validLicense, double expectedResult) {
        initMixedMiningTest(categoricalX, categoricalY, age, occupation, residenceState, validLicense, expectedResult);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("categoricalX", categoricalX);
            inputData.put("categoricalY", categoricalY);
            inputData.put("age", age);
            inputData.put("occupation", occupation);
            inputData.put("residenceState", residenceState);
            inputData.put("validLicense", validLicense);
            inputData.put("text_input", TEXT_INPUT);
            inputData.put("input3", 4.1);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }
}
