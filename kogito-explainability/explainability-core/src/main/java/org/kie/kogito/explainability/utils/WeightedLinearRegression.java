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
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/**
 * Performs a weighted linear regression over the provided features, observations, and weights
 * The algorithm is modified from modified from Dr. Walt Fair's WLR algorithm here:
 * https://www.codeproject.com/Articles/25335/An-Algorithm-for-Weighted-Linear-Regression?msg=4467580#xx4467580xx
 * We use Dr. Debabrata DasGupta's In-Place Matrix Inversion by Modified Gauss-Jordan algorithm to perform matrix
 * inversion, as described here:
 * https://www.researchgate.net/publication/271296470_In-Place_Matrix_Inversion_by_Modified_Gauss-Jordan_Algorithm,
 */
public class WeightedLinearRegression {
    private WeightedLinearRegression() {
        throw new IllegalStateException("Utility class");
    }

    // MAIN ALGORITHM ==================================================================================================
    /**
     * Fit the WLR model to the features.
     *
     * @param features An {@code nsamples features nfeatures} RealMatrix;
     *        each row contains one datapoint of size [nfeatures]
     * @param observations An {@code nsamples} RealVector, where y[n] is the observation for features point n.
     * @param sampleWeights An {@code nsamples} RealVector, where sampleWeights[n] is the weighting of features point n.
     *
     * @return C, an {@code nfeatures} RealVector of coefficients as computed by the regression.
     *         In the case where {@code intercept} is true, the last value of {@code C} is the intercept.
     *
     */
    public static WeightedLinearRegressionResults fit(
            RealMatrix features, RealVector observations, RealVector sampleWeights, boolean intercept)
            throws IllegalArgumentException, ArithmeticException {
        // if we want to compute an intercept, add a dummy feature at last column.
        int nfeatures = intercept ? features.getColumnDimension() + 1 : features.getColumnDimension();
        int nsamples = observations.getDimension();
        int dof = nsamples - nfeatures;

        if (features.getRowDimension() != nsamples) {
            throw new IllegalArgumentException(
                    String.format("Num sample mismatch: Number of rows in the features (%d)", features.getRowDimension()) +
                            String.format(" must match number of observations (%d)", nsamples));
        }

        double weightSum = Arrays.stream(sampleWeights.toArray()).sum();
        if (weightSum == 0) {
            throw new ArithmeticException("Weights cannot sum to zero!");
        }

        // add dummy intercept feature if intercept is true
        if (intercept) {
            features = WeightedLinearRegression.adjustFeatureMatrix(features);
        }

        Pair<RealMatrix, RealVector> jointXTWXandXTWY = MatrixUtilsExtensions.jointATBATandATBC(features, sampleWeights, observations);
        RealMatrix xtWXInv = MatrixUtilsExtensions.safeInvert(jointXTWXandXTWY.getFirst());
        RealVector xtWY = jointXTWXandXTWY.getSecond();
        RealVector coefficients = xtWXInv.operate(xtWY);

        ModelSquareSums mss = WeightedLinearRegression.getRSSandTSS(features, observations,
                sampleWeights, weightSum, coefficients, true);
        double mse = mss.residualSquareSum / weightSum;
        RealVector stdErrors = WeightedLinearRegression.getVarianceMatrix(dof, nfeatures, xtWXInv, mss);
        RealVector pvalues = WeightedLinearRegression.getPValues(dof, nfeatures, stdErrors, coefficients);

        // mark the model as being fit and return coefficients
        return new WeightedLinearRegressionResults(coefficients, intercept, nsamples - nfeatures, mse, stdErrors, pvalues);
    }

    /**
     * From a set of coefficients that determine a linear model, given some inputs X, observations Y, and sampleWeights, find the
     * MSE of the model's predictions
     * 
     * @param features An {@code nsamples features nfeatures} RealMatrix;
     *        each row contains one datapoint of size [nfeatures]
     * @param observations An {@code nsamples} RealVector, where y[n] is the actual observation for f(x[n]).
     * @param sampleWeights An {@code nsamples} RealVector, where sampleWeights[n] is the weighting of the nth sample.
     * @param coefficients: n {@code nsamples} RealVector giving the coefficients of the model.
     *
     * @return the mean square error of these predictions
     *
     */
    public static double getMSE(RealMatrix features, RealVector observations, RealVector sampleWeights,
            RealVector coefficients) {
        double weightSum = Arrays.stream(sampleWeights.toArray()).sum();
        if (weightSum == 0) {
            throw new ArithmeticException("Weights cannot sum to zero!");
        }
        ModelSquareSums mss = WeightedLinearRegression.getRSSandTSS(features, observations,
                sampleWeights, weightSum, coefficients, false);
        return mss.residualSquareSum / weightSum;
    }

