/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
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
                WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights),
                        false);
        assertArrayEquals(actualCoefs, wlrr.getCoefficients().toArray(), 1e-6);
        assertEquals(0.0, wlrr.getMSE(), 1e-6);

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
                WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights), true);
        assertArrayEquals(actualCoefs, wlrr.getCoefficients().toArray(), 1e-6);
        assertEquals(5., wlrr.getIntercept(), 1e-6);
        assertEquals(0.0, wlrr.getMSE(), 1e-6);
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
                WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights), true);

        // there is a random error in the given observations, so make sure we get close to the actual coefficients
        assertArrayEquals(actualCoefs, wlrr.getCoefficients().toArray(), 1.0);

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
                    WeightedLinearRegression.fit(
                            MatrixUtils.createRealMatrix(x),
                            MatrixUtils.createRealVector(y),
                            MatrixUtils.createRealVector(sampleWeights), false);
            assertEquals(0.0, wlrr.getMSE(), 1e-6);
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
                    WeightedLinearRegression.fit(
                            MatrixUtils.createRealMatrix(x),
                            MatrixUtils.createRealVector(y),
                            MatrixUtils.createRealVector(sampleWeights), true);
            assertEquals(0.0, wlrr.getMSE(), 1e-6);
        }
    }

    // === test some error cases ===
    @Test
    void testStdErr() {
        for (int test = 0; test < 1; test++) {
            double[] trueCoefs = { 1., 2., 3., 4., 5. };
            double[] expectedErr = { 0.519, 0.537, 0.586, 0.415, 0.391 };
            double[] expectedP = { 0.037, 0.415, 0.001, 0.000, 0.000 };
            double[][] x = {
                    { 8.32, 7.9, 0.31, 3.85, 0.05 },
                    { 2.39, 7.59, 4.06, 8.73, 8.59 },
                    { 1.59, 1.1, 4.3, 9.49, 2.13 },
                    { 5.36, 2.64, 4.65, 9.88, 5.25 },
                    { 1.96, 2.44, 0.58, 4.24, 0.3 },
                    { 8.22, 8.07, 0.57, 2.34, 8.89 },
                    { 9.08, 0.56, 2.22, 9.81, 0.34 },
                    { 4.84, 6.52, 3.12, 8.62, 9.79 },
                    { 2.42, 8.5, 9.33, 3.96, 9.9 },
                    { 5.1, 9.88, 8.6, 7.58, 3.0 },
            };
            double[] y = { 33.26402211568451, 107.47389791796185, 72.15586479806592, 96.52857945629758,
                    29.289064802655997, 78.73842411657569, 68.1835699678292, 122.79428874425378, 119.66821422153396,
                    96.08485899842191 };
            double[] sampleWeights = { .1, .1, .1, .1, .1, .1, .1, .1, .1, .1 };
            WeightedLinearRegressionResults wlrr = WeightedLinearRegression.fit(
                    MatrixUtils.createRealMatrix(x),
                    MatrixUtils.createRealVector(y),
                    MatrixUtils.createRealVector(sampleWeights), false);
            RealVector coefs = wlrr.getCoefficients();
            RealVector conf = wlrr.getConf(.01);
            RealVector ub = coefs.add(conf);
            RealVector lb = coefs.subtract(conf);
            assertArrayEquals(expectedErr, wlrr.getStdErrors().toArray(), .01);
            assertArrayEquals(expectedP, wlrr.getPValues().toArray(), .01);

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
                () -> WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights), true));
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
                WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights),
                        false);
        assertArrayEquals(actualCoefs, wlrr.getCoefficients().toArray(), 1e-6);
        assertEquals(0.0, wlrr.getIntercept(), 1e-6);
        assertEquals(0.0, wlrr.getMSE(), 1e-6);
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
                () -> WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights), true));
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
                () -> WeightedLinearRegression.fit(
                        MatrixUtils.createRealMatrix(x),
                        MatrixUtils.createRealVector(y),
                        MatrixUtils.createRealVector(sampleWeights), true));
    }

    // Test mse calculation
    @Test
    void testMSE() {
        RealMatrix X = MatrixUtils.createRealMatrix(5, 5).scalarAdd(1);
        RealVector coefs = MatrixUtils.createRealVector(new double[5]).mapAddToSelf(1.);
        RealVector weights = MatrixUtils.createRealVector(new double[5]).mapAddToSelf(1.);
        RealVector y = MatrixUtils.createRealVector(new double[5]).mapAddToSelf(6);
        double mse = WeightedLinearRegression.getMSE(X, y, weights, coefs);
        assertEquals(1.0, mse);
    }
}
