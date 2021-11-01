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

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.TDistribution;

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
     * @param features An {@code nsamples features nfeatures} array of doubles;
     *        each row contains one datapoint of size [nfeatures]
     * @param observations An {@code nsamples} array, where y[n] is the observation for features point n.
     * @param sampleWeights An {@code nsamples} array, where sampleWeights[n] is the weighting of features point n.
     * @param random Random generator used inside jitterInvert
     *
     * @return C, an {@code nfeatures} array of coefficients as computed by the regression.
     *         In the case where {@code intercept} is true, the last value of {@code C} is the intercept.
     *
     */
    public static WeightedLinearRegressionResults fit(
            double[][] features, double[] observations, double[] sampleWeights, boolean intercept, Random random)
            throws IllegalArgumentException, ArithmeticException {
        // if we want to compute an intercept, add a dummy feature at last column.
        int nfeatures = intercept ? features[0].length + 1 : features[0].length;
        int nsamples = observations.length;

        if (features.length != nsamples) {
            throw new IllegalArgumentException(
                    String.format("Num sample mismatch: Number of rows in the features (%d)", features.length) +
                            String.format(" must match number of observations (%d)", nsamples));
        }

        // add dummy intercept feature if intercept is true
        double[][] adjustedFeatures = WeightedLinearRegression.adjustFeatureMatrix(features, intercept);

        // matrices for solver
        double[][] x = new double[nfeatures][nfeatures];
        double[][] b = new double[nfeatures][1];

        // build X and B matrices
        for (int i = 0; i < nfeatures; i++) {
            b[i][0] = 0;
            for (int ii = 0; ii < nfeatures; ii++) {
                x[i][ii] = 0;
                for (int j = 0; j < nsamples; j++) {
                    x[i][ii] += (sampleWeights[j] * adjustedFeatures[j][i] * adjustedFeatures[j][ii]);

                    // hijack this loop to build B matrix, but we only need to loop over i and j
                    // therefore only do anything on the first step of the ii loop
                    if (ii == 0) {
                        b[i][0] += (sampleWeights[j] * adjustedFeatures[j][i] * observations[j]);
                    }
                }
            }
        }

        //invert the coefficient matrix
        try {
            x = MatrixUtilsExtensions.jitterInvert(x, 10, 1e-9, random);
        } catch (ArithmeticException e) {
            throw new ArithmeticException(
                    "Weighted Linear Regression: Matrix cannot be inverted! " +
                            "This can be caused by a very under-specified model, where " +
                            "the ratio of samples to features is roughly less than 0.10. This model has a ratio of " +
                            (double) nsamples / nfeatures +
                            ".");
        }

        // recover the coefficients by multiplying the inverse coefficient matrix by B
        double[][] coefficients = MatrixUtilsExtensions.matrixMultiply(x, b);
        double mse = WeightedLinearRegression
                .getMSE(adjustedFeatures, observations, sampleWeights, coefficients);

        double[] stdErrors = WeightedLinearRegression.getVarianceMatrix(adjustedFeatures,
                observations, sampleWeights, coefficients, x);
        double[] pvalues = WeightedLinearRegression.getPValues(nfeatures, nsamples, stdErrors, coefficients);

        // mark the model as being fit and return coefficients
        return new WeightedLinearRegressionResults(coefficients, intercept, nsamples - nfeatures, mse, stdErrors, pvalues);
    }

    /**
     * Add a dummy column of all 1s to the feature matrix if intercept is true
     * This acts as a constant term, therefore the coefficient of this dummy feature is the intercept
     * If we don't want an intercept, return the feature matrix as is
     *
     * @param features An {@code nsamples features nfeatures} array of doubles; each row contains one
     *        datapoint of size [nfeatures]
     * @param intercept A bool value, whether or not we should add the dummy intercept column
     *
     * @return adjustedFeatures: A nsamples x nfeatures if intercept is false or nsamples x (nfeatures+1) matrix
     *         if intercept is true
     *
     */
    private static double[][] adjustFeatureMatrix(double[][] features, boolean intercept) {
        int nsamples = features.length;
        int nfeatures = intercept ? features[0].length + 1 : features[0].length;
        double[][] adjustedFeatures = new double[nsamples][nfeatures];

        for (int i = 0; i < nsamples; i++) {
            if (intercept) {
                System.arraycopy(features[i], 0, adjustedFeatures[i], 0, nfeatures - 1);
                adjustedFeatures[i][nfeatures - 1] = 1;
            } else {
                System.arraycopy(features[i], 0, adjustedFeatures[i], 0, nfeatures);
            }
        }
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

    private static ModelSquareSums getRSSandTSS(double[][] features,
            double[] observations,
            double[] sampleWeights,
            double[][] coefficients) {
        int nfeatures = features[0].length;
        int nsamples = observations.length;
        double yBar = 0;
        double weightSum = 0;
        for (int i = 0; i < nsamples; i++) {
            yBar += sampleWeights[i] * observations[i];
            weightSum += sampleWeights[i];
        }
        if (weightSum == 0) {
            throw new ArithmeticException("Weights cannot sum to zero!");
        }
        yBar /= weightSum;
        double totalSquareSum = 0;
        double residualSquareSum = 0;
        for (int i = 0; i < nsamples; i++) {
            double fI = 0;
            for (int j = 0; j < nfeatures; j++) {
                fI += features[i][j] * coefficients[j][0];
            }
            double residual = (observations[i] - fI);
            double variance = (observations[i] - yBar);
            totalSquareSum += sampleWeights[i] * (variance * variance);
            residualSquareSum += sampleWeights[i] * (residual * residual);
        }
        if (totalSquareSum == 0) {
            throw new ArithmeticException("Total variance of observations is zero." +
                    " Use more samples to correct this error");
        }
        return new ModelSquareSums(residualSquareSum, totalSquareSum);
    }

    private static double[] getVarianceMatrix(double[][] features,
            double[] observations,
            double[] sampleWeights,
            double[][] coefficients,
            double[][] invertedLSMatrix) {

        int nfeatures = features[0].length;
        int nsamples = observations.length;
        int dof = nsamples - nfeatures;
        ModelSquareSums mss = WeightedLinearRegression.getRSSandTSS(features, observations, sampleWeights, coefficients);
        double residualMeanSquare = mss.residualSquareSum / dof;
        double[] coefficientError = new double[nfeatures];
        for (int i = 0; i < nfeatures; i++) {
            for (int j = 0; j < nfeatures; j++) {
                invertedLSMatrix[i][j] *= residualMeanSquare;
            }
            coefficientError[i] = Math.sqrt(invertedLSMatrix[i][i]);
        }

        return coefficientError;
    }

    private static double[] getPValues(int nfeatures, int nsamples, double[] coefficientError,
            double[][] coefficients) {

        int dof = nsamples - nfeatures;
        if (dof <= 0) {
            return IntStream.range(0, nfeatures).mapToDouble(x -> Double.POSITIVE_INFINITY).toArray();
        }
        double[] coefs = MatrixUtilsExtensions.getCol(coefficients, 0);
        double[] tvalues = IntStream.range(0, coefficientError.length)
                .mapToDouble(i -> coefs[i] / coefficientError[i]).toArray();
        TDistribution tdist = new TDistribution(dof);
        return Arrays.stream(tvalues).map(x -> 2 * (1 - tdist.cumulativeProbability(x))).toArray();
    }

    /**
     * Recover the mean square error of the WLR model.
     * 
     * @return the mean squared error of the model
     */
    private static double getMSE(double[][] features,
            double[] observations,
            double[] sampleWeights,
            double[][] coefficients) {

        int nfeatures = features[0].length;
        int nsamples = observations.length;

        double totalResidual = 0;
        double weightSum = 0;
        for (int i = 0; i < nsamples; i++) {
            double fI = 0;
            for (int j = 0; j < nfeatures; j++) {
                fI += features[i][j] * coefficients[j][0];
            }
            double residual = (observations[i] - fI);
            totalResidual += sampleWeights[i] * (residual * residual);
            weightSum += sampleWeights[i];
        }
        if (weightSum == 0) {
            throw new ArithmeticException("Weights cannot sum to zero!");
        }
        return totalResidual / weightSum;
    }
}
