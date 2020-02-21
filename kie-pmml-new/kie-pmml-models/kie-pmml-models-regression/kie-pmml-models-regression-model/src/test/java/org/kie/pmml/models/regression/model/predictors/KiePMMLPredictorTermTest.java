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

package org.kie.pmml.models.regression.model.predictors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;

import static org.junit.Assert.assertEquals;

public class KiePMMLPredictorTermTest {

    private static final String CATEGORICAL_NAME = "CATEGORICAL_NAME";
    private static final Object CATEGORICAL_VALUE = "CATEGORICAL_VALUE";
    private static final double CATEGORICAL_COEFFICIENT = 35.6;
    private static final String NUMERIC_NAME = "NUMERIC_NAME";
    private static final int NUMERIC_EXPONENT = 17;
    private static final double NUMERIC_COEFFICIENT = 35.6;
    private static final String NAME = "NAME";
    private static final double COEFFICIENT = 11.12;

    private KiePMMLPredictorTerm predictorTerm;

    @Before
    public void setup() {
        KiePMMLCategoricalPredictor categoricalPredictor = new KiePMMLCategoricalPredictor(CATEGORICAL_NAME, CATEGORICAL_VALUE, CATEGORICAL_COEFFICIENT, Collections.emptyList());
        KiePMMLNumericPredictor numericPredictor = new KiePMMLNumericPredictor(NUMERIC_NAME, NUMERIC_EXPONENT, NUMERIC_COEFFICIENT, Collections.emptyList());
        predictorTerm = new KiePMMLPredictorTerm(NAME, Arrays.asList(numericPredictor, categoricalPredictor), COEFFICIENT, Collections.emptyList());
    }

    @Test
    public void evaluate() {
        Map<String, Double> inputMap = new HashMap<>();
        inputMap.put(NUMERIC_NAME, 0.9);
        inputMap.put(CATEGORICAL_NAME, 0.3);
        double retrieved = predictorTerm.evaluate(inputMap);
        double expected = 0.9 * 0.3 * COEFFICIENT;
        assertEquals(expected, retrieved, 0.0);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void evaluateWrongMap() {
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put(NUMERIC_NAME, "0.9");
        inputMap.put(CATEGORICAL_NAME, "0.3");
        predictorTerm.evaluate(inputMap);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void evaluateNotMap() {
        predictorTerm.evaluate(new Date());
    }
}