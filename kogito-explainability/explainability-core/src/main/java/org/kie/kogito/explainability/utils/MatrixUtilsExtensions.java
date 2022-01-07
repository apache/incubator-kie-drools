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

import java.util.*;

import org.apache.commons.math3.linear.*;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;

public class MatrixUtilsExtensions {
    public enum Axis {
        ROW,
        COLUMN
    }

    private static final String shapeString = "Matrix %s shape: %d x %d";

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
    public static double[][] matrixFromPredictionInput(PredictionInput p) {
        return MatrixUtilsExtensions.rowVector(p.getFeatures().stream()
                .mapToDouble(f -> f.getValue().asNumber())
                .toArray());
    }

    /**
     * Convert a list of prediction inputs to an array compatible with matrix ops
     * 
     * @param ps: the list of prediction inputs to convert into a double[][] array
     * @return double[][] array, the converted matrix
     */
    public static double[][] matrixFromPredictionInput(List<PredictionInput> ps) {
        return ps.stream()
                .map(p -> p.getFeatures().stream()
                        .mapToDouble(f -> f.getValue().asNumber())
                        .toArray())
                .toArray(double[][]::new);
    }

    /**
     * Convert a prediction input to a row vector array compatible with matrix ops
     * 
     * @param p: the prediction inputs to convert into a double[][] row vector
     * @return double[][] array, the converted matrix
     */
    public static double[][] matrixFromPredictionOutput(PredictionOutput p) {
        return MatrixUtilsExtensions.rowVector(p.getOutputs().stream()
                .mapToDouble(f -> f.getValue().asNumber())
                .toArray());
    }

    /**
     * Convert a list of prediction outputs to an array compatible with matrix ops
     * 
     * @param ps: the list of prediction outputs to convert into a double[][] array
     * @return double[][] array, the converted matrix
     */
    public static double[][] matrixFromPredictionOutput(List<PredictionOutput> ps) {
        return ps.stream()
                .map(p -> p.getOutputs().stream()
                        .mapToDouble(o -> o.getValue().asNumber())
                        .toArray())
                .toArray(double[][]::new);
    }

    /**
     * Convert a 1D array into a row vector compatible with matrix ops
     * 
     * @param v: the array to convert into a double[][] array
     * @return double[][] array, the converted matrix
     */
    public static double[][] rowVector(double[] v) {
        double[][] out = new double[1][v.length];
        out[0] = v;
        return out;
    }

    /**
     * Convert a 1D array into a column vector compatible with matrix ops
     * 
     * @param v: the array to convert into a double[][] array
     * @return double[][] array, the converted matrix
     */
    public static double[][] columnVector(double[] v) {
        double[][] out = new double[v.length][1];
        for (int i = 0; i < v.length; i++) {
            out[i][0] = v[i];
        }
        return out;
    }

    // === Matrix Information Retrieval ========================================
    /**
     * @param x: the double array to return the matrix shape of
     * @return shape, an int array of length 2, where shape[0] = number of rows and shape[1] is number of columns
     */
    public static int[] getShape(double[][] x) {
        int rows = x.length;
        int cols = x[0].length;
        return new int[] { rows, cols };
    }

