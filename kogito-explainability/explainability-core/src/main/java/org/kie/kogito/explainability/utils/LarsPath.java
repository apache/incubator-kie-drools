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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LarsPath {
    // adapted from https://github.com/scikit-learn/scikit-learn/blob/0d378913be6d7e485b792ea36e9268be31ed52d0/sklearn/linear_model/_least_angle.py
    private LarsPath() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LarsPath.class);

    /**
     * Grab the maximum covariance from this iteration, as well as statistics about this value
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void updateCovarianceTrackers(LarsPathDataCarrier lpdc) {
        if (lpdc.getCov().getDimension() > 0) {
            lpdc.setcIdx(lpdc.getCov().map(Math::abs).map(x -> Precision.round(x, 16)).getMaxIndex());
            lpdc.setC_(lpdc.getCov().getEntry(lpdc.getcIdx()));
        } else {
            lpdc.setC_(0);
            lpdc.setcIdx(0);
        }
        lpdc.setC(Math.abs(lpdc.getC_()));
        //System.out.printf("C %f %n",lpdc.getC());
        lpdc.getAlphas().setEntry(lpdc.getnIter(), lpdc.getC() / lpdc.getnSamples());
    }

    /**
     * See if we've developed a degenerate regressor, and drop it if so
     * This is really, really rare, I can't manage to trigger it at all
     *
     * @param diag: The bottom-right value of the L matrix of the Cholesky decomposition
     * @param lpdc: The Lars path data carrier
     */
    private static void checkRegressorDegeneracy(double diag, LarsPathDataCarrier lpdc) {
        if (diag < 1e-7) {
            String logMessage = String.format(
                    "Regressors in active set degenerate." +
                            "Dropping a regressor, after %d iterations," +
                            " Reduce max_iter or increase eps parameters.",
                    lpdc.getnIter());
            LOGGER.warn(logMessage);
            lpdc.setCov(lpdc.getCovNotShortened());
            lpdc.getCov().setEntry(0, 0);
            MatrixUtilsExtensions.swap(lpdc.getCov(), lpdc.getcIdx(), 0);
            lpdc.setDegenerateRegressor(true);
        } else {
            lpdc.setDegenerateRegressor(false);
        }
    }

    /**
     * Compute the Gram matrix of the regression
     *
     * @param lpdc: The Lars path data carrier
     * @return the gram matrix
     */
    private static RealMatrix computeGram(LarsPathDataCarrier lpdc, boolean simulateNActiveIncrement) {
        int adj = simulateNActiveIncrement ? 0 : 1;
        RealMatrix xtSubset = lpdc.getXT().getSubMatrix(0, lpdc.getnActive() - adj, 0, lpdc.getnSamples() - 1);
        return MatrixUtilsExtensions.matrixDot(xtSubset, xtSubset.transpose());
    }

    /**
     * Grab the cholesky decomposition of M * M.T, where M=X.T[:nActive, :nSamples]=xtSubset
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void getCholeskyDecomposition(LarsPathDataCarrier lpdc) {
        CholeskyDecomposition decomp;
        int nActive = lpdc.getnActive();
        if (!lpdc.isDrop()) {
            // grab some temporary variables from within the data carrier
            int cIdx = lpdc.getcIdx();
            double c_ = lpdc.getC_();
            lpdc.getSignActive().setEntry(nActive, Math.signum(c_));
            int n = cIdx + nActive;

            // swap cov cIdx and 0
            MatrixUtilsExtensions.swap(lpdc.getCov(), cIdx, 0);
            MatrixUtilsExtensions.swap(lpdc.getIndices(), n, nActive);
            MatrixUtilsExtensions.swap(lpdc.getXT(), n, nActive);
            lpdc.setX(lpdc.getXT().transpose());

            lpdc.setCovNotShortened(lpdc.getCov().copy());
            lpdc.setCov(lpdc.getCov().getSubVector(1, lpdc.getCov().getDimension() - 1));

            // grab the decomposition
            decomp = new CholeskyDecomposition(computeGram(lpdc, true));
            lpdc.setDecomp(decomp);

            // if the bottom right element of the lower triangular decomposition of $defactored is 0, we have
            // a degeneracy issue we need to address.
            RealMatrix lowerDefactored = decomp.getL();
            double diag = lowerDefactored.getEntry(lowerDefactored.getRowDimension() - 1,
                    lowerDefactored.getColumnDimension() - 1);
            checkRegressorDegeneracy(diag, lpdc);
            if (lpdc.isDegenerateRegressor()) {
                return;
            }
            lpdc.getActive().add(lpdc.getIndices()[nActive]);
            lpdc.setnActive(nActive + 1);

        } else {
            decomp = new CholeskyDecomposition(computeGram(lpdc, false), 1e-16, -1e-12);
            lpdc.setDecomp(decomp);
        }
    }

    /**
     * See if the most recent alpha value is very close to zero. If so, break out of the LARS algorithm
     *
     * @param lpdc: The Lars path data carrier
     * @return bool: whether the break condition is satisfied
     */
    private static boolean minimumAlphaBreakCondition(LarsPathDataCarrier lpdc) {
        int nIter = lpdc.getnIter();
        int adj = nIter > 0 ? 1 : 0;

        RealVector alphas = lpdc.getAlphas();
        RealMatrix coefs = lpdc.getCoefs();
        RealVector coef = coefs.getRowVector(nIter);
        RealVector prevCoef = coefs.getRowVector(nIter - adj);

        if (alphas.getEntry(nIter) <= lpdc.getEqualityTolerance()) {
            if (Math.abs(alphas.getEntry(nIter)) > lpdc.getEqualityTolerance()) {
                if (nIter > 0) {
                    double ss = alphas.getEntry(nIter - 1) / (alphas.getEntry(nIter - 1) - alphas.getEntry(nIter));
                    coef = prevCoef.mapAdd(ss).ebeMultiply(coef.subtract(prevCoef));
                }
                lpdc.getAlphas().setEntry(nIter, 0);
            }
            lpdc.getCoefs().setRowVector(nIter, coef);
            return true;
        }
        return false;
    }

    /**
     * See if we've exceeded the maxmium possible iterations or hit the user-configured max-iterations
     *
     * @param lpdc: The Lars path data carrier
     * @return bool: whether the break condition is satisfied
     */
    private static boolean maximumIterationBreakCondition(LarsPathDataCarrier lpdc) {
        return (lpdc.getnIter() >= lpdc.getMaxIterations() || lpdc.getnActive() >= lpdc.getnFeatures());
    }

    /**
     * Check if the solution found is satisfactorily well-fit before we've hit max iterations
     *
     * @param lpdc: The Lars path data carrier
     * @return bool: whether the break condition is satisfied
     */
    private static boolean earlyStoppingBreakCondition(LarsPathDataCarrier lpdc) {
        RealVector alphas = lpdc.getAlphas();
        int nIter = lpdc.getnIter();
        int nActive = lpdc.getnActive();

        if (nIter > 0 && alphas.getEntry(nIter - 1) < alphas.getEntry(nIter)) {
            String logMessage = String.format(
                    "Early stopping the lars path, as the residues " +
                            "are small and the current value of alpha is no " +
                            "longer well controlled. %d iterations, alpha=%.3f, " +
                            "previous alpha=%.3f, with an active set of %d " +
                            "regressors.",
                    nIter, alphas.getEntry(nIter), alphas.getEntry(nIter - 1), nActive);
            LOGGER.warn(logMessage);
            return true;
        }
        return false;
    }

    /**
     * Compute the least-squares solution of our current solution, then normalize to unit vector
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void getNormalizedLeastSquares(LarsPathDataCarrier lpdc) {
        RealVector signActiveSubset = lpdc.getSignActive().getSubVector(0, lpdc.getnActive());
        RealVector leastSquares = lpdc.getDecomp().getSolver().solve(signActiveSubset);

        //adjust least squares
        double normalizationFactor;
        if (leastSquares.getDimension() == 1 && leastSquares.getEntry(0) == 0) {
            leastSquares.setEntry(0, 1.0);
            normalizationFactor = 1.0;
        } else {
            normalizationFactor = 1 / Math.sqrt(Arrays.stream(leastSquares.ebeMultiply(signActiveSubset).toArray()).sum());
            leastSquares.mapMultiplyToSelf(normalizationFactor);
        }
        lpdc.setLeastSquares(leastSquares);
        lpdc.setNormalizationFactor(normalizationFactor);
    }

    /**
     * Find the direction and magnitude of the correlations between our solution and the data
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void getCorrelationDirection(LarsPathDataCarrier lpdc) {
        RealVector corrEqDir;
        RealVector eqDir = lpdc.getXT().getSubMatrix(0, lpdc.getnActive() - 1, 0, lpdc.getnSamples() - 1)
                .transpose()
                .operate(lpdc.getLeastSquares());
        int nActive = lpdc.getnActive();
        int nFeatures = lpdc.getnFeatures();
        int nSamples = lpdc.getnSamples();
        double gamma;
        double c = lpdc.getC();
        double normalizationFactor = lpdc.getNormalizationFactor();

        if (nActive < nFeatures) {
            corrEqDir = lpdc.getXT().getSubMatrix(nActive, nFeatures - 1, 0, nSamples - 1).operate(eqDir);
            corrEqDir.mapToSelf(x -> Precision.round(x, 16));
            RealVector cov = lpdc.getCov();
            double tiny = lpdc.getTiny();
            double g1 = MatrixUtilsExtensions.minPos(
                    cov.map(x -> c - x).ebeDivide(corrEqDir.map(x -> normalizationFactor - x + tiny)));
            double g2 = MatrixUtilsExtensions.minPos(
                    cov.mapAdd(c).ebeDivide(corrEqDir.mapAdd(normalizationFactor + tiny)));
            gamma = Math.min(Math.min(g1, g2), c / normalizationFactor);
        } else {
            corrEqDir = MatrixUtils.createRealVector(new double[] {});
            gamma = c / normalizationFactor;
        }
        lpdc.setCorrEqDir(corrEqDir);
        lpdc.setGamma(gamma);
    }

    /**
     * Find the z-vector = -coef[a]/least_squares[a] for all a in the active regressor set
     * Also find the minimum positive value within z
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void setZInformation(LarsPathDataCarrier lpdc) {
        RealVector coef = lpdc.getCoefs().getRowVector(lpdc.getnIter());
        lpdc.setZ(
                MatrixUtils.createRealVector(
                        lpdc.getActive().stream().mapToDouble(a -> -coef.getEntry(a)).toArray())
                        .ebeDivide(
                                lpdc.getLeastSquares().mapAdd(lpdc.getEqualityTolerance())));
        lpdc.setzPos(MatrixUtilsExtensions.minPos(lpdc.getZ()));
    }

    /**
     * If the minimum positive z is less than gamma, we need to drop a feature.
     * First, find the indices of the minimum positive z-values, these will be our active regressors
     * Next, swap the signs of those active regressors, and set gamma and drop values
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void getActiveIndices(LarsPathDataCarrier lpdc) {
        lpdc.setDrop(false);
        double zPos = lpdc.getzPos();
        RealVector z = lpdc.getZ();
        if (zPos < lpdc.getGamma()) {
            lpdc.setIdx(
                    IntStream.range(0, z.getDimension())
                            .filter(i -> Math.abs(z.getEntry(i) - zPos) < lpdc.getEqualityTolerance())
                            .boxed()
                            .collect(Collectors.toSet()));
            for (int i : lpdc.getIdx()) {
                lpdc.getSignActive().setEntry(i, -lpdc.getSignActive().getEntry(i));
            }
            if (lpdc.isLasso()) {
                lpdc.setGamma(zPos);
            }
            lpdc.setDrop(true);
        }
    }

    /**
     * Update the coef and alpha values given what we've calculated in this iteration.
     * This might entail expanding our return objects.
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void trackCoefficientsAndAlphas(LarsPathDataCarrier lpdc) {
        int nIter = lpdc.getnIter();
        int nActive = lpdc.getnActive();
        int nFeatures = lpdc.getnFeatures();
        int maxFeatures = lpdc.getMaxFeatures();
        List<Integer> active = lpdc.getActive();

        // do we need to expand our return objects?
        if (nIter >= lpdc.getCoefs().getRowDimension()) {
            int addFeatures = 2 * Math.max(1, (maxFeatures - nActive));
            RealMatrix newCoefs = MatrixUtils.createRealMatrix(nIter + addFeatures, nFeatures);
            newCoefs.setSubMatrix(lpdc.getCoefs().getData(), 0, 0);
            lpdc.setCoefs(newCoefs);
            lpdc.setAlphas(lpdc.getAlphas().append(MatrixUtils.createRealVector(new double[addFeatures])));
        }

        // update the coefficients
        for (int i = 0; i < active.size(); i++) {
            double newVal = lpdc.getCoefs().getEntry(nIter - 1, active.get(i)) +
                    (lpdc.getGamma() * lpdc.getLeastSquares().getEntry(i));
            lpdc.getCoefs().setEntry(nIter, active.get(i), newVal);
        }
    }

    /**
     * Adjust the covariance matrix in the corrEqDir direction by a factor of gamma
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void adjustCovarianceByCorrelationDirection(LarsPathDataCarrier lpdc) {
        if (lpdc.getCorrEqDir().getDimension() > 0) {
            lpdc.setCov(
                    lpdc.getCov().subtract(lpdc.getCorrEqDir().mapMultiply(lpdc.getGamma())));
        }
    }

    /**
     * If we've previously assigned the drop flag, we need to drop a feature from the regression
     *
     * @param lpdc: The Lars path data carrier
     */
    private static void dropFeature(LarsPathDataCarrier lpdc) {
        lpdc.setnActive(lpdc.getnActive() - 1);
        int nActive = lpdc.getnActive();

        lpdc.setActive(
                IntStream.range(0, lpdc.getActive().size())
                        .filter(i -> !lpdc.getIdx().contains(i))
                        .map(i -> lpdc.getActive().get(i))
                        .boxed()
                        .collect(Collectors.toList()));
        RealMatrix coefs = lpdc.getCoefs();
        RealVector y = lpdc.getY();

        for (int ii : lpdc.getIdx()) {
            for (int i = ii; i < nActive; i++) {
                MatrixUtilsExtensions.swap(lpdc.getXT(), i, i + 1);
                lpdc.setX(lpdc.getXT().transpose());
                MatrixUtilsExtensions.swap(lpdc.getIndices(), i, i + 1);
            }
            RealVector activeCoef = MatrixUtils.createRealVector(
                    lpdc.getActive().stream()
                            .mapToDouble(a -> coefs.getEntry(lpdc.getnIter(), a)).toArray());
            RealVector residual = y.subtract(lpdc.getX().getSubMatrix(0, lpdc.getX().getRowDimension() - 1, 0, nActive - 1).operate(activeCoef));
            lpdc.setCov(MatrixUtils.createRealVector(new double[] { lpdc.getXT().getRowVector(nActive).dotProduct(residual) })
                    .append(lpdc.getCov()));
        }

        lpdc.setSignActive(
                MatrixUtils.createRealVector(
                        IntStream.range(0, lpdc.getSignActive().getDimension())
                                .filter(i -> !lpdc.getIdx().contains(i))
                                .mapToDouble(i -> lpdc.getSignActive().getEntry(i))
                                .toArray()));
        lpdc.setSignActive(lpdc.getSignActive().append(0.));
    }

    /**
     * If we've early-stopped, our result objects might be too large/small. In this case, truncate them to the appropriate size
     * Then create the results container
     *
     * @param lpdc: The Lars path data carrier
     * @return the LarsPathResults container
     */
    private static LarsPathResults truncatedAndFormattedResults(LarsPathDataCarrier lpdc) {
        int nIter = lpdc.getnIter();
        RealVector rawAlphas = lpdc.getAlphas();
        RealMatrix rawCoefs = lpdc.getCoefs();

        if (nIter + 1 < rawAlphas.getDimension()) {
            lpdc.setAlphas(
                    rawAlphas.getSubVector(0, Math.min(nIter + 1, rawAlphas.getDimension())));
            lpdc.setCoefs(
                    rawCoefs.getSubMatrix(0,
                            Math.min(nIter, rawCoefs.getRowDimension() - 1), 0, lpdc.getnFeatures() - 1));
        }
        return new LarsPathResults(lpdc.getCoefs().transpose(), lpdc.getAlphas(), lpdc.getActive(), nIter);
    }

    /**
     * Compute the LarsPath regression given some input data X, observations y, and a maximum iteration count for
     * the algorithm.
     *
     * @param X: The input data matrix of size nSamples x nFeatures
     * @param y: The vector of observations of size nSamples
     * @param maxIterations The maximum number of iterations to run. If maxIterations < nFeatures, then the
     *        algorithm will try and find a solution with nFeatures non-zero regressors. Otherwirse, will
     *        return a solution with maxIterations non-zero regressors
     * @return the LarsPathResults container
     */
    public static LarsPathResults fit(RealMatrix X, RealVector y, int maxIterations, boolean lasso) {
        if (X.getRowDimension() != y.getDimension()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Number of rows of X (%d) must match number of entries in y (%d)!",
                            X.getRowDimension(), y.getDimension()));
        }

        // initialize the data carrier that will contain all intermediate results from the algorithm
        LarsPathDataCarrier lpdc = new LarsPathDataCarrier(X, y, maxIterations, lasso);

        while (true) {
            updateCovarianceTrackers(lpdc);

            // check preliminary break conditions
            if (minimumAlphaBreakCondition(lpdc) || maximumIterationBreakCondition(lpdc))
                break;
            // get the decomposition of the X transpose subset
            getCholeskyDecomposition(lpdc);
            if (lpdc.isDegenerateRegressor()) {
                continue;
            }

            if (lpdc.isLasso() && earlyStoppingBreakCondition(lpdc)) {
                break;
            }

            //adjust least squares of current solution
            getNormalizedLeastSquares(lpdc);

            //extract the directions of each features
            getCorrelationDirection(lpdc);

            // grab the z-vector
            setZInformation(lpdc);

            // find indicies of minimum positive z-values
            getActiveIndices(lpdc);

            // nIter ++
            lpdc.incrementnIter();

            // save the results we've computed in this iteration
            trackCoefficientsAndAlphas(lpdc);

            // update the covariance matrix
            adjustCovarianceByCorrelationDirection(lpdc);

            // drop features if required
            if (lpdc.isLasso() && lpdc.isDrop()) {
                dropFeature(lpdc);
            }
        }

        // format and package results
        return truncatedAndFormattedResults(lpdc);
    }
}
