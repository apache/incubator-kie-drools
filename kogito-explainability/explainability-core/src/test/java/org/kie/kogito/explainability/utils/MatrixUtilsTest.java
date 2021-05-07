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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.*;

import static org.junit.jupiter.api.Assertions.*;

class MatrixUtilsTest {
    // === Make some matrices to use in tests ===
    double[][] matOneElem = {
            { 5. },
    };
    double[] vector = { 5., 6., 7 };
    double[][] matRowVector = {
            { 5., 6., 7, },
    };
    double[][] matColVector = {
            { 5. },
            { 6. },
            { 7, },
    };

    double[][] vectorProdRowCol = { { 110. } };
    double[][] vectorProdColRow = {
            { 25., 30., 35. },
            { 30., 36., 42. },
            { 35., 42., 49. }
    };

    double[][] mat4X3 = {
            { 1., 2., 3. },
            { 10., 5., -3. },
            { 14., -6.6, 7. },
            { 0., 5., -3. }
    };
    double[][] mat3X4 = {
            { 1., 10., 14., 0. },
            { 2, 5., -6.6, 5. },
            { 3., -3, 7., -3 }
    };

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

    double[][] mat43X35Product = {
            { 63., .2, 18., 45.4, 11. },
            { 18., 144.8, -6., -63.5, -4. },
            { 46., 60.8, 110.8, 17.58, 14.4 },
            { 8., 44.8, -36., -23.5, -4. }
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
    double[][] identity = {
            { 1., 0., 0. },
            { 0., 1., 0. },
            { 0., 0., 1. },
    };

    double[][] matIdentityPlusVector = {
            { 6., 6., 7. },
            { 5., 7., 7. },
            { 5., 6., 8. },
    };

    double[][] mssPlusIdentity = {
            { 2., 2., 3. },
            { 4., 6., 6. },
            { 7., 8., 10. },
    };
    double[][] mssMinusIdentity = {
            { 0., 2., 3. },
            { 4., 4., 6. },
            { 7., 8., 8. },
    };
    double[] mssSumRow = { 12., 15., 18. };
    double[] mssSumCol = { 6., 15., 24. };
    Random rn = new Random();

    // === Matrix creation tests ===
    // test creation of matrix from single PredictionInput
    @Test
    void testPICreation() {
        // use the mat 3x5 to grab one row for prediction input
        List<Feature> fs = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            fs.add(FeatureFactory.newNumericalFeature("f", mat3X5[0][j]));
        }
        PredictionInput pi = new PredictionInput(fs);
        double[][] converted = MatrixUtils.matrixFromPredictionInput(pi);
        assertArrayEquals(mat3X5[0], converted[0]);
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

        double[][] converted = MatrixUtils.matrixFromPredictionInput(ps);
        assertArrayEquals(mat3X5, converted);
    }

    // test creation of matrix from single PredictionOutput
    @Test
    void testPOCreation() {
        // use the mat 3x5 as our list of prediction inputs
        List<Output> os = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            Value v = new Value(mat3X5[0][j]);
            os.add(new Output("o", Type.NUMBER, v, 0.0));
            ;
        }
        PredictionOutput po = new PredictionOutput(os);
        double[][] converted = MatrixUtils.matrixFromPredictionOutput(po);
        assertArrayEquals(mat3X5[0], converted[0]);
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

