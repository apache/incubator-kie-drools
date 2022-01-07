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

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LarsPathTest {

    // Check lars_path equality for the first set of input and observations
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
    List<Integer> correctActives = List.of(7, 3, 0, 4, 8, 5, 9, 2, 1, 6);
    RealVector correctAlphas = MatrixUtils.createRealVector(new double[] { 1.56169836, 0.83963397, 0.68991047, 0.6664122, 0.29931992,
            0.14315316, 0.1200302, 0.00776273, 0.00389666, 0.00187156,
            0. });

    @ParameterizedTest
    @ValueSource(ints = { 2, 3, 4, 5, 6, 7, 8, 9, 10 })
    void testLars10(int maxIter) {
        LarsPathResults lpr = LarsPath.fit(X, y, maxIter, false);
        assertEquals(correctActives.subList(0, maxIter), lpr.getActive().subList(0, maxIter));
        assertArrayEquals(
                correctAlphas.getSubVector(0, maxIter).toArray(),
                lpr.getAlphas().getSubVector(0, maxIter).toArray(),
                1e-6);
    }

    // Check lars_path equality for the second set of input and observations
    RealMatrix X2 = MatrixUtils.createRealMatrix(new double[][] { { 0, 1, 0, 1, 0 },
            { 0, 1, 0, 1, 0 },
            { 0, 1, 1, 0, 1 },
            { 1, 1, 0, 0, 1 },
            { 0, 1, 1, 0, 1 },
            { 0, 0, 1, 0, 1 },
            { 0, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 1, 1, 0, 1, 0 },
            { 0, 0, 1, 0, 1 },
            { 1, 0, 0, 0, 0 },
            { 0, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 1 },
    });
    RealVector y2 = MatrixUtils
            .createRealVector(
                    new double[] { 1.09622926, 1.09622926, 1.07290478, 0.5044599, 1.07290478, 0.88775703, 1.79883855, 0., 0., 1.39511415, 0.88775703, 0.29888489, 1.0524775, 0., 1.98398629 });
    List<Integer> correctActives2 = List.of(1, 2, 3, 4, 0);
    RealVector correctAlphas2 = MatrixUtils.createRealVector(new double[] { 0.61828706, 0.54926307, 0.36443261, 0.183918, 0.04809642, 0. });

    @ParameterizedTest
    @ValueSource(ints = { 5 })
    void testLars5(int maxIter) {
        LarsPathResults lpr = LarsPath.fit(X2, y2, maxIter, false);
        assertEquals(correctActives2.subList(0, maxIter), lpr.getActive().subList(0, maxIter));
        assertArrayEquals(
                correctAlphas2.getSubVector(0, maxIter).toArray(),
                lpr.getAlphas().getSubVector(0, maxIter).toArray(),
                1e-6);
    }

    // Test the min_alpha_break condition ==============================================================================
    RealMatrix XMinAlpha = MatrixUtils.createRealMatrix(new double[][] { { 1., 2., 3., 4., 5. },
            { 2., 4., 6., 8., 10. },
            { 3., 6., 8., 12., 15. },
            { -1., -2., -3., -4., -5. },
    });
    RealVector yMinAlpha = MatrixUtils.createRealVector(new double[] { 55., 110., 162., -55. });
    List<Integer> correctActivesMinAlpha = List.of(4, 2);
    RealVector correctAlphasMinAlpha = MatrixUtils.createRealVector(new double[] { 1.02000000e+03, 6.81818182e-01, 4.16888746e-13 });

    @Test
    void testLarsMinAlpha() {
        LarsPathResults lpr = LarsPath.fit(XMinAlpha, yMinAlpha, 500, false);
        assertEquals(correctActivesMinAlpha, lpr.getActive());
        assertArrayEquals(correctAlphasMinAlpha.toArray(), lpr.getAlphas().toArray(), 1e-6);
    }

    // Test Degenerate Regressor conditions ============================================================================
    // both of these tests invoke the degenerate regressor condition in Python, but this implementation does not
    // seem to run into them, with no apparent consequence

    // this test will be a little clunky until the WLR library is moved to Apache Commons MatrixUtils (FAI-671)
    RealMatrix XDGR = MatrixUtils.createRealMatrix(new double[][] { { 0., 1., 2., 3., 4. },
            { 5., 6., 7., 8., 9. },
            { 10., 11., 12., 13., 14. },
            { 15., 16., 17., 18., 19. },
            { 20., 21., 22., 23., 24. },
            { 25., 26., 27., 28., 29. },
            { 30., 31., 32., 33., 34. },
            { 35., 36., 37., 38., 39. },
            { 40., 41., 42., 43., 44. },
            { 45., 46., 47., 48., 49. },
    });
    RealVector yDGR = MatrixUtils.createRealVector(new double[] { 0., 50., 100., 150., 200., 250., 300., 350., 400., 450. });
    RealVector dummyWeights = yDGR.map(x -> 1.);

    @Test
    void testLarsDGR() {
        LarsPathResults lpr = LarsPath.fit(XDGR, yDGR, 500, false);
        RealMatrix coefs = lpr.getCoefs();
        double mse = WeightedLinearRegression.getMSE(
                XDGR,
                yDGR,
                dummyWeights,
                coefs.getColumnVector(coefs.getColumnDimension() - 1));
        assertTrue(mse < 1e-16);
    }

    // Test VariableDrop condition ==============================================================================
    RealMatrix XVarDrop = MatrixUtils.createRealMatrix(new double[][] { { 0.18321534, -0.35029812, 0.07666221, -0.0143539, 0.07493564, 0.0264753, },
            { -0.02852663, 0.35584493, 0.49064148, -0.25236788, 0.61892759, -0.31065164 },
            { 0.21982559, 0.49110281, -0.5332698, -0.68922198, -0.52132598, 0.56451554 },
            { 0.61981706, 0.26882512, 0.50779668, 0.36052515, 0.13083871, 0.41441906 },
            { -0.63640374, -0.65128988, -0.45031639, 0.57505155, 0.22006143, -0.63980788 },
            { -0.35792762, -0.11418486, -0.09151419, 0.02036707, -0.52343739, -0.05495039 },
    });
    RealVector yVarDrop = MatrixUtils.createRealVector(new double[] { 0.00357273, 0.008411, 0.33522509, 0.07329731, -0.24901509, -0.17149104 });
    List<Integer> correctActivesVarDrop = List.of(1, 3, 0, 2, 4);
    RealVector correctAlphasVarDrop = MatrixUtils
            .createRealVector(new double[] { 0.0643070982530952, 0.0545709061429076, 0.051526183371599, 0.0273483558266873, 0.0086875666842567, 0.0056182106014212, 0.0037483228269213, 0., });

    @Test
    void testLarsVarDrop() {
        LarsPathResults lpr = LarsPath.fit(XVarDrop, yVarDrop, 500, true);
        assertEquals(correctActivesVarDrop, lpr.getActive());
        assertArrayEquals(correctAlphasVarDrop.toArray(), lpr.getAlphas().toArray(), 1e-6);
    }

    // mismatched vector sizes
    @Test
    void testLarsMismatchedInputs() {
        assertThrows(IllegalArgumentException.class, () -> LarsPath.fit(XVarDrop, yDGR, 500, true));
    }

}
