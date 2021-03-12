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

import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedLinearRegressionTest {

    static Random random = new Random();

    @BeforeAll
    static void initRandom() {
        random.setSeed(0);
    }

    // check the overspecified case, no intercept
    @Test
    void testOverspecifiedNoIntercept() {
        double[][] x = {
                { 1., 10., 3., -4. },
                { 10., 5., -3., 3.7 },
                { 14., -6.6, 7., 14. },
                { -20., 15., 3.3, 1. },
                { 0., 3., -1., 2.2 },
                { 17., -3, 0., 7. }
        };
        double[] y = { 104., 88.2, 130., 102.4, 35.2, 80. };
        double[] sampleWeights = { .1, .1, .1, .1, .3, .3 };
        double[] actualCoefs = { 4., 10., 8., 6. };

        WeightedLinearRegressionResults wlrr =
                WeightedLinearRegression.fit(x, y, sampleWeights, false, random);
        assertArrayEquals(actualCoefs, wlrr.getCoefficients(), 1e-6);
        assertEquals(0.0, wlrr.getMSE(), 1e-6);
        assertEquals(1.0, wlrr.getGof(), 1e-6);

    }

    // check the overspecified case, with intercept
    @Test
    void testOverspecifiedIntercept() {
        double[][] x = {
                { 1., 10., 3., -4. },
                { 10., 5., -3., 3.7 },
                { 14., -6.6, 7., 14. },
                { -20., 15., 3.3, 1. },
                { 0., 3., -1., 2.2 },
                { 17., -3, 0., 7. }
        };
        double[] y = { 109., 93.2, 135., 107.4, 40.2, 85. };
        double[] sampleWeights = { .1, .1, .1, .1, .3, .3 };
        double[] actualCoefs = { 4., 10., 8., 6. };

        WeightedLinearRegressionResults wlrr =
                WeightedLinearRegression.fit(x, y, sampleWeights, true, random);
        assertArrayEquals(actualCoefs, wlrr.getCoefficients(), 1e-6);
        assertEquals(5., wlrr.getIntercept(), 1e-6);
        assertEquals(0.0, wlrr.getMSE(), 1e-6);
        assertEquals(1.0, wlrr.getGof(), 1e-6);
    }

    // check the overspecified case with intercept, with also random error in the observations.
    @Test
    void testOverspecifiedWithError() {
        double[][] x = {
                { 1., 10., 3. },
                { 10., 5., -3. },
                { 14., -6.6, 7. },
                { -20., 15., 3.3 },
                { 0., 3., -1. },
                { 17., -3, 0. }
        };
        double[] y = { 131.24777803, 72.68862812, 51.48328659, 105.24910402, 23.76140738, 41.08339528 };
        double[] sampleWeights = { 0.11155536, 0.2297424, 0.18834107, 0.30395088, 0.06050119, 0.10590911 };
        double[] actualCoefs = { 4., 10., 8. };

        WeightedLinearRegressionResults wlrr =
                WeightedLinearRegression.fit(x, y, sampleWeights, true, random);

        // there is a random error in the given observations, so make sure we get close to the actual coefficients
        assertArrayEquals(actualCoefs, wlrr.getCoefficients(), 1.0);

        // the random error in the observations is between 0 and 10, so the MSE should be less than or equal to 10.
        assertTrue(wlrr.getMSE() <= 10.);

    }

    // check the underspecified case with no intercept. The coefficient matrix here is un-invertible, so we must
    // use jittering. This adds some randomness to the algorithm, so we run it 100 times to check for stability
    @Test
    void testUnderspecifiedNoIntercept() {
        double[][] x = {
                { 1., 10., 3., -4. },
                { 10., 5., -3., 3.7 },
                { 14., -6.6, 7., 14. },
        };
        double[] y = { 104., 88.2, 130. };
        double[] sampleWeights = { .8, .1, .1 };

        // since there's some randomness in the jitter invert, let's make sure it's stable
        for (int run = 0; run < 100; run++) {
            WeightedLinearRegressionResults wlrr =
                    WeightedLinearRegression.fit(x, y, sampleWeights, false, random);
            assertEquals(0.0, wlrr.getMSE(), 1e-6);
            assertEquals(1.0, wlrr.getGof(), 1e-6);
        }
    }

    // check the underspecified case with intercept. The coefficient matrix here is un-invertible, so we must
    // use jittering. This adds some randomness to the algorithm, so we run it 100 times to check for stability
    @Test
    void testUnderspecifiedIntercept() {
        double[][] x = {
                { 1., 10., 3., -4. },
                { 10., 5., -3., 3.7 },
                { 14., -6.6, 7., 14. },
        };
        double[] y = { 103., 87.2, 129. };
        double[] sampleWeights = { .8, .1, .1 };

        // since there's some randomness in the jitter invert, let's make sure it's stable
        for (int run = 0; run < 100; run++) {
            WeightedLinearRegressionResults wlrr =
                    WeightedLinearRegression.fit(x, y, sampleWeights, true, random);
            assertEquals(0.0, wlrr.getMSE(), 1e-6);
            assertEquals(1.0, wlrr.getGof(), 1e-6);
        }
    }

    // === testing some edge cases ===
    // when we only have one sample, the GoF calculation should break
    @Test
    void testOneSample() {
        double[][] x = {
                { 1., 2., 3., 4. },
        };
        double[] y = { 72. };
        double[] sampleWeights = { 1. };

        assertThrows(ArithmeticException.class,
                () -> WeightedLinearRegression.fit(x, y, sampleWeights, true, random));
    }

    // if we have only one feature, should be an easy calculation
    @Test
    void testOneFeature() {
        double[][] x = {
                { 1. },
                { 4. },
                { 10. },
                { 5. }
        };
        double[] y = { 5., 20., 50., 25., };
        double[] sampleWeights = { 1., 1., 1., 1. };
        double[] actualCoefs = { 5. };

        WeightedLinearRegressionResults wlrr =
                WeightedLinearRegression.fit(x, y, sampleWeights, false, random);
        assertArrayEquals(actualCoefs, wlrr.getCoefficients(), 1e-6);
        assertEquals(0.0, wlrr.getIntercept(), 1e-6);
        assertEquals(0.0, wlrr.getMSE(), 1e-6);
        assertEquals(1.0, wlrr.getGof(), 1e-6);
    }

    // === testing error cases ===
    @Test
    void testSampleMismatch() {
        double[][] x = {
                { 1., 10., 3., -4. },
                { 10., 5., -3., 3.7 },
                { 14., -6.6, 7., 14. },
        };
        double[] y = { 103., 87.2 };
        double[] sampleWeights = { .8, .1, .1 };

        // since there's some randomness in the jitter invert, let's make sure it's stable

        assertThrows(IllegalArgumentException.class,
                () -> WeightedLinearRegression.fit(x, y, sampleWeights, true, random));
    }

    @Test
    void testZeroWeights() {
        double[][] x = {
                { 1., 10., 3., -4. },
                { 10., 5., -3., 3.7 },
                { 14., -6.6, 7., 14. },
        };
        double[] y = { 103., 87.2 };
        double[] sampleWeights = { 0., 0., 0. };

        // since there's some randomness in the jitter invert, let's make sure it's stable

        assertThrows(IllegalArgumentException.class,
                () -> WeightedLinearRegression.fit(x, y, sampleWeights, true, random));
    }
}
