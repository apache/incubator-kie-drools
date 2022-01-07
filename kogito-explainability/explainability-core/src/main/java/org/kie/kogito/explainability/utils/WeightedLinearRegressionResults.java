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

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class WeightedLinearRegressionResults {
    private final RealVector coefficients;
    private final double intercept;
    private final int dof;
    private final double mse;
    private final RealVector stdErrors;
    private final RealVector pvalues;

    /**
     * Store the results of a weighted linear regression into a container class
     *
     * @param coefficients A RealVector of the coefficients+intercept found by the weighted linear regression.
     * @param intercept Whether or not the weighted linear regression computed an intercept. If true, then the
     *        last value of the coefficients array is taken as the intercept. If false, the intercept is
     *        set to 0.
     * @param dof: The degress of freedom of the weighted linear regression
     * @param mse: The mean square error of the weighted linear regression
     * @param stdErrors: The standard errors of each coefficient of the weighted linear regression
     * @param pvalues: The pvalues of each coefficient of the weighted linear regression
     *
     */
    public WeightedLinearRegressionResults(RealVector coefficients, boolean intercept, int dof, double mse,
            RealVector stdErrors, RealVector pvalues) {
        //if intercept is true
        if (intercept) {
            this.coefficients = coefficients.getSubVector(0, coefficients.getDimension() - 1);
            this.intercept = coefficients.getEntry(coefficients.getDimension() - 1);
        } else {
            this.coefficients = coefficients;
            this.intercept = 0.0;
        }

        this.dof = dof;
        this.mse = mse;
        this.stdErrors = stdErrors;
        this.pvalues = pvalues;

    }

    /**
     * Given a matrix of features, return the model's predictions over each datapoint.
     *
     * @param x: A feature matrix, where each row corresponds to a single datapoint.
     *
     * @return y: The values of the computed function for each datapoint x[i]
     */
    public RealVector predict(RealMatrix x) throws IllegalArgumentException {
        if (x.getColumnDimension() != this.coefficients.getDimension()) {
            throw new IllegalArgumentException(
                    String.format("Num feature mismatch: Number of columns in x (%d)", x.getColumnDimension()) +
                            String.format(" must match number of coefficients (%d)", this.coefficients.getDimension()));
        }
        return x.operate(this.coefficients).mapAdd(this.intercept);
    }

    /**
     * @return An array of the coefficients found by the weighted linear regression
     */
    public RealVector getCoefficients() {
        return this.coefficients;
    }

    /**
     * @return The intercept found by the weighted linear regression
     */
    public double getIntercept() {
        return this.intercept;
    }

    /**
     * @return mse: The mean square error of the weighted linear regression.
     */
    public double getMSE() {
        return this.mse;
    }

    /**
     * @return stdError: The standard error of each coefficient of the weighted linear regression.
     */
    public RealVector getStdErrors() {
        return this.stdErrors;
    }

    public RealVector getPValues() {
        return this.pvalues;
    }

    public RealVector getConf(double alpha) {
        TDistribution tdist = new TDistribution(this.dof);
        double q = tdist.inverseCumulativeProbability(1 - alpha / 2);
        return this.stdErrors.mapMultiply(q);
    }
}
