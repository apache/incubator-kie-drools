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

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatrixUtilsTest {
    // === Make some matrices to use in tests ===
    private static final double[][] matOneElem = {
            { 5. },
    };
    private static final double[][] matRowVector = {
            { 5., 6., 7, },
    };
    private static final double[][] matColVector = {
            { 5. },
            { 6. },
            { 7, },
    };

    private static final double[][] vectorProdRowCol = { { 110. } };
    private static final double[][] vectorProdColRow = {
            { 25., 30., 35. },
            { 30., 36., 42. },
            { 35., 42., 49. }
    };

    private static final double[][] mat4X3 = {
            { 1., 2., 3. },
            { 10., 5., -3. },
            { 14., -6.6, 7. },
            { 0., 5., -3. }
    };
    private static final double[][] mat3X4 = {
            { 1., 10., 14., 0. },
            { 2, 5., -6.6, 5. },
            { 3., -3, 7., -3 }
    };
    private static final double[][] mat3X5 = {
            { 1., 10., 3., -4., 0. },
            { 10., 5., -3., 3.7, 1. },
            { 14., -6.6, 7., 14., 3. },
    };

    private static final double[][] mat43X35Product = {
            { 63., .2, 18., 45.4, 11. },
            { 18., 144.8, -6., -63.5, -4. },
            { 46., 60.8, 110.8, 17.58, 14.4 },
            { 8., 44.8, -36., -23.5, -4. }
    };

    private static final double[][] matSquareNonSingular = {
            { 1., 2., 3. },
            { 10., 5., -3. },
            { 14., -6.6, 7. },
    };
    private static final double[][] matSNSInv = {
            { -0.02464332, 0.05479896, 0.03404669 },
            { 0.18158236, 0.05674449, -0.05350195 },
            { 0.22049287, -0.05609598, 0.02431907 }
    };

    private static final double[][] matSquareSingular = {
            { 1., 2., 3. },
            { 4., 5., 6. },
            { 7., 8., 9. },
    };
    private static final double[][] identity = {
            { 1., 0., 0. },
            { 0., 1., 0. },
            { 0., 0., 1. },
    };

    // === Shape Tests ===
    @Test
    void testShape() {
        int[] shape = MatrixUtils.getShape(mat3X5);
        assertArrayEquals(new int[] { 3, 5 }, shape);
    }

    // === getColumn Tests ===
    @Test
    void testGetCol() {
        double[] col = MatrixUtils.getCol(mat3X4, 1);
        assertArrayEquals(col, new double[] { 10., 5., -3. });
    }

    @Test
    void testGetColTooBig() {
        assertThrows(IllegalArgumentException.class, () -> MatrixUtils.getCol(mat3X4, 10));
    }

    // === Transpose Tests ===
    @Test
    void testOneElemTranspose() {
        double[][] matOneElemTranspose = MatrixUtils.transpose(matOneElem);
        for (int i = 0; i < matOneElemTranspose.length; i++) {
            assertArrayEquals(matOneElemTranspose[i], matOneElem[i]);
        }
    }

    @Test
    void testVectorTranspose() {
        double[][] matRowVectorTranspose = MatrixUtils.transpose(matRowVector);
        for (int i = 0; i < matRowVectorTranspose.length; i++) {
            assertArrayEquals(matRowVectorTranspose[i], matColVector[i]);
        }
    }

    @Test
    void testMatrixTranspose() {
        double[][] mat3X4Transpose = MatrixUtils.transpose(mat3X4);
        for (int i = 0; i < mat3X4Transpose.length; i++) {
            assertArrayEquals(mat3X4Transpose[i], mat4X3[i]);
        }
    }

    // === Matrix Multiplication Tests ===
    @Test
    void testMatMulNormal() {
        double[][] prod = MatrixUtils.matrixMultiply(mat4X3, mat3X5);
        for (int i = 0; i < prod.length; i++) {
            assertArrayEquals(mat43X35Product[i], prod[i], 1e-6);
        }
    }

    @Test
    void testMatMulWrongShape() {
        assertThrows(IllegalArgumentException.class,
                () -> MatrixUtils.matrixMultiply(mat3X4, mat3X5));

    }

    @Test
    void testVectorRowColMultiply() {
        double[][] prod = MatrixUtils.matrixMultiply(matRowVector, matColVector);
        for (int i = 0; i < prod.length; i++) {
            assertArrayEquals(vectorProdRowCol[i], prod[i], 1e-6);
        }
    }

    @Test
    void testVectorColRowMultiply() {
        double[][] prod = MatrixUtils.matrixMultiply(matColVector, matRowVector);
        for (int i = 0; i < prod.length; i++) {
            assertArrayEquals(vectorProdColRow[i], prod[i], 1e-6);
        }
    }

    // === Matrix Inversion tests ===
    @Test
    void testInvertNormal() {
        double[][] inv = MatrixUtils.invertSquareMatrix(matSquareNonSingular, 1e-9);
        for (int i = 0; i < inv.length; i++) {
            assertArrayEquals(matSNSInv[i], inv[i], 1e-6);
        }
    }

    @Test
    void testInvertSingular() {
        assertThrows(ArithmeticException.class, () -> MatrixUtils.invertSquareMatrix(matSquareSingular, 1e-9));
    }

    // === Jitter Invert Tests ===
    @Test
    void testJitterInvert() {
        // since there's some randomness in jitter invert, let's make sure it's stable
        for (int run = 0; run < 100; run++) {
            Random random = new Random();
            random.setSeed(run);
            double[][] inv = MatrixUtils.jitterInvert(matSquareSingular, 10, 1e-9, random);

            // since the output of jitterInvert is non-deterministic for singular matrices, check to make sure
            // key properties of the inverse matrix hold true; namely M*M_inv = Identity
            double[][] prod = MatrixUtils.matrixMultiply(matSquareSingular, inv);
            for (int i = 0; i < prod.length; i++) {
                assertArrayEquals(prod[i], identity[i], 1e-6);
            }
        }
    }
}
