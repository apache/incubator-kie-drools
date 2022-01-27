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
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatrixUtilsExtensionsTest {
    // === Make some matrices to use in tests ===
    double[][] mat3X5 = {
            { 1., 10., 3., -4., 0. },
            { 10., 5., -3., 3.7, 1. },
            { 14., -6.6, 7., 14., 3. },
    };

    double[][] mat3X5get013 = {
            { 1., 10., -4. },
            { 10., 5., 3.7, },
            { 14., -6.6, 14. },
    };
    double[][] mat3X5get03130 = {
            { 1., -4, 10., -4., 1 },
            { 10., 3.7, 5., 3.7, 10 },
            { 14., 14., -6.6, 14., 14. },
    };

    double[][] matSquareNonSingular = {
            { 1., 2., 3. },
            { 10., 5., -3. },
            { 14., -6.6, 7. },
    };
    double[][] matSNSInv = {
            { -0.02464332, 0.05479896, 0.03404669 },
            { 0.18158236, 0.05674449, -0.05350195 },
            { 0.22049287, -0.05609598, 0.02431907 }
    };

    double[][] matSquareSingular = {
            { 1., 2., 3. },
            { 4., 5., 6. },
            { 7., 8., 9. },
    };

    double[][] matSquareSingularPow = {
            { 1., 4., 9. },
            { 16., 25., 36. },
            { 49., 64., 81. },
    };

    double[] mssSumRow = { 12., 15., 18. };

    RealVector v = MatrixUtils.createRealVector(new double[] { 1, 2, 3 });
    RealMatrix mssMatrix = MatrixUtils.createRealMatrix(matSquareSingular);
    RealMatrix rowDiffResult = MatrixUtils.createRealMatrix(new double[][] { { 0, 0, 0 }, { 3, 3, 3 }, { 6, 6, 6 } });
    RealMatrix colDiffResult = MatrixUtils.createRealMatrix(new double[][] { { 0, 1, 2 }, { 2, 3, 4 }, { 4, 5, 6 } });
    RealMatrix swapResult = MatrixUtils.createRealMatrix(new double[][] { { 7, 8, 9 }, { 4, 5, 6 }, { 1, 2, 3 } });
    RealVector swapResultV = MatrixUtils.createRealVector(new double[] { 3, 2, 1 });

    RealMatrix dotInput1 = MatrixUtils.createRealMatrix(new double[][] { { 0, 1, 2 }, { 3, 4, 5 } });
    RealMatrix dotInput2 = MatrixUtils.createRealMatrix(new double[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } });
    RealMatrix dotResult = MatrixUtils.createRealMatrix(new double[][] { { 15, 18, 21 }, { 42, 54, 66 } });
    RealVector vMix = MatrixUtils.createRealVector(new double[] { -3, -2, -1, 1, 2, 3 });
    RealVector allNeg = MatrixUtils.createRealVector(new double[] { -3, -2, -1 });
    RealVector varInput = MatrixUtils.createRealVector(new double[] { 0, 4, 16, 2, -128, -4 });

    // === Matrix creation tests =======================================================================================
    // test creation of matrix from single PredictionInput
    @Test
    void testPICreation() {
        // use the mat 3x5 to grab one row for prediction input
        List<Feature> fs = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            fs.add(FeatureFactory.newNumericalFeature("f", mat3X5[0][j]));
        }
        PredictionInput pi = new PredictionInput(fs);
        RealVector converted = MatrixUtilsExtensions.vectorFromPredictionInput(pi);
        assertArrayEquals(mat3X5[0], converted.toArray());
    }

    // test creation of matrix from list of PredictionInputs
    @Test
    void testPIListCreation() {
        // use the mat 3x5 as our list of prediction inputs
        List<PredictionInput> ps = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Feature> fs = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                fs.add(FeatureFactory.newNumericalFeature("f", mat3X5[i][j]));
            }
            ps.add(new PredictionInput(fs));
        }

        RealMatrix converted = MatrixUtilsExtensions.matrixFromPredictionInput(ps);
        assertArrayEquals(mat3X5, converted.getData());
    }

    // test creation of matrix from single PredictionOutput
    @Test
    void testPOCreation() {
        // use the mat 3x5 as our list of prediction inputs
        List<Output> os = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            Value v = new Value(mat3X5[0][j]);
            os.add(new Output("o", Type.NUMBER, v, 0.0));
        }
        PredictionOutput po = new PredictionOutput(os);
        RealVector converted = MatrixUtilsExtensions.vectorFromPredictionOutput(po);
        assertArrayEquals(mat3X5[0], converted.toArray());
    }

    // test creation of matrix from list of PredictionOutputs
    @Test
    void testPOListCreation() {
        // use the mat 3x5 as our list of prediction outputs
        List<PredictionOutput> ps = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Output> os = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                Value v = new Value(mat3X5[i][j]);
                os.add(new Output("o", Type.NUMBER, v, 0.0));
            }
            ps.add(new PredictionOutput(os));
        }

        RealMatrix converted = MatrixUtilsExtensions.matrixFromPredictionOutput(ps);
        assertArrayEquals(mat3X5, converted.getData());
    }

    // === getCols Tests ===============================================================================================
    // test multi column indexing
    @Test
    void testGetCols() {
        RealMatrix output = MatrixUtilsExtensions.getCols(MatrixUtils.createRealMatrix(mat3X5), List.of(0, 1, 3));
        for (int i = 0; i < mat3X5get013.length; i++) {
            assertArrayEquals(mat3X5get013[i], output.getRow(i));
        }
    }

    // test extracting multiple dup columns by indexing
    @Test
    void testGetDupCols() {
        RealMatrix output = MatrixUtilsExtensions.getCols(MatrixUtils.createRealMatrix(mat3X5), List.of(0, 3, 1, 3, 0));
        for (int i = 0; i < mat3X5get03130.length; i++) {
            assertArrayEquals(mat3X5get03130[i], output.getRow(i));
        }
    }

    // test that extracting columns that are outside bounds throws error
    @Test
    void testGetColsTooBig() {
        List<Integer> testIdxs = List.of(0, 6);
        assertThrows(IllegalArgumentException.class, () -> MatrixUtilsExtensions.getCols(
                MatrixUtils.createRealMatrix(mat3X5), testIdxs));
    }

    // test that extracting negative columns throws error
    @Test
    void testGetNegCols() {
        List<Integer> testIdxs = List.of(0, -6);
        assertThrows(IllegalArgumentException.class, () -> MatrixUtilsExtensions.getCols(
                MatrixUtils.createRealMatrix(mat3X5), testIdxs));
    }

    // test that extracting no columns throws error
    @Test
    void testGetNoCols() {
        List<Integer> testIdxs = List.of();
        assertThrows(IllegalArgumentException.class, () -> MatrixUtilsExtensions.getCols(
                MatrixUtils.createRealMatrix(mat3X5), testIdxs));
    }

    // === Matrix Sum Tests ============================================================================================
    // test that summing all the rows of matrix returns expected result
    @Test
    void testIntraSumRow() {
        RealVector sum = MatrixUtilsExtensions.rowSum(mssMatrix);
        assertArrayEquals(mssSumRow, sum.toArray(), 1e-6);
    }

    @Test
    void rowSum() {
        assertArrayEquals(new double[] { 12, 15, 18 }, MatrixUtilsExtensions.rowSum(mssMatrix).toArray());
    }

    @Test
    void rowSquareSum() {
        assertArrayEquals(new double[] { 66, 93, 126 }, MatrixUtilsExtensions.rowSquareSum(mssMatrix).toArray());
    }

    @Test
    void rowDifference() {
        assertEquals(rowDiffResult, MatrixUtilsExtensions.vectorDifference(mssMatrix, v,
                MatrixUtilsExtensions.Axis.ROW));
    }

    @Test
    void colDifference() {
        assertEquals(colDiffResult, MatrixUtilsExtensions.vectorDifference(mssMatrix, v,
                MatrixUtilsExtensions.Axis.COLUMN));
    }

    @Test
    void matMap() {
        assertArrayEquals(matSquareSingularPow, MatrixUtilsExtensions.map(mssMatrix, x -> x * x).getData());
    }

    // Matrix Product tests ============================================================================================
    @Test
    void matrixDot() {
        assertEquals(dotResult, MatrixUtilsExtensions.matrixDot(dotInput1, dotInput2));
    }

    // === Matrix Inversion tests ======================================================================================
    // test inverting a non singular square matrix
    @Test
    void testInvertNormal() {
        RealMatrix inv = MatrixUtilsExtensions.safeInvert(MatrixUtils.createRealMatrix(matSquareNonSingular));
        for (int i = 0; i < inv.getRowDimension(); i++) {
            assertArrayEquals(matSNSInv[i], inv.getRow(i), 1e-4);
        }
    }

    @Test
    // test inverting a singular square matrix
    // this should return a psuedoinverse, which should have the property A = A(A+)A
    void testInvertSingular() {
        RealMatrix orig = mssMatrix;
        RealMatrix inv = MatrixUtilsExtensions.safeInvert(orig);
        RealMatrix invProperty = orig.multiply(inv).multiply(orig);
        for (int i = 0; i < inv.getRowDimension(); i++) {
            assertArrayEquals(orig.getRow(i), invProperty.getRow(i), 1e-4);
        }
    }

    // RealVector statistics tests =====================================================================================
    @Test
    void testMinPos() {
        assertEquals(1, MatrixUtilsExtensions.minPos(vMix), 1e-4);
    }

    @Test
    void testMinPosNoNeg() {
        assertEquals(Double.MAX_VALUE, MatrixUtilsExtensions.minPos(allNeg), 1e-4);
    }

    @Test
    void testVar() {
        assertEquals(2443.22222222, MatrixUtilsExtensions.variance(varInput), 1e-4);
    }

    // === Swap Tests ==================================================================================================
    @Test
    void testSwapRealMatrix() {
        RealMatrix mCopy = mssMatrix.copy();
        MatrixUtilsExtensions.swap(mCopy, 0, 2);
        assertEquals(swapResult, mCopy);
    }

    @Test
    void testSwapRealVector() {
        RealVector vCopy = v.copy();
        MatrixUtilsExtensions.swap(vCopy, 0, 2);
        assertEquals(swapResultV, vCopy);
    }

    @Test
    void testSwapIntArr() {
        int[] arr = new int[] { 1, 2, 3 };
        MatrixUtilsExtensions.swap(arr, 0, 2);
        assertArrayEquals(new int[] { 3, 2, 1 }, arr);
    }

}
