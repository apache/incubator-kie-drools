/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.config.util;

import org.junit.Test;

import static org.junit.Assert.*;

import static org.optaplanner.core.config.util.ConfigUtils.*;

public class ConfigUtilsTest {

    private static void checkCeilDivision(int dividend, int divisor) {
        long result = Math.round(Math.ceil(dividend / (double) divisor));
        assertEquals("CeilDiv: " + dividend + "/" + divisor + "=" + result, result, ceilDivide(dividend, divisor));
    }

    private static void checkFloorDivision(int dividend, int divisor) {
        long result = Math.round(Math.floor(dividend / (double) divisor));
        assertEquals("FloorDiv: " + dividend + "/" + divisor + "=" + result, result, floorDivide(dividend, divisor));
    }

    private static void checkBothDivisions(int dividend, int divisor) {
        checkCeilDivision(dividend, divisor);
        checkFloorDivision(dividend, divisor);
    }

    @Test
    public void testBothDivisions() {
        int a = 20;
        int b = 2;
        int[] signs = {-1, 1};
        for (int signB : signs) {
            for (int signA : signs) {
                for (int deltaA = -1; deltaA <= 1; deltaA++) {
                    checkBothDivisions(signA * a + deltaA, signB * b);
                }
            }
        }
    }

    @Test(expected = ArithmeticException.class)
    public void testCeilDivideByZero() {
        ceilDivide(20, -0);
    }
}
