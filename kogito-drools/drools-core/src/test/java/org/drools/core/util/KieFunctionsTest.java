/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class KieFunctionsTest {

    @Test
    public void testKieFunctions() {

        assertEquals(true, KieFunctions.isNull(null));
        assertEquals(false, KieFunctions.isNull("nothing"));

        assertEquals(true, KieFunctions.isEmpty(null));
        assertEquals(true, KieFunctions.isEmpty(""));
        assertEquals(false, KieFunctions.isEmpty(" "));


        assertEquals(true, KieFunctions.equalsTo(55, "55"));
        assertEquals(false, KieFunctions.equalsTo(55, "550"));

        assertEquals(true, KieFunctions.equalsTo(new BigDecimal("322.123"), "322.123"));
        assertEquals(false, KieFunctions.equalsTo(new BigDecimal("322.123"), "3322.123"));

        assertEquals(true, KieFunctions.equalsTo(new BigInteger("123456"), "123456"));
        assertEquals(false, KieFunctions.equalsTo(new BigInteger("123456"), "1234567"));

        assertEquals(true, KieFunctions.equalsTo((String)null, null));
        assertEquals(false, KieFunctions.equalsTo((String)null, "a"));
        assertEquals(false, KieFunctions.equalsTo("f", null));

        assertEquals(true, KieFunctions.equalsTo((Integer)null, null));
        assertEquals(false, KieFunctions.equalsTo((Integer)null, "1"));

        boolean comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.equalsTo(44, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);


        assertEquals(true, KieFunctions.contains("welcome to jamaica", "jama"));
        assertEquals(false, KieFunctions.contains("welcome to jamaica", "Jama"));
        assertEquals(true, KieFunctions.contains(null, null));
        assertEquals(false, KieFunctions.contains("hello", null));
        assertEquals(false, KieFunctions.contains(null, "hello"));

        assertEquals(true, KieFunctions.startsWith("welcome to jamaica", "wel"));
        assertEquals(false, KieFunctions.startsWith("welcome to jamaica", "Well"));
        assertEquals(true, KieFunctions.startsWith(null, null));
        assertEquals(false, KieFunctions.startsWith("hello", null));
        assertEquals(false, KieFunctions.startsWith(null, "hello"));

        assertEquals(true, KieFunctions.endsWith("welcome to jamaica", "jamaica"));
        assertEquals(false, KieFunctions.endsWith("welcome to jamaica", "Jamaica"));
        assertEquals(true, KieFunctions.endsWith(null, null));
        assertEquals(false, KieFunctions.endsWith("hello", null));
        assertEquals(false, KieFunctions.endsWith(null, "hello"));


        assertEquals(true, KieFunctions.greaterThan(5, "2"));
        assertEquals(false, KieFunctions.greaterThan(0, "2"));
        assertEquals(false, KieFunctions.greaterThan(0, "0"));
        assertEquals(false, KieFunctions.greaterThan(null, "0"));
        assertEquals(false, KieFunctions.greaterThan(null, null));

        comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.greaterThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);


        assertEquals(true, KieFunctions.greaterOrEqualThan(5, "2"));
        assertEquals(true, KieFunctions.greaterOrEqualThan(2, "2"));
        assertEquals(false, KieFunctions.greaterOrEqualThan(0, "2"));
        assertEquals(true, KieFunctions.greaterOrEqualThan(0, "0"));
        assertEquals(false, KieFunctions.greaterOrEqualThan(null, "0"));
        assertEquals(false, KieFunctions.greaterOrEqualThan(null, null));

        comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.greaterOrEqualThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);


        assertEquals(false, KieFunctions.lessThan(5, "2"));
        assertEquals(true, KieFunctions.lessThan(0, "2"));
        assertEquals(false, KieFunctions.lessThan(0, "0"));
        assertEquals(false, KieFunctions.lessThan(null, "0"));
        assertEquals(false, KieFunctions.lessThan(null, null));

        comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.lessThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);



        assertEquals(false, KieFunctions.lessOrEqualThan(5, "2"));
        assertEquals(true, KieFunctions.lessOrEqualThan(2, "2"));
        assertEquals(true, KieFunctions.lessOrEqualThan(0, "2"));
        assertEquals(true, KieFunctions.lessOrEqualThan(0, "0"));
        assertEquals(false, KieFunctions.lessOrEqualThan(null, "0"));
        assertEquals(false, KieFunctions.lessOrEqualThan(null, null));

        comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.lessOrEqualThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);



        assertEquals(false, KieFunctions.between(0, "1", "10"));
        assertEquals(false, KieFunctions.between(11, "1", "10"));
        assertEquals(true, KieFunctions.between(1, "1", "10"));
        assertEquals(true, KieFunctions.between(10, "1", "10"));
        assertEquals(true, KieFunctions.between(2, "1", "10"));
        assertEquals(false, KieFunctions.between(null, "5", "6"));

        comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.between(2, null, "9"));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);

        comparitionFailed = false;
        try {
            assertEquals(false, KieFunctions.between(2, "1", null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertEquals(true, comparitionFailed);



        assertEquals(true, KieFunctions.isTrue(true));
        assertEquals(false, KieFunctions.isTrue(null));
        assertEquals(false, KieFunctions.isTrue(false));

        assertEquals(true, KieFunctions.isFalse(false));
        assertEquals(false, KieFunctions.isFalse(null));
        assertEquals(false, KieFunctions.isFalse(true));

    }
}