    /**
     * Add a dummy column of all 1s to the feature matrix if intercept is true
     * This acts as a constant term, therefore the coefficient of this dummy feature is the intercept
     *
     * @param features A RealMatrix of size nsamples features nfeatures} array of doubles; each row contains one
     *        datapoint of size [nfeatures]
     *
     * @return adjustedFeatures: A RealMatrix of size nsamples x (nfeatures+1)
     *
     */
    private static RealMatrix adjustFeatureMatrix(RealMatrix features) {
        int nsamples = features.getRowDimension();
        int nfeatures = features.getColumnDimension() + 1;
        RealMatrix adjustedFeatures = MatrixUtils.createRealMatrix(nsamples, nfeatures);
        adjustedFeatures.setSubMatrix(features.getData(), 0, 0);
        adjustedFeatures.setColumnVector(nfeatures - 1, MatrixUtils.createRealVector(new double[nsamples]).mapAdd(1));
        return adjustedFeatures;
    }

    // MODEL METRICS ===================================================================================================
    private static class ModelSquareSums {
        public final double residualSquareSum;
        public final double totalSquareSum;

        ModelSquareSums(double residualSquareSum, double totalSquareSum) {
            this.residualSquareSum = residualSquareSum;
            this.totalSquareSum = totalSquareSum;
        }
    }

    /**
     * Compute the residualSquareSum and totalSquareSum of the model over the input data
     *
     * @param features A RealMatrix of size nsamples features nfeatures} array of doubles; each row contains one
     *        datapoint of size [nfeatures]
     * @param observations An {@code nsamples} RealVector, where y[n] is the actual observation for f(x[n]).
     * @param sampleWeights An {@code nsamples} RealVector, where sampleWeights[n] is the weighting of the nth sample.
     * @param weightSum: the sum of all sample weight
     * @param coefficients: n {@code nsamples} RealVector giving the coefficients of the model.
     * @param needObservationVariance boolean, whether the observations need a non-zero variance. A zero variance can be
     *        problematic in certain cases, so this flag if an error needs to be thrown in
     *        those circumstances
     *
     * @return adjustedFeatures: A RealMatrix of size nsamples x (nfeatures+1)
     *
     */
    private static ModelSquareSums getRSSandTSS(RealMatrix features, RealVector observations, RealVector sampleWeights,
            double weightSum,
            RealVector coefficients, boolean needObservationVariance) {

        double yBar = sampleWeights.dotProduct(observations) / weightSum;
        RealVector residual = observations.subtract(features.operate(coefficients));
        RealVector variance = observations.mapSubtract(yBar);
        double residualSquareSum = sampleWeights.dotProduct(residual.ebeMultiply(residual));
        double totalSquareSum = sampleWeights.dotProduct(variance.ebeMultiply(variance));

        if (needObservationVariance && totalSquareSum == 0) {
            throw new ArithmeticException("Total variance of observations is zero." +
                    " Use more samples to correct this error");
        }
        return new ModelSquareSums(residualSquareSum, totalSquareSum);
    }

    private static RealVector getVarianceMatrix(int dof, int nfeatures, RealMatrix invertedLSMatrix, ModelSquareSums mss) {
        double residualMeanSquare = mss.residualSquareSum / dof;

        return MatrixUtils.createRealVector(
                IntStream.range(0, nfeatures)
                        .mapToDouble(i -> Math.sqrt(invertedLSMatrix.getEntry(i, i) * residualMeanSquare))
                        .toArray());
    }

    private static RealVector getPValues(int dof, int nfeatures, RealVector coefficientError, RealVector coefficients) {
        if (dof <= 0) {
            return MatrixUtils.createRealVector(new double[nfeatures]).mapAdd(Double.POSITIVE_INFINITY);
        }
        RealVector tvalues = coefficients.ebeDivide(coefficientError);
        TDistribution tdist = new TDistribution(dof);
        return tvalues.map(x -> 2 * (1 - tdist.cumulativeProbability(x)));
    }
}
