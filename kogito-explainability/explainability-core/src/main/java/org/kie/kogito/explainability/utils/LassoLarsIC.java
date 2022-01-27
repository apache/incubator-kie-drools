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

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class LassoLarsIC {
    private LassoLarsIC() {
        throw new IllegalStateException("Utility class");
    }

    public enum Criterion {
        AIC,
        BIC
    }

    public static LassoLarsICResults fit(RealMatrix X, RealVector y, Criterion c) {
        return LassoLarsIC.fit(X, y, c, X.getColumnDimension() * 200);
    }

    public static LassoLarsICResults fit(RealMatrix X, RealVector y, Criterion c, int maxIterations) {
        int nSamples = X.getRowDimension();
        double epsilon32 = Math.ulp((float) 1.0);
        double epsilon64 = Math.ulp(1.0);

        // center input data
        RealVector xMean = MatrixUtilsExtensions.rowSum(X).mapDivide(nSamples);
        double yMean = Arrays.stream(y.toArray()).sum() / nSamples;

        RealMatrix xCenter = MatrixUtilsExtensions.vectorDifference(X, xMean, MatrixUtilsExtensions.Axis.ROW);
        RealVector yCenter = y.mapSubtract(yMean);

        LarsPathResults lpResults = LarsPath.fit(xCenter, yCenter, maxIterations, true);

        double K = c == Criterion.AIC ? 2 : Math.log(nSamples);
        RealMatrix residuals = MatrixUtilsExtensions.vectorDifference(
                MatrixUtilsExtensions.matrixDot(xCenter, lpResults.getCoefs()), yCenter,
                MatrixUtilsExtensions.Axis.COLUMN);
        RealVector mse = MatrixUtilsExtensions.rowSquareSum(residuals).mapDivide(residuals.getRowDimension());
        double sigma2 = MatrixUtilsExtensions.variance(yCenter);

        RealVector dof = MatrixUtils.createRealVector(new double[lpResults.getCoefs().getColumnDimension()]);
        RealMatrix coefT = lpResults.getCoefs().transpose();

        for (int k = 0; k < coefT.getRowDimension(); k++) {
            RealVector mask = coefT.getRowVector(k).map(x -> Math.abs(x) > epsilon32 ? 1 : 0);
            double maskSum = Arrays.stream(mask.toArray()).sum();
            if (maskSum == 0) {
                continue;
            }
            dof.setEntry(k, maskSum);
        }
        RealVector criterion = mse.mapMultiply(nSamples).mapDivide(sigma2 + epsilon64).add(dof.mapMultiply(K));
        int best = criterion.getMinIndex();
        RealVector bestCoef = lpResults.getCoefs().getColumnVector(best);
        double bestAlpha = lpResults.getAlphas().getEntry(best);
        double intercept = yMean - xMean.dotProduct(bestCoef);

        return new LassoLarsICResults(bestCoef, bestAlpha, intercept);

    }
}
