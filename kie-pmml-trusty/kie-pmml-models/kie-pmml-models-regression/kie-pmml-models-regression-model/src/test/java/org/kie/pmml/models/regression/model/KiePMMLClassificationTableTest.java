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
package org.kie.pmml.models.regression.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.testingutility.PMMLRuntimeContextTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class KiePMMLClassificationTableTest {

    // CAUCHIT
    private static final DoubleUnaryOperator FIRST_ITEM_OPERATOR =
            aDouble -> 0.5 + (1 / Math.PI) * Math.atan(aDouble); //  aDouble -> 1 / aDouble;
    private static final DoubleUnaryOperator SECOND_ITEM_OPERATOR = aDouble -> 1 - aDouble;
    //
    private static final String CASE_A = "caseA";
    private static final String CASE_B = "caseB";
    private KiePMMLClassificationTable classificationTable;
    private double firstTableResult;
    private double secondTableResult;
    private String expectedResult;
    private double firstExpectedValue;
    private double secondExpectedValue;

    public void initKiePMMLClassificationTableTest(double firstTableResult,
                                          double secondTableResult,
                                          String expectedResult,
                                          double firstExpectedValue,
                                          double secondExpectedValue) {
        this.firstTableResult = firstTableResult;
        this.secondTableResult = secondTableResult;
        this.expectedResult = expectedResult;
        this.firstExpectedValue = firstExpectedValue;
        this.secondExpectedValue = secondExpectedValue;
        classificationTable = getKiePMMLRegressionClassificationTable();
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {24.5, 13.2, CASE_A, 0.5, 0.5},
                {10.4, 16.8, CASE_A, 0.5, 0.5},
                {-0.7, 123.22, CASE_A, 0.5, 0.5},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void evaluateRegression(double firstTableResult, double secondTableResult, String expectedResult, double firstExpectedValue, double secondExpectedValue) {
        initKiePMMLClassificationTableTest(firstTableResult, secondTableResult, expectedResult, firstExpectedValue, secondExpectedValue);
        PMMLRuntimeContextTest pmmlContextTest = new PMMLRuntimeContextTest();
        Map<String, Object> input = new HashMap<>();
        input.put(CASE_A, firstTableResult);
        input.put(CASE_B, secondTableResult);
        Object retrieved = classificationTable.evaluateRegression(input, pmmlContextTest);
        assertThat(retrieved).isEqualTo(expectedResult);
        final Map<String, Double> probabilityResultMap = pmmlContextTest.getProbabilityResultMap();
        assertThat(probabilityResultMap.get(CASE_A)).isCloseTo(firstExpectedValue, Offset.offset(0.0));
        assertThat(probabilityResultMap.get(CASE_B)).isCloseTo(secondExpectedValue, Offset.offset(0.0));
    }

    @MethodSource("data")
    @ParameterizedTest
    void getProbabilityMap(double firstTableResult, double secondTableResult, String expectedResult, double firstExpectedValue, double secondExpectedValue) {
        initKiePMMLClassificationTableTest(firstTableResult, secondTableResult, expectedResult, firstExpectedValue, secondExpectedValue);
        LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put(CASE_B, firstTableResult);
        resultMap.put(CASE_A, secondTableResult);
        LinkedHashMap<String, Double> retrieved = KiePMMLClassificationTable.getProbabilityMap(resultMap,
                FIRST_ITEM_OPERATOR,
                SECOND_ITEM_OPERATOR);
        double expectedDouble = FIRST_ITEM_OPERATOR.applyAsDouble(firstTableResult);

        assertThat(retrieved.get(CASE_B)).isCloseTo(expectedDouble, Offset.offset(0.0));
        expectedDouble = SECOND_ITEM_OPERATOR.applyAsDouble(expectedDouble);
        assertThat(retrieved.get(CASE_A)).isCloseTo(expectedDouble, Offset.offset(0.0));
    }

    @MethodSource("data")
    @ParameterizedTest
    void getProbabilityMapFewInput(double firstTableResult, double secondTableResult, String expectedResult, double firstExpectedValue, double secondExpectedValue) {
        initKiePMMLClassificationTableTest(firstTableResult, secondTableResult, expectedResult, firstExpectedValue, secondExpectedValue);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
            resultMap.put(CASE_B, firstTableResult);
            KiePMMLClassificationTable.getProbabilityMap(resultMap, FIRST_ITEM_OPERATOR, SECOND_ITEM_OPERATOR);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void getProbabilityMapTooManyInput(double firstTableResult, double secondTableResult, String expectedResult, double firstExpectedValue, double secondExpectedValue) {
        initKiePMMLClassificationTableTest(firstTableResult, secondTableResult, expectedResult, firstExpectedValue, secondExpectedValue);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
            resultMap.put(CASE_B, firstTableResult);
            resultMap.put(CASE_A, secondTableResult);
            resultMap.put("CASE_ELSE", 444.1);
            KiePMMLClassificationTable.getProbabilityMap(resultMap, FIRST_ITEM_OPERATOR, SECOND_ITEM_OPERATOR);
        });
    }

    private KiePMMLClassificationTable getKiePMMLRegressionClassificationTable() {
        Map<String, KiePMMLRegressionTable> categoryTableMapLocal = new HashMap<>();
        categoryTableMapLocal.put(CASE_A, getKiePMMLRegressionTable());
        categoryTableMapLocal.put(CASE_B, getKiePMMLRegressionTable());
        return KiePMMLClassificationTable.builder("", Collections.emptyList())
                .withCategoryTableMap(categoryTableMapLocal)
                .withProbabilityMapFunction(KiePMMLClassificationTable::getCAUCHITProbabilityMap)
                .build();
    }

    private KiePMMLRegressionTable getKiePMMLRegressionTable() {
        return KiePMMLRegressionTable.builder("", Collections.emptyList()).build();
    }
}