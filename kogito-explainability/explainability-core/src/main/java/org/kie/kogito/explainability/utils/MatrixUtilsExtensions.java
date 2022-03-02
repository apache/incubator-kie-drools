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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.util.Pair;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;

public class MatrixUtilsExtensions {
    public enum Axis {
        ROW,
        COLUMN
    }

    private static final String SHAPE_STRING = "Matrix %s shape: %d x %d";

    private MatrixUtilsExtensions() {
        throw new IllegalStateException("Utility class");
    }

    // === Creation ops ================================================================================================
    /**
     * Convert a prediction input to a row vector array compatible with matrix ops
     * 
     * @param p: the prediction inputs to convert into a double[][] row vector
     * @return double[][] array, the converted matrix
     */
    public static RealVector vectorFromPredictionInput(PredictionInput p) {
        return MatrixUtils.createRealVector(p.getFeatures().stream()
                .mapToDouble(f -> f.getValue().asNumber())
                .toArray());
    }

    /**
     * Convert a list of prediction inputs to an array compatible with matrix ops
     * 
     * @param ps: the list of prediction inputs to convert into a double[][] array
     * @return double[][] array, the converted matrix
     */
    public static RealMatrix matrixFromPredictionInput(List<PredictionInput> ps) {
        return MatrixUtils.createRealMatrix(
                ps.stream()
                        .map(p -> p.getFeatures().stream()
                                .mapToDouble(f -> f.getValue().asNumber())
                                .toArray())
                        .toArray(double[][]::new));
    }

    /**
     * Convert a prediction input to a row vector array compatible with matrix ops
     * 
     * @param p: the prediction inputs to convert into a double[][] row vector
     * @return double[][] array, the converted matrix
     */
    public static RealVector vectorFromPredictionOutput(PredictionOutput p) {
        return MatrixUtils.createRealVector(p.getOutputs().stream()
                .mapToDouble(f -> f.getValue().asNumber())
                .toArray());
    }

    /**
     * Convert a list of prediction outputs to an array compatible with matrix ops
     * 
     * @param ps: the list of prediction outputs to convert into a double[][] array
     * @return double[][] array, the converted matrix
     */
    public static RealMatrix matrixFromPredictionOutput(List<PredictionOutput> ps) {
        return MatrixUtils.createRealMatrix(
                ps.stream()
                        .map(p -> p.getOutputs().stream()
                                .mapToDouble(o -> o.getValue().asNumber())
                                .toArray())
                        .toArray(double[][]::new));
    }

    // === RealMAtrix Operations =======================================================================================
    /**
     * Compute the Moore-Pensor Psuedoinverse of a matrix via SVD
     *
     * @param a A RealMatrix to be psuedoinverted
     * @return The psuedoinversion of a
     *
     */
    public static RealMatrix getPsuedoInverse(RealMatrix a) {
        SingularValueDecomposition svd = new SingularValueDecomposition(a);
        RealMatrix u = svd.getU();
        RealMatrix v = svd.getV();
        RealMatrix sigma = svd.getS();

        for (int i = 0; i < sigma.getRowDimension(); i++) {
            double entry = sigma.getEntry(i, i);
            if (entry > 1e-6) {
                sigma.setEntry(i, i, 1 / entry);
            } else {
                sigma.setEntry(i, i, 0);
            }
        }
        sigma = sigma.transpose();
        return v.multiply((sigma.multiply(u.transpose())));
    }

    /**
     * Attempt to invert the matrix. If it's numerically non-invertible, use Moore-Penrose Psuedoinverse via
     * SVD instead
     *
     * @param a A RealMatrix to be inverted
     * @return The inversion or psuedoinversion of a
     *
     */
    public static RealMatrix safeInvert(RealMatrix a) {
        try {
            return MatrixUtils.inverse(a, 1e-6);
        } catch (SingularMatrixException e) {
            return getPsuedoInverse(a);
        }
    }

    /**
     * Sums the rows of a RealMatrix together
     *
     * @param m the matrix to be row-summed
     * @return RealVector, the sum of all rows
     *
     */
    public static RealVector rowSum(RealMatrix m) {
        RealVector out = org.apache.commons.math3.linear.MatrixUtils.createRealVector(new double[m.getColumnDimension()]);
        for (int i = 0; i < m.getRowDimension(); i++) {
            out = out.add(m.getRowVector(i));
        }
        return out;
    }

    /**
     * Sums the columns of a RealMatrix together
     *
     * @param m the matrix to be column-summed
     * @return RealVector, the sum of all columns
     *
     */
    public static RealVector colSum(RealMatrix m) {
        RealVector out = org.apache.commons.math3.linear.MatrixUtils.createRealVector(new double[m.getRowDimension()]);
        for (int i = 0; i < m.getColumnDimension(); i++) {
            out = out.add(m.getColumnVector(i));
        }
        return out;
    }

