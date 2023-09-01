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

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class LinearRegressionSampleWithTransformationsTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "LinearRegressionSampleWithTransformations";
    private static final String MODEL_NAME = "LinearRegressionSampleWithTransformations";
    private static final String TARGET_FIELD = "number_of_claims";

    private static final String OUT_NUMBER_OF_CLAIMS = "Number of Claims";
    private static final String OUT_DER_FUN_CAR_LOCATION_REFERRED = "out_der_fun_car_location_referred";
    private static final String OUT_SALARY = "out_salary";
    private static final String OUT_DER_AGE = "out_der_age";
    private static final String OUT_DER_SALARY = "out_der_salary";
    private static final String OUT_DER_CAR_LOCATION = "out_der_car_location";
    private static final String OUT_DER_CAR_LOCATION_REFERRAL = "out_der_car_location_referral";
    private static final String OUT_DER_CONSTANT = "out_der_constant";
    private static final String OUT_DER_FUN_SALARY_FIELDREF = "out_der_fun_salary_fieldref";
    private static final String OUT_DER_FUN_SALARY_CONSTANT = "out_der_fun_salary_constant";
    private static final String OUT_DER_FUN_SALARY_APPLY = "out_der_fun_salary_apply";
    private static final String OUT_DER_FUN_SALARY_APPLY_FUN_SALARY_FIELDREF =
            "out_der_fun_salary_apply_fun_salary_fieldref";
    private static final String OUT_NORMDISCRETE_FIELD = "out_normdiscrete_field";
    private static final String OUT_DISCRETIZE_FIELD = "out_discretize_field";
    private static final String OUT_MAPVALUED_FIELD = "out_mapvalued_field";
    private static final String OUT_TEXT_INDEX_NORMALIZATION_FIELD = "out_text_index_normalization_field";
    private static final String TEXT_INPUT = "Testing the app for a few days convinced me the interfaces are " +
            "excellent!";

    private static final String CONSTANT = "constant";
    private static final String FUN_SALARY_CONSTANT = "FUN_SALARY_CONSTANT";
    private static final String STRING_CONSTANT = "9.87654321E8";
    private static final double DOUBLE_CONSTANT = 9.87654321E8;

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.001);
    private static PMMLRuntime pmmlRuntime;

    private double age;
    private double salary;
    private String car_location;
    private double expectedResult;

    public void initLinearRegressionSampleWithTransformationsTest(double age,
                                                         double salary,
                                                         String car_location,
                                                         double expectedResult) {
        this.age = age;
        this.salary = salary;
        this.car_location = car_location;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {27, 34000, "street", 3116.0},
                {49, 78000, "carpark", 4096.0},
                {57, 72000, "street", 4978.0},
                {61, 123000, "carpark", 5777.0},
                {18, 26000, "street", 2664.0},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testLinearRegressionSampleWithTransformations(double age, double salary, String car_location, double expectedResult) throws Exception {
        initLinearRegressionSampleWithTransformationsTest(age, salary, car_location, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", car_location);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", 34.1);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat((double) pmml4Result.getResultVariables().get(TARGET_FIELD)).isCloseTo(expectedResult,
                TOLERANCE_PERCENTAGE);
        assertThat(pmml4Result.getResultVariables().get(OUT_NUMBER_OF_CLAIMS)).isNotNull();
        assertThat((double) pmml4Result.getResultVariables().get(OUT_NUMBER_OF_CLAIMS)).isCloseTo(expectedResult, TOLERANCE_PERCENTAGE);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_CAR_LOCATION_REFERRED)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_CAR_LOCATION_REFERRED)).isEqualTo(car_location);
        assertThat(pmml4Result.getResultVariables().get(OUT_SALARY)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_SALARY)).isEqualTo(salary);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_AGE)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_AGE)).isEqualTo(age);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_SALARY)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_SALARY)).isEqualTo(salary);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CAR_LOCATION)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CAR_LOCATION)).isEqualTo(car_location);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CAR_LOCATION_REFERRAL)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CAR_LOCATION_REFERRAL)).isEqualTo(car_location);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CONSTANT)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CONSTANT)).isEqualTo(CONSTANT);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_FIELDREF)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_FIELDREF)).isEqualTo(DOUBLE_CONSTANT);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_CONSTANT)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_CONSTANT)).isEqualTo(FUN_SALARY_CONSTANT);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_APPLY)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_APPLY)).isEqualTo(FUN_SALARY_CONSTANT);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_APPLY_FUN_SALARY_FIELDREF)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_FUN_SALARY_APPLY_FUN_SALARY_FIELDREF)).isEqualTo(STRING_CONSTANT);
        assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isNotNull();
        if (car_location.equals("carpark")) {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo(1.0);
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo(0.0);
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
        switch (car_location) {
            case "carpark":
                expected = "inside";
                break;
            case "street":
                expected = "outside";
                break;
            default:
                throw new Exception("Unexpected car_location " + car_location);
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isEqualTo(expected);
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isEqualTo(1.0);
    }

    @MethodSource("data")
    @ParameterizedTest
    void testLinearRegressionSampleWithTransformationsWithoutRequired(double age, double salary, String car_location, double expectedResult) {
        initLinearRegressionSampleWithTransformationsTest(age, salary, car_location, expectedResult);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("age", age);
            inputData.put("salary", salary);
            inputData.put("car_location", car_location);
            inputData.put("text_input", TEXT_INPUT);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testLinearRegressionSampleWithTransformationsConvertible(double age, double salary, String car_location, double expectedResult) {
        initLinearRegressionSampleWithTransformationsTest(age, salary, car_location, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", String.valueOf(age));
        inputData.put("salary", String.valueOf(salary));
        inputData.put("car_location", car_location);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", "34.1");
        assertThat(evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME)).isNotNull();
    }

    @MethodSource("data")
    @ParameterizedTest
    void testLinearRegressionSampleWithTransformationsNotConvertible(double age, double salary, String car_location, double expectedResult) {
        initLinearRegressionSampleWithTransformationsTest(age, salary, car_location, expectedResult);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("age", age);
            inputData.put("salary", salary);
            inputData.put("car_location", car_location);
            inputData.put("text_input", TEXT_INPUT);
            inputData.put("input3", true);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testLinearRegressionSampleInvalidValue(double age, double salary, String car_location, double expectedResult) {
        initLinearRegressionSampleWithTransformationsTest(age, salary, car_location, expectedResult);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("age", age);
            inputData.put("salary", salary);
            inputData.put("car_location", car_location);
            inputData.put("text_input", TEXT_INPUT);
            inputData.put("input3", 4.1);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }
}
