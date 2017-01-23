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

public class ConfigUtilsTest {

    @Test
    public void mergeProperty() {
        Integer a = null;
        Integer b = null;
        assertEquals(null, ConfigUtils.mergeProperty(a, b));
        a = Integer.valueOf(1);
        assertEquals(null, ConfigUtils.mergeProperty(a, b));
        b = Integer.valueOf(10);
        assertEquals(null, ConfigUtils.mergeProperty(a, b));
        b = Integer.valueOf(1);
        assertEquals(Integer.valueOf(1), ConfigUtils.mergeProperty(a, b));
        a = null;
        assertEquals(null, ConfigUtils.mergeProperty(a, b));
    }

    @Test
    public void meldProperty() {
        Integer a = null;
        Integer b = null;
        assertEquals(null, ConfigUtils.meldProperty(a, b));
        a = Integer.valueOf(1);
        assertEquals(Integer.valueOf(1), ConfigUtils.meldProperty(a, b));
        b = Integer.valueOf(10);
        assertEquals(ConfigUtils.mergeProperty(Integer.valueOf(1), Integer.valueOf(10)), ConfigUtils.meldProperty(a, b));
        a = null;
        assertEquals(Integer.valueOf(10), ConfigUtils.meldProperty(a, b));
    }

    @Test
    public void ceilDivide() {
        assertEquals(10, ConfigUtils.ceilDivide(19, 2));
        assertEquals(10, ConfigUtils.ceilDivide(20, 2));
        assertEquals(11, ConfigUtils.ceilDivide(21, 2));

        assertEquals(-9, ConfigUtils.ceilDivide(19, -2));
        assertEquals(-10, ConfigUtils.ceilDivide(20, -2));
        assertEquals(-10, ConfigUtils.ceilDivide(21, -2));

        assertEquals(-9, ConfigUtils.ceilDivide(-19, 2));
        assertEquals(-10, ConfigUtils.ceilDivide(-20, 2));
        assertEquals(-10, ConfigUtils.ceilDivide(-21, 2));

        assertEquals(10, ConfigUtils.ceilDivide(-19, -2));
        assertEquals(10, ConfigUtils.ceilDivide(-20, -2));
        assertEquals(11, ConfigUtils.ceilDivide(-21, -2));
    }

    @Test(expected = ArithmeticException.class)
    public void ceilDivideByZero() {
        ConfigUtils.ceilDivide(20, -0);
    }

}
