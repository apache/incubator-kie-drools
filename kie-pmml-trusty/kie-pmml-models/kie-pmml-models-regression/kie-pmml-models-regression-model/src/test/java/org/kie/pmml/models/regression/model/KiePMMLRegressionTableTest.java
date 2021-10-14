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
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.api.iinterfaces.SerializableFunction;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class KiePMMLRegressionTableTest {

    private static final String TARGET_FIELD = "TARGET_FIELD";
    private static final String FIRST_NUMERIC_INPUT = "FIRST_NUMERIC_INPUT";
    private static final String SECOND_NUMERIC_INPUT = "SECOND_NUMERIC_INPUT";
    private static final String FIRST_CATEGORICAL_INPUT = "FIRST_CATEGORICAL_INPUT";
    private static final String SECOND_CATEGORICAL_INPUT = "SECOND_CATEGORICAL_INPUT";
    private static final SerializableFunction<Double, Double> FIRST_NUMERIC_FUNCTION = aDouble -> 1 / aDouble;
    private static final SerializableFunction<Double, Double> SECOND_NUMERIC_FUNCTION = aDouble -> 1 - aDouble;
    private final KiePMMLRegressionTable regressionTable;
    private final SerializableFunction<Object, Double> firstCategoricalFunction;
    private final SerializableFunction<Object, Double> secondCategoricalFunction;
    private final double firstNumericalInput;
    private final double secondNumericalInput;
    private final double expectedResult;

    public KiePMMLRegressionTableTest(double firstNumericalInput,
                                      double secondNumericalInput,
                                      double firstCategoricalResult,
                                      double secondCategoricalResult,
                                      double expectedResult) {
        firstCategoricalFunction = aObject -> firstCategoricalResult;
        secondCategoricalFunction = aObject -> secondCategoricalResult;
        this.firstNumericalInput = firstNumericalInput;
        this.secondNumericalInput = secondNumericalInput;
        this.expectedResult = expectedResult;
        regressionTable = getKiePMMLRegressionTable();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {36.2, 11.2, 24.5, 13.2, 27.527624309392266},
                {8.12, 3.17, 10.4, 16.8, 25.153152709359606},
                {0.33, 11.57, 0.7, 0.22, -6.61969696969697},
        });
    }

    @Test
    public void evaluateRegression() {
        Map<String, Object> input = new HashMap<>();
        input.put(FIRST_NUMERIC_INPUT, firstNumericalInput);
        input.put(SECOND_NUMERIC_INPUT, secondNumericalInput);
        input.put(FIRST_CATEGORICAL_INPUT, "unused");
        input.put(SECOND_CATEGORICAL_INPUT, "unused");
        Object retrieved = regressionTable.evaluateRegression(input);
        assertEquals(expectedResult, retrieved);
    }

    private KiePMMLRegressionTable getKiePMMLRegressionTable() {
        KiePMMLRegressionTable toReturn = new KiePMMLRegressionTable() {
            @Override
            public Object getTargetCategory() {
                return null;
            }

            @Override
            protected void updateResult(AtomicReference<Double> toUpdate) {

            }
        };
        toReturn.targetField = TARGET_FIELD;
        toReturn.numericFunctionMap.put(FIRST_NUMERIC_INPUT, FIRST_NUMERIC_FUNCTION);
        toReturn.numericFunctionMap.put(SECOND_NUMERIC_INPUT, SECOND_NUMERIC_FUNCTION);
        toReturn.categoricalFunctionMap.put(FIRST_CATEGORICAL_INPUT, firstCategoricalFunction);
        toReturn.categoricalFunctionMap.put(SECOND_CATEGORICAL_INPUT, secondCategoricalFunction);
        return toReturn;
    }
}