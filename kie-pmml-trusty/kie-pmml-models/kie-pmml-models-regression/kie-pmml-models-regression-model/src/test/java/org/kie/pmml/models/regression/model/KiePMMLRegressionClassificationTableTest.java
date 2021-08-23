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

package org.kie.pmml.models.regression.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class KiePMMLRegressionClassificationTableTest {

    private static final DoubleUnaryOperator FIRST_ITEM_OPERATOR = aDouble -> 1 / aDouble;
    private static final DoubleUnaryOperator SECOND_ITEM_OPERATOR = aDouble -> 1 - aDouble;
    private static final String CASE_A = "caseA";
    private static final String CASE_B = "caseB";
    private static final String PROBABILITY_FALSE = String.format("probability(%s)", CASE_A);
    private static final String PROBABILITY_TRUE = String.format("probability(%s)", CASE_B);
    private final KiePMMLRegressionClassificationTable classificationTable;
    private final double firstTableResult;
    private final double secondTableResult;
    private final String expectedResult;

    public KiePMMLRegressionClassificationTableTest(double firstTableResult, double secondTableResult,
                                                    String expectedResult) {
        this.firstTableResult = firstTableResult;
        this.secondTableResult = secondTableResult;
        this.expectedResult = expectedResult;
        classificationTable = getKiePMMLRegressionClassificationTable();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {24.5, 13.2, CASE_B},
                {10.4, 16.8, CASE_B},
                {0.7, 0.22, CASE_A},
        });
    }

    @Test
    public void evaluateRegression() {
        Map<String, Object> input = new HashMap<>();
        input.put("a", 24);
        input.put("b", 32);
        Object retrieved = classificationTable.evaluateRegression(input);
        assertEquals(expectedResult, retrieved);
        final Map<String, Object> outputFieldsMap = classificationTable.getOutputFieldsMap();
        double expectedDouble = FIRST_ITEM_OPERATOR.applyAsDouble(firstTableResult);
        assertEquals(expectedDouble, outputFieldsMap.get(PROBABILITY_FALSE));
        expectedDouble = SECOND_ITEM_OPERATOR.applyAsDouble(expectedDouble);
        assertEquals(expectedDouble, outputFieldsMap.get(PROBABILITY_TRUE));
    }

    @Test
    public void getProbabilityMap() {
        LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put(CASE_B, firstTableResult);
        resultMap.put(CASE_A, secondTableResult);
        LinkedHashMap<String, Double> retrieved = classificationTable.getProbabilityMap(resultMap,
                                                                                        FIRST_ITEM_OPERATOR,
                                                                                        SECOND_ITEM_OPERATOR);
        double expectedDouble = FIRST_ITEM_OPERATOR.applyAsDouble(firstTableResult);
        assertEquals(expectedDouble, retrieved.get(CASE_B), 0.0);
        expectedDouble = SECOND_ITEM_OPERATOR.applyAsDouble(expectedDouble);
        assertEquals(expectedDouble, retrieved.get(CASE_A), 0.0);
    }

    @Test(expected = KiePMMLException.class)
    public void getProbabilityMapFewInput() {
        LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put(CASE_B, firstTableResult);
        classificationTable.getProbabilityMap(resultMap, FIRST_ITEM_OPERATOR,  SECOND_ITEM_OPERATOR);
    }

    @Test(expected = KiePMMLException.class)
    public void getProbabilityMapTooManyInput() {
        LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put(CASE_B, firstTableResult);
        resultMap.put(CASE_A, secondTableResult);
        resultMap.put("CASE_ELSE", 444.1);
        classificationTable.getProbabilityMap(resultMap, FIRST_ITEM_OPERATOR,  SECOND_ITEM_OPERATOR);
    }

    private KiePMMLRegressionClassificationTable getKiePMMLRegressionClassificationTable() {
        KiePMMLRegressionClassificationTable toReturn = new KiePMMLRegressionClassificationTable() {

            @Override
            public boolean isBinary() {
                return true;
            }

            @Override
            protected LinkedHashMap<String, Double> getProbabilityMap(LinkedHashMap<String, Double> resultMap) {
                return getProbabilityMap(resultMap, FIRST_ITEM_OPERATOR, SECOND_ITEM_OPERATOR);
            }

            @Override
            public Object getTargetCategory() {
                return null;
            }

            @Override
            protected void populateOutputFieldsMapWithProbability(Map.Entry<String, Double> predictedEntry,
                                                                  LinkedHashMap<String, Double> probabilityMap) {
                outputFieldsMap.put(PROBABILITY_FALSE, probabilityMap.get(CASE_A));
                outputFieldsMap.put(PROBABILITY_TRUE, probabilityMap.get(CASE_B));
            }

        };
        toReturn.categoryTableMap.put(CASE_A, getKiePMMLRegressionTable(firstTableResult));
        toReturn.categoryTableMap.put(CASE_B, getKiePMMLRegressionTable(secondTableResult));
        return toReturn;
    }

    private KiePMMLRegressionTable getKiePMMLRegressionTable(double returnedValue) {
        return new KiePMMLRegressionTable() {

            @Override
            public Object evaluateRegression(Map<String, Object> input) {
                return returnedValue;
            }

            @Override
            public Object getTargetCategory() {
                return null;
            }

            @Override
            protected void updateResult(AtomicReference<Double> toUpdate) {

            }

        };
    }
}