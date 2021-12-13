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

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LassoLarsICTest {
    // check equivalent output as https://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LassoLarsIC.html

    // ================ TEST 1 =========================================================================================
    // Check AIC and BIC for the first set of input and observations
    // test for a well-formed system of equations
    RealMatrix X = MatrixUtils.createRealMatrix(new double[][] { { 0.92966881, 0.17435502, 0.86274567, 0.02096693, 0.61729408, 0.27663037, 0.07324771, 0.86299396, 0.20387837, 0.2678897, },
            { 0.46124402, 0.21212798, 0.54547663, 0.85310364, 0.23584478, 0.89939373, 0.90052444, 0.48947526, 0.97695481, 0.31682039 },
            { 0.66084177, 0.54153099, 0.76965712, 0.08213559, 0.9262654, 0.68282777, 0.500637, 0.76781516, 0.14606141, 0.53844816 },
            { 0.44602165, 0.72739983, 0.66221962, 0.20234917, 0.80836334, 0.37038587, 0.67539221, 0.77099063, 0.92992129, 0.56789747 },
            { 0.67568569, 0.37884472, 0.18745406, 0.04757457, 0.09661771, 0.50471931, 0.35367252, 0.75794935, 0.6424804, 0.55250168 },
            { 0.19722479, 0.32117211, 0.70339706, 0.53906674, 0.76903061, 0.32923893, 0.50025901, 0.20776133, 0.1088789, 0.79303772 },
            { 0.31128645, 0.05883037, 0.64210569, 0.88726458, 0.19756748, 0.02448866, 0.2172705, 0.27894779, 0.55028519, 0.70483099 },
            { 0.47339132, 0.14034869, 0.0816702, 0.06699631, 0.06823621, 0.03639515, 0.07545303, 0.1208853, 0.72845905, 0.74802801 },
            { 0.99628077, 0.83760513, 0.63542635, 0.07380346, 0.79007766, 0.55288944, 0.44548098, 0.4055312, 0.70605767, 0.83153303 },
            { 0.47161946, 0.97424448, 0.91217761, 0.6264732, 0.43486423, 0.39281956, 0.66218207, 0.01484187, 0.75595905, 0.04462323 },
    });
    RealVector y = MatrixUtils.createRealVector(new double[] { 6.38923853, -2.16396995, 7.37162403, 1.79236199, 4.21888433, 0.41875855, -3.69136276, -0.50760573, 4.89875242, -4.03316984 });
    RealVector cCorrect = MatrixUtils.createRealVector(new double[] { 0., 0., 0., -4.7191308, 0., 0., 0., 2.30445186, 0., 0. });

    @Test
    void testLassoLarsAIC1() {
        LassoLarsICResults llicr = LassoLarsIC.fit(X, y, LassoLarsIC.Criterion.AIC, 500);
        assertArrayEquals(cCorrect.toArray(), llicr.getCoefs().toArray(), 1e-6);
        assertEquals(0.4449046388149383, llicr.getAlpha(), 1e-6);
        assertEquals(1.9958938453612995, llicr.getIntercept(), 1e-6);
    }

    @Test
    void testLassoLarsBIC1() {
        LassoLarsICResults llicr = LassoLarsIC.fit(X, y, LassoLarsIC.Criterion.BIC, 500);
        assertArrayEquals(cCorrect.toArray(), llicr.getCoefs().toArray(), 1e-6);
        assertEquals(0.4449046388149383, llicr.getAlpha(), 1e-6);
        assertEquals(1.9958938453612995, llicr.getIntercept(), 1e-6);
    }

    // ================ TEST 2 =========================================================================================
    // this tests the MIN_ALPHA_BREAK condition
    // Check AIC and BIC for the second set of input and observations
    RealMatrix X2 = MatrixUtils.createRealMatrix(new double[20][20]).scalarAdd(1.0);
    RealVector y2 = MatrixUtils.createRealVector(new double[20]).mapAdd(190.);
    RealVector cCorrect2 = MatrixUtils.createRealVector(new double[20]);

    @Test
    void testLassoLarsAIC2() {
        LassoLarsICResults llicr = LassoLarsIC.fit(X2, y2, LassoLarsIC.Criterion.AIC, 500);
        assertArrayEquals(cCorrect2.toArray(), llicr.getCoefs().toArray(), 1e-6);
        assertEquals(0, llicr.getAlpha(), 1e-6);
        assertEquals(190, llicr.getIntercept(), 1e-6);
    }

    @Test
    void testLassoLarsBIC2() {
        LassoLarsICResults llicr = LassoLarsIC.fit(X2, y2, LassoLarsIC.Criterion.BIC, 500);
        assertArrayEquals(cCorrect2.toArray(), llicr.getCoefs().toArray(), 1e-6);
        assertEquals(0, llicr.getAlpha(), 1e-6);
        assertEquals(190, llicr.getIntercept(), 1e-6);
    }

    // ================ TEST 3 ========================================================================================
    // Check AIC and BIC for the set of input and observations
    // test condition when AIC and BIC differ
    RealMatrix X3 = MatrixUtils.createRealMatrix(new double[][] { { 0.11542803, 0.20223286, 0.50635094, 0.2981027, 0.62258941 },
            { 0.82363115, 0.72488016, 0.47460919, 0.90378779, 0.37764358 },
            { 0.95356652, 0.56921523, 0.30947282, 0.19964256, 0.77501456 },
            { 0.76365121, 0.93890888, 0.32035303, 0.37175223, 0.31471032 },
            { 0.22220301, 0.11807254, 0.83201371, 0.61226084, 0.06749518 },
            { 0.81896382, 0.97537429, 0.6196591, 0.05742652, 0.06183891 },
            { 0.84315897, 0.27244913, 0.23105381, 0.4410028, 0.59067501 },
            { 0.46617363, 0.20838618, 0.29574261, 0.07406868, 0.80432574 },
            { 0.5761739, 0.76014582, 0.65613161, 0.94977952, 0.60631693 },
            { 0.89793806, 0.1201857, 0.81394908, 0.41184656, 0.25093766 },
            { 0.06979188, 0.18489251, 0.62269406, 0.14490719, 0.82650325 },
            { 0.46455973, 0.14397062, 0.63508708, 0.21019864, 0.13210203 },
            { 0.04395622, 0.02443612, 0.58377207, 0.81030415, 0.07176587 },
            { 0.99211442, 0.1420474, 0.89257316, 0.87574911, 0.85681771 },
            { 0.60074807, 0.02153421, 0.48581558, 0.27725285, 0.18374034 },
    });
    RealVector y3 = MatrixUtils
            .createRealVector(
                    new double[] { 1.99574655, 2.48255887, 2.6835637, 2.17903056, 1.76771216, 2.40659925, 2.70073659, 1.98624935, 3.07837355, 2.13519951, 1.23270283, 2.00338918, 1.11061156,
                            2.69312564, 1.63668346 });
    RealVector cCorrect3AIC = MatrixUtils.createRealVector(new double[] { 0.76831491, 0.18542135, 0., 0., 0. });
    RealVector cCorrect3BIC = MatrixUtils.createRealVector(new double[] { 0.57715437, 0., 0., 0., 0. });

    @Test
    void testLassoLarsAIC3() {
        LassoLarsICResults llicr = LassoLarsIC.fit(X3, y3, LassoLarsIC.Criterion.AIC, 500);
        assertArrayEquals(cCorrect3AIC.toArray(), llicr.getCoefs().toArray(), 1e-6);
        assertEquals(0.043165506829777246, llicr.getAlpha(), 1e-6);
        assertEquals(1.6294835757269654, llicr.getIntercept(), 1e-6);
    }

    @Test
    void testLassoLarsBIC3() {
        LassoLarsICResults llicr = LassoLarsIC.fit(X3, y3, LassoLarsIC.Criterion.BIC, 500);
        assertArrayEquals(cCorrect3BIC.toArray(), llicr.getCoefs().toArray(), 1e-6);
        assertEquals(0.07147262552303063, llicr.getAlpha(), 1e-6);
        assertEquals(1.8065806212142568, llicr.getIntercept(), 1e-6);
    }
}
