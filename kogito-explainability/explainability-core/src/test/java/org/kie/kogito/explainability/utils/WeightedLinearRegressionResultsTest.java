/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeightedLinearRegressionResultsTest {

    // check that WLRR works with no intercept, that everything recovers as expected
    @Test
    void testWLRResultsNoIntercept() {
        double[][] coefficients = { { 5. }, { 1. }, { -1. }, { 3. } };
        double[] flatCoef = { 5., 1., -1., 3. };
        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, false, 1.0, .01);
        assertArrayEquals(flatCoef, wlrr.getCoefficients());
        assertEquals(0.0, wlrr.getIntercept());
        assertEquals(1.0, wlrr.getGof());
        assertEquals(.01, wlrr.getMSE());
    }

    // check that WLRR works with intercept, that the intercept is extracted from coefficient matrix
    @Test
    void testWLRResultWithIntercept() {
        double[][] coefficients = { { 5. }, { 1. }, { -1. }, { 3. } };
        double[] flatCoef = { 5., 1., -1. };
        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, true, 1.0, .01);
        assertArrayEquals(flatCoef, wlrr.getCoefficients());
        assertEquals(3.0, wlrr.getIntercept());
        assertEquals(1.0, wlrr.getGof());
        assertEquals(.01, wlrr.getMSE());
    }

    // check that predict correctly computes the linear function
    @Test
    void testPredictions() {
        double[][] coefficients = { { 5. }, { 1. }, { -1. }, { 3. }, { 5. } };
        double[][] x = {
                { 1., 5., 3., -2 },
                { 10., -1., 0., 4. },
                { -2, 7.5, 6., -3.3 },
        };
        double[] y = { 6., 66., -13.4 };

        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, true, 1.0, .01);
        assertArrayEquals(y, wlrr.predict(x), 1e-6);
    }

    // check that predict throws an error if the input matrix has the wrong number of features compared to the number
    // of coefficients
    @Test
    void testPredictionsWrongNumFeatures() {
        double[][] coefficients = { { 5. }, { 1. }, { -1. }, { 3. }, { 5. } };
        double[][] x = {
                { 1., 5. },
                { 10., -1. },
                { -2, 7.5 },
        };

        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, true, 1.0, .01);
        assertThrows(IllegalArgumentException.class, () -> wlrr.predict(x));
    }
}
