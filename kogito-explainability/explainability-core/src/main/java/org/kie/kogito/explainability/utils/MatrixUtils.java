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

public class MatrixUtils {

    private MatrixUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param x: the double array to return the matrix shape of
     * @return shape, an int array of length 2, where shape[0] = number of rows and shape[1] is number of columns
     */
    static int[] getShape(double[][] x) {
        int rows = x.length;
        int cols = x[0].length;
        return new int[] { rows, cols };
    }

    static double[] getCol(double[][] x, int i) throws IllegalArgumentException {
        int cols = MatrixUtils.getShape(x)[1];
        if (cols <= i) {
            throw new IllegalArgumentException(
                    String.format("Column index %d too large, matrix only has %d column(s)", i, cols));
        }
        return IntStream.range(0, x.length)
                .mapToDouble(rowIdx -> x[rowIdx][i])
                .toArray();
    }

    /**
     * Find the matrix transpose
     *
     * @param x double[][]; the matrix to be transposed
     * @return the transpose of x
     */
    static double[][] transpose(double[][] x) {
        int[] shape = MatrixUtils.getShape(x);

        double[][] transposed = new double[shape[1]][shape[0]];
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                transposed[j][i] = x[i][j];
            }
        }
        return transposed;
    }

    /**
     * Find the matrix product. Throws an error if the matrix shapes are misaligned
     *
     * @param a double[][]; the first matrix in the multiplication
     * @param b double[][]; the second matrix in the multiplication
     * @return the matrix product of a and b
     */
    static double[][] matrixMultiply(double[][] a, double[][] b) throws IllegalArgumentException {
        int[] aShape = MatrixUtils.getShape(a);
        int[] bShape = MatrixUtils.getShape(b);

        if (aShape[1] != bShape[0]) {
            throw new IllegalArgumentException("# columns of matrix A must match # rows of matrix B" +
                    String.format("Matrix A shape:  %d x %d, ", aShape[0], aShape[1]) +
                    String.format("Matrix B shape:  %d x %d,", bShape[0], bShape[1]));
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
     * Find the diagonal element at row i with the largest absolute value, where {@code pivotsUsed[i] == 0}
     * This is the pivot value for the Gauss-Jordan algorithm
     *
     * @param x a square double[][]; the matrix to find the diagonal element within
     * @param pivotsUsed a boolean[], marking whether a specific index has been used as a pivot yet or not
     * @return the index of the found pivot
     */
    private static int findPivot(double[][] x, boolean[] pivotsUsed) {
        double maxAbs = 0;
        int pivot = 0;
        int size = MatrixUtils.getShape(x)[0];
        for (int diagIdx = 0; diagIdx < size; diagIdx++) {
            double abs = Math.abs(x[diagIdx][diagIdx]);
            if (abs > maxAbs && !pivotsUsed[diagIdx]) {
                pivot = diagIdx;
                maxAbs = abs;
            }
        }
        return pivot;
    }

    /**
     * Inverts the square, non-singular matrix X
     *
     * @param x a square, non-singular double[][] X
     * @return the inverted matrix
     *         <p>
     *         Dr. Debabrata DasGupta's description of the algorithm is here:
     *         https://www.researchgate.net/publication/271296470_In-Place_Matrix_Inversion_by_Modified_Gauss-Jordan_Algorithm
     */
    public static double[][] invertSquareMatrix(double[][] x, double zeroThreshold) {
        int size = MatrixUtils.getShape(x)[0];
        double[][] copy = new double[size][size];
        for (int i = 0; i < size; i++) {
            copy[i] = Arrays.copyOf(x[i], size);
        }

        // initialize array to track which pivots have been used
        boolean[] pivotsUsed = new boolean[size];
        Arrays.fill(pivotsUsed, false);

        // perform both operations until each row has been used as pivot
        // once we've done all iterations, X will be inverted in place
        for (int iterations = 0; iterations < size; iterations++) {
            // OPERATION 1
            // find the pivot
            // the pivot idx is the idx of the diagonal element with largest absolute value
            // that hasn't already been used as a pivot
            int pivot = MatrixUtils.findPivot(copy, pivotsUsed);
            double pivotVal = copy[pivot][pivot];

            // check if pivotVal is 0, allowing for some floating point error
            if (Math.abs(pivotVal) < zeroThreshold) {
                throw new ArithmeticException("Matrix is singular and cannot be inverted");
            }

            //virtualize the pivot
            copy[pivot][pivot] = 1.;

            //mark the pivot used
            pivotsUsed[pivot] = true;

            // normalize the pivot row
            for (int i = 0; i < size; i++) {
                copy[pivot][i] /= pivotVal;
            }

            // OPERATION 2
            // reduce each non-pivot column by X[p][i] * X[i][pivot]
            for (int i = 0; i < size; i++) {
                if (i != pivot) {
                    double rowValueAtPivot = copy[i][pivot];
                    copy[i][pivot] = 0.;
                    for (int j = 0; j < size; j++) {
                        copy[i][j] -= copy[pivot][j] * rowValueAtPivot;
                    }
                }
            }
        }
        return copy;
    }

    /**
     * Attempt to invert the given matrix.
     * If the matrix is singular, jitter the values slightly to break singularity
     * 
     * @param x a square double[][]; the matrix to be inverted
     * @param numRetries the number of times to attempt jittering before giving up
     * @return double[][], the inverted matrix
     *
     */
    public static double[][] jitterInvert(double[][] x, int numRetries, double zeroThreshold, Random random) {
        double[][] xInv;
        for (int jitterTries = 0; jitterTries < numRetries; jitterTries++) {
            try {
                xInv = MatrixUtils.invertSquareMatrix(x, zeroThreshold);
                return xInv;
            } catch (ArithmeticException e) {
                // if the inversion is unsuccessful, we can try slightly jittering the matrix.
                // this will reduce the accuracy of the inversion marginally, but ensures that we get results
                MatrixUtils.jitterMatrix(x, 1e-8, random);
            }
        }

        // if jittering didn't work, throw an error
        throw new ArithmeticException("Matrix is singular and could not be inverted via jittering");
    }

    /**
     * Jitters the values of a matrix IN-PLACE by a random number in range (0, delta)
     * 
     * @param x the matrix to be jittered
     * @param delta the scale of the jittering
     *
     */
    private static void jitterMatrix(double[][] x, double delta, Random random) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                x[i][j] += delta * random.nextDouble();
            }
        }
    }
}
