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

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeightedLinearRegressionResultsTest {
    RealVector coefficients = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
    RealVector flatCoef = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
    RealVector stdErrs = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });

    // check that WLRR works with no intercept, that everything recovers as expected
    @Test
    void testWLRResultsNoIntercept() {
        RealVector coefficients = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
        RealVector flatCoef = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
        RealVector stdErrs = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
        RealVector pvalues = MatrixUtils.createRealVector(new double[4]);
        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients,
                        false, 1, .01, stdErrs, pvalues);
        assertArrayEquals(flatCoef.toArray(), wlrr.getCoefficients().toArray());
        assertArrayEquals(stdErrs.toArray(), wlrr.getStdErrors().toArray());
        assertArrayEquals(pvalues.toArray(), wlrr.getPValues().toArray());
        assertEquals(0.0, wlrr.getIntercept());
        assertEquals(.01, wlrr.getMSE());
    }

    // check that WLRR works with intercept, that the intercept is extracted from coefficient matrix
    @Test
    void testWLRResultWithIntercept() {
        RealVector coefficients = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
        RealVector flatCoef = MatrixUtils.createRealVector(new double[] { 5., 1., -1. });
        RealVector stdErrs = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3. });
        RealVector pvalues = MatrixUtils.createRealVector(new double[4]);

        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, true, 1, .01, stdErrs, pvalues);
        assertArrayEquals(flatCoef.toArray(), wlrr.getCoefficients().toArray());
        assertArrayEquals(stdErrs.toArray(), wlrr.getStdErrors().toArray());
        assertEquals(3.0, wlrr.getIntercept());
        assertEquals(.01, wlrr.getMSE());
    }

    // check that predict correctly computes the linear function
    @Test
    void testPredictions() {
        RealVector coefficients = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3., 5. });
        RealVector stdErrs = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3., 5. });
        RealMatrix x = MatrixUtils.createRealMatrix(new double[][] {
                { 1., 5., 3., -2 },
                { 10., -1., 0., 4. },
                { -2, 7.5, 6., -3.3 },
        });
        RealVector pvalues = MatrixUtils.createRealVector(new double[5]);
        RealVector y = MatrixUtils.createRealVector(new double[] { 6., 66., -13.4 });

        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, true, 1, .01, stdErrs, pvalues);
        assertArrayEquals(y.toArray(), wlrr.predict(x).toArray(), 1e-6);
    }

    // check that predict throws an error if the input matrix has the wrong number of features compared to the number
    // of coefficients
    @Test
    void testPredictionsWrongNumFeatures() {
        RealVector coefficients = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3., 5. });
        ;
        RealVector stdErrs = MatrixUtils.createRealVector(new double[] { 5., 1., -1., 3., 5. });
        RealVector pvalues = MatrixUtils.createRealVector(new double[5]);
        RealMatrix x = MatrixUtils.createRealMatrix(new double[][] {
                { 1., 5. },
                { 10., -1. },
                { -2, 7.5 },
        });

        WeightedLinearRegressionResults wlrr =
                new WeightedLinearRegressionResults(coefficients, true, 1, .01, stdErrs, pvalues);
        assertThrows(IllegalArgumentException.class, () -> wlrr.predict(x));
    }
}