    /**
     * Sums the squared rows of a RealMatrix together
     *
     * @param m the matrix to be row-square-summed
     * @return RealVector, the sum of all rows squared
     *
     */
    public static RealVector rowSquareSum(RealMatrix m) {
        RealVector out = org.apache.commons.math3.linear.MatrixUtils.createRealVector(new double[m.getColumnDimension()]);
        for (int i = 0; i < m.getRowDimension(); i++) {
            RealVector rv = m.getRowVector(i);
            out = out.add(rv.ebeMultiply(rv));
        }
        return out;
    }

    /**
     * Subtract a vector from each row of a matrix
     *
     * @param m the matrix to operate on
     * @param v the vector to subtract from each row of m
     * @return RealMatrix m-v
     *
     */
    public static RealMatrix vectorDifference(RealMatrix m, RealVector v, Axis a) {
        if (a == Axis.ROW) {
            RealMatrix out = m.createMatrix(m.getRowDimension(), m.getColumnDimension());
            for (int i = 0; i < m.getRowDimension(); i++) {
                out.setRowVector(i, m.getRowVector(i).subtract(v));
            }
            return out;
        } else {
            RealMatrix out = m.createMatrix(m.getRowDimension(), m.getColumnDimension());
            for (int i = 0; i < m.getColumnDimension(); i++) {
                out.setColumnVector(i, m.getColumnVector(i).subtract(v));
            }
            return out;
        }
    }

    /**
     * Perform element-wise product of a vector and each row of a matrix
     *
     * @param m the matrix to operate on
     * @param v the vector to multiply
     * @return RealMatrix m*v
     *
     */
    public static RealMatrix vectorRowProduct(RealMatrix m, RealVector v) {
        int mRows = m.getRowDimension();
        int mCols = m.getColumnDimension();
        int vSize = v.getDimension();

        if (mCols != vSize) {
            throw new IllegalArgumentException("Columns of matrix A must match size of vector b" +
                    String.format(SHAPE_STRING, "A", mRows, mCols) +
                    String.format("Size of vector b: %d", vSize));
        }

        RealMatrix out = MatrixUtils.createRealMatrix(mRows, mCols);
        for (int row = 0; row < mRows; row++) {
            out.setRowVector(row, m.getRowVector(row).ebeMultiply(v));
        }
        return out;
    }

    public static RealMatrix map(RealMatrix m, UnivariateFunction op) {
        RealMatrix output = m.copy();
        for (int i = 0; i < m.getRowDimension(); i++) {
            output.setRowVector(i, m.getRowVector(i).map(op));
        }
        return output;
    }

    /*
     * Retrieve a list of columns of a matrix
     *
     * @param x: double array
     *
     * @param idxs: the columns to return
     *
     * @return matrix, each column corresponding to the ith column from idxs.
     */
    public static RealMatrix getCols(RealMatrix x, List<Integer> idxs) {
        if (idxs.isEmpty()) {
            throw new IllegalArgumentException("Empty column idxs passed to getCols");
        }

        RealMatrix out = MatrixUtils.createRealMatrix(new double[x.getRowDimension()][idxs.size()]);
        for (int col = 0; col < idxs.size(); col++) {
            if (idxs.get(col) >= x.getColumnDimension() || idxs.get(col) < 0) {
                throw new IllegalArgumentException(
                        String.format("Column index %d output bounds, matrix only has %d column(s)", idxs.get(col), x.getColumnDimension()));
            }
            out.setColumnVector(col, x.getColumnVector(idxs.get(col)));
        }
        return out;
    }

    /**
     * Perform a row-wise dot product between two matrices A and B, where ith,jth value of the
     * output is the dot product between ith row of A and the jth col of B
     *
     * @param a the first matrix in the product of shape x,y
     * @param b the second matrix in the product of shape y,z
     * @return RealMatrix of shape x, z
     *
     */
    public static RealMatrix matrixDot(RealMatrix a, RealMatrix b) {
        int aRows = a.getRowDimension();
        int aCols = a.getColumnDimension();
        int bRows = b.getRowDimension();
        int bCols = b.getColumnDimension();

        if (aCols != bRows) {
            throw new IllegalArgumentException("Columns of matrix A must match rows of matrix B" +
                    String.format(SHAPE_STRING, "A", aRows, aCols) +
                    String.format(SHAPE_STRING, "B", bRows, bCols));
        }

        RealMatrix out = MatrixUtils.createRealMatrix(aRows, bCols);
        for (int row = 0; row < aRows; row++) {
            for (int col = 0; col < bCols; col++) {
                out.setEntry(row, col, a.getRowVector(row).dotProduct(b.getColumnVector(col)));
            }
        }
        return out;
    }

