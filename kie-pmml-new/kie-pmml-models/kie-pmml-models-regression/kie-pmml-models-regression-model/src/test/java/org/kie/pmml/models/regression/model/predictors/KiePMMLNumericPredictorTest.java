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

import java.util.Collections;

import org.junit.Test;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;

import static org.junit.Assert.assertEquals;

public class KiePMMLNumericPredictorTest {

    private static final String NAME = "NAME";
    private static final double COEFFICIENT = 35.6;

    private KiePMMLNumericPredictor predictor;

    @Test
    public void evaluateExponent1() {
        predictor = new KiePMMLNumericPredictor(NAME, 1, COEFFICIENT, Collections.emptyList());
        double input = 17.13;
        double expected = input * COEFFICIENT;
        assertEquals(expected, predictor.evaluate(input), 0.0);
    }

    @Test
    public void evaluateExponentNot1() {
        predictor = new KiePMMLNumericPredictor(NAME, 26, COEFFICIENT, Collections.emptyList());
        double input = 17.13;
        double expected = Math.pow(input, 26) * COEFFICIENT;
        assertEquals(expected, predictor.evaluate(input), 0.0);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void evaluateNotNumber() {
        predictor = new KiePMMLNumericPredictor(NAME, 26, COEFFICIENT, Collections.emptyList());
        assertEquals(0.0, predictor.evaluate("UNKNOWN"), 0.0);
    }
}