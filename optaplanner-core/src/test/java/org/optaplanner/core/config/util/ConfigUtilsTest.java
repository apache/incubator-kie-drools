/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

    @Test
    public void testMergeProperty() {
        Integer a = null;
        Integer b = null;
        assertEquals(null, mergeProperty(a, b));
        a = Integer.valueOf(1);
        assertEquals(null, mergeProperty(a, b));
        b = Integer.valueOf(10);
        assertEquals(null, mergeProperty(a, b));
        b = Integer.valueOf(1);
        assertEquals(Integer.valueOf(1), mergeProperty(a, b));
        a = null;
        assertEquals(null, mergeProperty(a, b));
    }

    @Test
    public void testMeldProperty() {
        Integer a = null;
        Integer b = null;
        assertEquals(null, meldProperty(a, b));
        a = Integer.valueOf(1);
        assertEquals(Integer.valueOf(1), meldProperty(a, b));
        b = Integer.valueOf(10);
        assertEquals(mergeProperty(Integer.valueOf(1), Integer.valueOf(10)), meldProperty(a, b));
        a = null;
        assertEquals(Integer.valueOf(10), meldProperty(a, b));
    }

    @Test
    public void testCeilDivide() {
        assertEquals(10, ceilDivide(19, 2));
        assertEquals(10, ceilDivide(20, 2));
        assertEquals(11, ceilDivide(21, 2));

        assertEquals(-9, ceilDivide(19, -2));
        assertEquals(-10, ceilDivide(20, -2));
        assertEquals(-10, ceilDivide(21, -2));

        assertEquals(-9, ceilDivide(-19, 2));
        assertEquals(-10, ceilDivide(-20, 2));
        assertEquals(-10, ceilDivide(-21, 2));

        assertEquals(10, ceilDivide(-19, -2));
        assertEquals(10, ceilDivide(-20, -2));
        assertEquals(11, ceilDivide(-21, -2));
    }

    @Test
    public void testFloorDivide() {
        assertEquals(9, floorDivide(19, 2));
        assertEquals(10, floorDivide(20, 2));
        assertEquals(10, floorDivide(21, 2));

        assertEquals(-10, floorDivide(19, -2));
        assertEquals(-10, floorDivide(20, -2));
        assertEquals(-11, floorDivide(21, -2));

        assertEquals(-10, floorDivide(-19, 2));
        assertEquals(-10, floorDivide(-20, 2));
        assertEquals(-11, floorDivide(-21, 2));

        assertEquals(9, floorDivide(-19, -2));
        assertEquals(10, floorDivide(-20, -2));
        assertEquals(10, floorDivide(-21, -2));
    }

    @Test(expected = ArithmeticException.class)
    public void testCeilDivideByZero() {
        ceilDivide(20, -0);
    }

    @Test(expected = ArithmeticException.class)
    public void testFloorDivideByZero() {
        floorDivide(20, -0);
    }
}