    /**
     * Perform a row-wise mean of a matrix, with $batchSize rows included per mean
     *
     * @param m the matrix to be row-mean of size n x m
     * @param batchSize the number of rows to be included per mean
     * @return RealMatrix with n//batchSize rows
     *
     */
    public static RealMatrix batchRowMean(RealMatrix m, int batchSize) {
        int mRows = m.getRowDimension();
        if (mRows % batchSize != 0) {
            throw new IllegalArgumentException(String.format(
                    "# Rows of matrix A (%d) must be an exact product of batch size %d", mRows, batchSize));
        }
        RealMatrix result = MatrixUtils.createRealMatrix(mRows / batchSize, m.getColumnDimension());
        for (int i = 0; i < m.getRowDimension(); i += batchSize) {
            RealVector batch = m.getRowVector(i);
            for (int j = i + 1; j < i + batchSize; j++) {
                batch = batch.add(m.getRowVector(j));
            }
            batch.mapDivideToSelf(batchSize);
            result.setRowVector(i / batchSize, batch);
        }
        return result;
    }

    // specific optimizations to minimize unneccesary allocation =======================================================
    /**
     * For some matrix A, diagonal matrix B, and vector C, compute (A^T B A)^T and A^T B C. These can be combined
     * to avoid iterating across A twice
     * 
     * @param A: Matrix
     * @param B: Diagonal matrix, represented as a vector of its diagonal
     * @param C: Vector
     * @return (A^T B A)^T and A^T B C as first and second elements of a Pair
     */
    public static Pair<RealMatrix, RealVector> jointATBATandATBC(RealMatrix A, RealVector B, RealVector C) {
        RealMatrix atbat = MatrixUtils.createRealMatrix(A.getColumnDimension(), A.getColumnDimension());
        RealVector atbc = MatrixUtils.createRealVector(new double[A.getColumnDimension()]);

        for (int i = 0; i < A.getColumnDimension(); i++) {
            double atbcElement = 0;
            for (int j = 0; j < A.getColumnDimension(); j++) {
                double atbatElement = 0;
                for (int k = 0; k < B.getDimension(); k++) {
                    atbatElement += A.getEntry(k, j) * B.getEntry(k) * A.getEntry(k, i);
                    if (j == 0) {
                        atbcElement += A.getEntry(k, i) * B.getEntry(k) * C.getEntry(k);
                    }
                }
                atbat.setEntry(i, j, atbatElement);
                if (j == 0) {
                    atbc.setEntry(i, atbcElement);
                }
            }
        }
        return new Pair<>(atbat, atbc);

    }

    // === REAL VECTOR STATISTICS =====================================
    /**
     * Find the minimum positive value of a vector. Returns the max double if no values are positive.
     * this mirrors behavior of https://github.com/scikit-learn/scikit-learn/blob/0d378913be6d7e485b792ea36e9268be31ed52d0/sklearn/utils/arrayfuncs.pyx#L21
     *
     * @param v the vector to find the minimum positive double within
     * @return the minimum positive value if any exist, otherwise the maximum double
     *
     */

    public static double minPos(RealVector v) {
        double minPos = Double.MAX_VALUE;
        for (int i = 0; i < v.getDimension(); i++) {
            double vI = v.getEntry(i);
            if (vI > 0 && vI < minPos) {
                minPos = vI;
            }
        }
        return minPos;
    }

    /**
     * Find nonzero indexs of a vector.
     *
     * @param v the vector to find nonzeros in
     * @return a list of nonzero indexes
     *
     */
    public static List<Integer> nonzero(RealVector v) {
        List<Integer> output = new ArrayList<>();
        for (int i = 0; i < v.getDimension(); i++) {
            if (v.getEntry(i) != 0) {
                output.add(i);
            }
        }
        return output;
    }

    /**
     * Find the variance of a vector
     *
     * @param v the vector to compute the variance of
     * @return var(v)
     *
     */
    public static double variance(RealVector v) {
        double mean = Arrays.stream(v.toArray()).sum() / v.getDimension();
        return Arrays.stream(v.map(a -> Math.pow(a - mean, 2)).toArray()).sum() / v.getDimension();
    }

    /**
     * Find the total sum of a vector
     *
     * @param v the vector to compute the sum of
     * @return sum(v)
     *
     */
    public static double sum(RealVector v) {
        return Arrays.stream(v.toArray()).sum();
    }

    // === SWAP FUNCTIONS ==============================================================================================
    /**
     * Functions to swap the ith and jth element of x in-place
     *
     * @param x the object to perform the swap within
     * @param i the first swap index
     * @param j the second swap index
     * @return RealMatrix m-v
     *
     */
    public static void swap(RealMatrix x, int i, int j) {
        double[] tmp = x.getRow(i);
        x.setRow(i, x.getRow(j));
        x.setRow(j, tmp);
    }

    public static void swap(RealVector x, int i, int j) {
        double tmp = x.getEntry(i);
        x.setEntry(i, x.getEntry(j));
        x.setEntry(j, tmp);
    }

    public static void swap(int[] x, int i, int j) {
        int tmp = x[i];
        x[i] = x[j];
        x[j] = tmp;
    }

}