        double[][] converted = MatrixUtils.matrixFromPredictionOutput(ps);
        assertArrayEquals(mat3X5, converted);
    }

    // test creation of row matrix from 1-D array
    @Test
    void testRowVectorCreation() {
        double[][] converted = MatrixUtils.rowVector(vector);
        for (int i = 0; i < converted.length; i++) {
            assertEquals(converted[0][i], vector[i]);
        }
    }

    // test creation of column matrix from 1-D array
    @Test
    void testColVectorCreation() {
        double[][] converted = MatrixUtils.columnVector(vector);
        for (int i = 0; i < converted.length; i++) {
            assertEquals(converted[i][0], vector[i]);
        }
    }

    // === Shape Tests ===
    @Test
    void testShape() {
        int[] shape = MatrixUtils.getShape(mat3X5);
        assertArrayEquals(new int[] { 3, 5 }, shape);
    }

    // === getColumn Tests ===

    // test if indexing by too big of column throws an error
    @Test
    void testGetColTooBig() {
        assertThrows(IllegalArgumentException.class, () -> MatrixUtils.getCol(mat3X4, 10));
    }

    // test if indexing a negative column throws an error
    @Test
    void testGetNegCol() {
        assertThrows(IllegalArgumentException.class, () -> MatrixUtils.getCol(mat3X4, -10));
    }

    // test single column indexing
    @Test
    void testGetCol() {
        double[] col = MatrixUtils.getCol(mat3X4, 1);
        assertArrayEquals(col, new double[] { 10., 5., -3. });
    }

    // test multi column indexing
    @Test
    void testGetCols() {
        double[][] output = MatrixUtils.getCols(mat3X5, List.of(0, 1, 3));
        for (int i = 0; i < mat3X5get013.length; i++) {
            assertArrayEquals(mat3X5get013[i], output[i]);
        }
    }

    // test extracting multiple dup columns by indexing
    @Test
    void testGetDupCols() {
        double[][] output = MatrixUtils.getCols(mat3X5, List.of(0, 3, 1, 3, 0));
        for (int i = 0; i < mat3X5get03130.length; i++) {
            assertArrayEquals(mat3X5get03130[i], output[i]);
        }
    }

    // test that extracting columns that are outside bounds throws error
    @Test
    void testGetColsTooBig() {
        List<Integer> testIdxs = List.of(0, 6);
        assertThrows(IllegalArgumentException.class, () -> MatrixUtils.getCols(mat3X5, testIdxs));
    }

    // test that extracting negative columns throws error
    @Test
    void testGetNegCols() {
        List<Integer> testIdxs = List.of(0, -6);
        assertThrows(IllegalArgumentException.class, () -> MatrixUtils.getCols(mat3X5, testIdxs));
    }

    // test that extracting no columns throws error
    @Test
    void testGetNoCols() {
        List<Integer> testIdxs = List.of();
        assertThrows(IllegalArgumentException.class, () -> MatrixUtils.getCols(mat3X5, testIdxs));
    }

    // === Transpose Tests ===
    // test transpose a 1x1 mat
    @Test
    void testOneElemTranspose() {
        double[][] matOneElemTranspose = MatrixUtils.transpose(matOneElem);
        for (int i = 0; i < matOneElemTranspose.length; i++) {
            assertArrayEquals(matOneElemTranspose[i], matOneElem[i]);
        }
    }

    // test transpose a row vector into column
    @Test
    void testVectorTranspose() {
        double[][] matRowVectorTranspose = MatrixUtils.transpose(matRowVector);
        for (int i = 0; i < matRowVectorTranspose.length; i++) {
            assertArrayEquals(matRowVectorTranspose[i], matColVector[i]);
        }
    }

    // test transpose a 3x4 matrix
    @Test
    void testMatrixTranspose() {
        double[][] mat3X4Transpose = MatrixUtils.transpose(mat3X4);
        for (int i = 0; i < mat3X4Transpose.length; i++) {
            assertArrayEquals(mat3X4Transpose[i], mat4X3[i]);
        }
    }

    // === Matrix Sum Tests ===
    // test that summing all the rows of matrix returns expected result
    @Test
    void testIntraSumRow() {
        double[] sum = MatrixUtils.sum(matSquareSingular, MatrixUtils.Axis.ROW);
        assertArrayEquals(mssSumRow, sum, 1e-6);
    }

    // test that summing all the cols of matrix returns expected result
    @Test
    void testIntraSumCol() {
        double[] sum = MatrixUtils.sum(matSquareSingular, MatrixUtils.Axis.COLUMN);
        assertArrayEquals(mssSumCol, sum, 1e-6);
    }

    // test that summing two matrices returns expected result
    @Test
    void testMatSum() {
        double[][] sum = MatrixUtils.matrixSum(matSquareSingular, identity);
        for (int i = 0; i < sum.length; i++) {
            assertArrayEquals(mssPlusIdentity[i], sum[i], 1e-6);
        }
    }

    // test that summing two matrices of incompatible sizes throws error
    @Test
    void testMatSumWrongSizes() {
        assertThrows(IllegalArgumentException.class,
                () -> MatrixUtils.matrixSum(matSquareSingular, mat4X3));
    }

    // test that difference of two matrices returns expected result
    @Test
    void testMatDiff() {
        double[][] diff = MatrixUtils.matrixDifference(matSquareSingular, identity);
        for (int i = 0; i < diff.length; i++) {
            assertArrayEquals(mssMinusIdentity[i], diff[i], 1e-6);
        }
    }

    // test adding a vector to each row of a matrix
    @Test
    void testMatRowSum() {
        double[][] sum = MatrixUtils.matrixRowSum(identity, vector);
        for (int i = 0; i < sum.length; i++) {
            assertArrayEquals(matIdentityPlusVector[i], sum[i], 1e-6);
        }
    }

    // test subtracting a vector to each row of a matrix
    @Test
    void testMatRowDiff() {
        double[][] diff = MatrixUtils.matrixRowDifference(matIdentityPlusVector, vector);
        for (int i = 0; i < diff.length; i++) {
            assertArrayEquals(identity[i], diff[i], 1e-6);
        }
    }

    // === Matrix Multiplication Tests ===
    // test multiplying a matrix by a scalar
    @Test
    void testMatMulScalar() {
        double[][] prod = MatrixUtils.matrixMultiply(mat4X3, 3.0);
        for (int i = 0; i < prod.length; i++) {
            for (int j = 0; j < prod[0].length; j++) {
                assertEquals(mat4X3[i][j] * 3.0, prod[i][j], 1e-6);
            }
        }
    }

    // test multiplying a matrix by zero
    @Test
    void testMatMulByZero() {
        double[][] prod = MatrixUtils.matrixMultiply(mat4X3, 0.0);
        for (int i = 0; i < prod.length; i++) {
            for (int j = 0; j < prod[0].length; j++) {
                assertEquals(mat4X3[i][j] * 0.0, prod[i][j], 1e-6);
            }
        }
    }

    // test multiplying a matrix by another matrix
    @Test
    void testMatMulNormal() {
        double[][] prod = MatrixUtils.matrixMultiply(mat4X3, mat3X5);
        for (int i = 0; i < prod.length; i++) {
            assertArrayEquals(mat43X35Product[i], prod[i], 1e-6);
        }
    }

    // test multiplying a matrix by another matrix
    @Test
    void testMatMulWrongShape() {
        assertThrows(IllegalArgumentException.class,
                () -> MatrixUtils.matrixMultiply(mat3X4, mat3X5));

    }

    // test multiplying a row matrix by a col matrix
    @Test
    void testVectorRowColMultiply() {
        double[][] prod = MatrixUtils.matrixMultiply(matRowVector, matColVector);
        for (int i = 0; i < prod.length; i++) {
            assertArrayEquals(vectorProdRowCol[i], prod[i], 1e-6);
        }
    }

    // test multiplying a col matrix by a row matrix
    @Test
    void testVectorColRowMultiply() {
        double[][] prod = MatrixUtils.matrixMultiply(matColVector, matRowVector);
        for (int i = 0; i < prod.length; i++) {
            assertArrayEquals(vectorProdColRow[i], prod[i], 1e-6);
        }
    }

    // === Matrix Inversion tests ===
    // test inverting a non singular square matrix
    @Test
    void testInvertNormal() {
        double[][] inv = MatrixUtils.jitterInvert(matSquareNonSingular, 1, 1e-9, rn);
        for (int i = 0; i < inv.length; i++) {
            assertArrayEquals(matSNSInv[i], inv[i], 1e-6);
        }
    }

    @Test
    // test inverting a singular square matrix
    void testInvertSingular() {
        assertThrows(ArithmeticException.class, () -> MatrixUtils.jitterInvert(matSquareSingular, 1, 1e-9, rn));
    }

    // === Jitter Invert Tests ===
    @Test
    void testJitterInvert() {
        // since there's some randomness in jitter invert, let's make sure it's stable
        for (int run = 0; run < 100; run++) {
            double[][] inv = MatrixUtils.jitterInvert(matSquareSingular, 10, 1e-9, rn);

            // since the output of jitterInvert is non-deterministic for singular matrices, check to make sure
            // key properties of the inverse matrix hold true; namely M*M_inv = Identity
            double[][] prod = MatrixUtils.matrixMultiply(matSquareSingular, inv);
            for (int i = 0; i < prod.length; i++) {
                assertArrayEquals(prod[i], identity[i], 1e-6);
            }
        }
    }

    @Test
    void testSecureJitterInvert() {
        // since there's some randomness in jitter invert, let's make sure it's stable
        for (int run = 0; run < 100; run++) {
            double[][] inv = MatrixUtils.jitterInvert(matSquareSingular, 10, 1e-9);

            // since the output of jitterInvert is non-deterministic for singular matrices, check to make sure
            // key properties of the inverse matrix hold true; namely M*M_inv = Identity
            double[][] prod = MatrixUtils.matrixMultiply(matSquareSingular, inv);
            for (int i = 0; i < prod.length; i++) {
                assertArrayEquals(prod[i], identity[i], 1e-6);
            }
        }
    }
}