    /*
     * Retrieve the ith column of a matrix
     *
     * @param x: double array
     * 
     * @param i: the column to return
     * 
     * @return vector, the ith column of x
     */
    public static double[] getCol(double[][] x, int i) {
        int cols = MatrixUtilsExtensions.getShape(x)[1];
        if (cols <= i || i < 0) {
            throw new IllegalArgumentException(
                    String.format("Column index %d too large, matrix only has %d column(s)", i, cols));
        }
        return Arrays.stream(x).mapToDouble(doubles -> doubles[i]).toArray();
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
    public static double[][] getCols(double[][] x, List<Integer> idxs) {
        if (idxs.isEmpty()) {
            throw new IllegalArgumentException("Empty column idxs passed to getCols");
        }

        int[] shape = MatrixUtilsExtensions.getShape(x);
        double[][] out = new double[shape[0]][idxs.size()];

        for (int i = 0; i < shape[0]; i++) {
            for (int col = 0; col < idxs.size(); col++) {
                if (idxs.get(col) >= shape[1] || idxs.get(col) < 0) {
                    throw new IllegalArgumentException(
                            String.format("Column index %d output bounds, matrix only has %d column(s)", col, shape[1]));
                }
                out[i][col] = x[i][idxs.get(col)];
            }
        }
        return out;
    }

    // === Two matrix operations =======================================================================================
    /**
     * Find the matrix element-wise sum. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the sum
     * @param b double[][]; the second matrix in the sum
     * @return the element-wise sum of a and b
     */
    public static double[][] matrixSum(double[][] a, double[][] b) {
        int[] aShape = MatrixUtilsExtensions.getShape(a);
        int[] bShape = MatrixUtilsExtensions.getShape(b);

        if (!Arrays.equals(aShape, bShape)) {
            throw new IllegalArgumentException("Shape of matrix A must shape of matrix B" +
                    String.format(shapeString, "A", aShape[0], aShape[1]) +
                    String.format(shapeString, "B", bShape[0], bShape[1]));
        }

        double[][] sum = new double[aShape[0]][aShape[1]];
        for (int i = 0; i < aShape[0]; i++) {
            for (int j = 0; j < aShape[1]; j++) {
                sum[i][j] = a[i][j] + b[i][j];
            }
        }
        return sum;
    }

    /**
     * Find the matrix element-wise sum. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the sum
     * @param b double[]; the row to add
     * @return the result from adding b from every row of a
     */
    public static double[][] matrixRowSum(double[][] a, double[] b) {
        int[] aShape = MatrixUtilsExtensions.getShape(a);
        double[][] bMat = MatrixUtilsExtensions.rowVector(b);
        double[][] out = new double[aShape[0]][aShape[1]];

        for (int i = 0; i < aShape[0]; i++) {
            out[i] = MatrixUtilsExtensions.matrixSum(MatrixUtilsExtensions.rowVector(a[i]), bMat)[0];
        }
        return out;
    }

    /**
     * Find the matrix element-wise difference. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the difference
     * @param b double[][]; the second matrix in the difference
     * @return the element-wise matrix difference of a and b
     */
    public static double[][] matrixDifference(double[][] a, double[][] b) {
        double[][] bNeg = MatrixUtilsExtensions.matrixMultiply(b, -1.);
        return matrixSum(a, bNeg);
    }

    /**
     * Find the matrix row-wise difference. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the difference
     * @param b double[]; the row to subtract
     * @return the result from subtracting b from every row of a
     */
    public static double[][] matrixRowDifference(double[][] a, double[] b) {
        double[] bNeg = Arrays.stream(b).map(v -> -v).toArray();
        return matrixRowSum(a, bNeg);
    }

    /**
     * Find the matrix product. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the multiplication
     * @param b double[][]; the second matrix in the multiplication
     * @return the matrix product of a and b
     */
    public static double[][] matrixMultiply(double[][] a, double[][] b) {
        int[] aShape = MatrixUtilsExtensions.getShape(a);
        int[] bShape = MatrixUtilsExtensions.getShape(b);

        if (aShape[1] != bShape[0]) {
            throw new IllegalArgumentException("# columns of matrix A must match # rows of matrix B" +
                    String.format(shapeString, "A", aShape[0], aShape[1]) +
                    String.format(shapeString, "B", bShape[0], bShape[1]));
        }

        double[][] product = new double[aShape[0]][bShape[1]];
        for (int i = 0; i < aShape[0]; i++) {
            for (int j = 0; j < bShape[1]; j++) {
                for (int k = 0; k < aShape[1]; k++) {
                    product[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return product;
    }

    /**
     * Find the matrix-vector dot product. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the multiplication
     * @param b double[]; the vector in the multiplication
     * @return the matrix dot-product of a and b
     */
    public static double[] matrixMultiply(double[][] a, double[] b) {
        int[] aShape = MatrixUtilsExtensions.getShape(a);
        int bShape = b.length;

        if (aShape[1] != bShape) {
            throw new IllegalArgumentException("# columns of matrix A must match length of vector B" +
                    String.format(shapeString, "A", aShape[0], aShape[1]) +
                    String.format("Vector B shape:  %d,", bShape));
        }

        double[] product = new double[aShape[0]];
        for (int i = 0; i < aShape[0]; i++) {
            for (int j = 0; j < bShape; j++) {
                product[i] += a[i][j] * b[j];
            }
        }
        return product;
    }

    /**
     * Find the element-wise product of a matrix and a scalar.
     *
     * @param a double[][]; the first matrix in the multiplication
     * @param b double; the scalar to multiply the matrix by
     * @return the matrix product of a and the scalar b
     */
    public static double[][] matrixMultiply(double[][] a, double b) {
        int[] aShape = MatrixUtilsExtensions.getShape(a);

        double[][] product = new double[aShape[0]][aShape[1]];
        for (int i = 0; i < aShape[0]; i++) {
            for (int j = 0; j < aShape[1]; j++) {
                product[i][j] = a[i][j] * b;
            }
        }
        return product;
    }

    // === Single matrix operations ====================================================================================
    /**
     * Find the sum of the elements within the matrix, along a certain axis
     *
     * @param x double[][]; the matrix within which to compute the sum
     * @param axis enum; the direction which to sum. axis.ROW will add all rows together, axis.COLUMN adds columns
     * @return the result of the sum along that dimension
     */
    public static double[] sum(double[][] x, Axis axis) {
        int[] shape = MatrixUtilsExtensions.getShape(x);
        if (axis == MatrixUtilsExtensions.Axis.ROW) {
            double[][] out = new double[1][shape[1]];
            for (int i = 0; i < shape[0]; i++) {
                out = MatrixUtilsExtensions.matrixSum(out, MatrixUtilsExtensions.rowVector(x[i]));
            }
            return out[0];
        } else {
            double[][] out = new double[1][shape[0]];
            for (int i = 0; i < shape[1]; i++) {
                out = MatrixUtilsExtensions.matrixSum(out, MatrixUtilsExtensions.rowVector(MatrixUtilsExtensions.getCol(x, i)));
            }
            return out[0];
        }
    }

    /**
     * Find the matrix transpose
     *
     * @param x double[][]; the matrix to be transposed
     * @return the transpose of x
     */
    public static double[][] transpose(double[][] x) {
        int[] shape = MatrixUtilsExtensions.getShape(x);

        double[][] transposed = new double[shape[1]][shape[0]];
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                transposed[j][i] = x[i][j];
            }
        }
        return transposed;
    }

    // === Extensions to the Apache MatrixUtils =========================================================================
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
    public static RealMatrix rowDifference(RealMatrix m, RealVector v) {
        RealMatrix out = m.createMatrix(m.getRowDimension(), m.getColumnDimension());
        for (int i = 0; i < m.getRowDimension(); i++) {
            out.setRowVector(i, m.getRowVector(i).subtract(v));
        }
        return out;
    }

    /**
     * Subtract a vector from each col of a matrix
     *
     * @param m the matrix to operate on
     * @param v the vector to subtract from each col of m
     * @return RealMatrix m-v
     *
     */
    public static RealMatrix colDifference(RealMatrix m, RealVector v) {
        RealMatrix out = m.createMatrix(m.getRowDimension(), m.getColumnDimension());
        for (int i = 0; i < m.getColumnDimension(); i++) {
            out.setColumnVector(i, m.getColumnVector(i).subtract(v));
        }
        return out;
    }

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

    /**
     * Create a one row Matrix from a vector of size n
     *
     * @param v the vector to turn into a one-row matrix
     * @return RealMatrix of shape 1,n
     *
     */
    public static RealMatrix createRowMatrix(RealVector v) {
        RealMatrix out = MatrixUtils.createRealMatrix(1, v.getDimension());
        out.setRowVector(0, v);
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
                    String.format(shapeString, "A", aRows, aCols) +
                    String.format(shapeString, "B", bRows, bCols));
        }

        RealMatrix out = MatrixUtils.createRealMatrix(aRows, bCols);
        for (int row = 0; row < aRows; row++) {
            for (int col = 0; col < bCols; col++) {
                out.setEntry(row, col, a.getRowVector(row).dotProduct(b.getColumnVector(col)));
            }
        }
        return out;
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
}
