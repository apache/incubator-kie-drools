/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.api.enums;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BUILTIN_FUNCTIONSTest {

    private final static List<BUILTIN_FUNCTIONS> supportedBuiltinFunctions;
    private final static List<BUILTIN_FUNCTIONS> unsupportedBuiltinFunctions;

    static {
        supportedBuiltinFunctions = new ArrayList<>();
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.AVG);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LOWERCASE);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MAX);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MEDIAN);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MIN);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MINUS);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MULTI);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DIVISION);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.PLUS);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.PRODUCT);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SUM);
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.UPPERCASE);

        unsupportedBuiltinFunctions = new ArrayList<>();
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LOG10);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SQRT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ABS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.EXP);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.POW);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.THRESHOLD);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.FLOOR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.CEIL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ROUND);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MODULO);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_MISSING);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_NOT_MISSING);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_VALID);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_NOT_VALID);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NOT_EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LESS_THAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LESS_OR_EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.GREATER_THAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.GREATER_OR_EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.AND);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.OR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NOT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_IN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_NOT_IN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STRING_LENGTH);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SUBSTRING);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.TRIM_BLANKS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.CONCAT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.REPLACE);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MATCHES);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.FORMAT_NUMBER);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.FORMAT_DATE_TIME);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DATE_DAYS_SINCE_YEAR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DATE_SECONDS_SINCE_YEAR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DATE_SECONDS_SINCE_MIDNIGHT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NORMAL_CDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NORMAL_PDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STD_NORMAL_CDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STD_NORMAL_PDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ERF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NORMAL_IDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STD_NORMAL_IDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.EXPM1);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.HYPOT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LN1P);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.RINT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SIN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ASIN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SINH);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.COS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ACOS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.COSH);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.TAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ATAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.TANH);
    }

    @Test
    public void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedBuiltinFunctions.forEach(builtinFunction -> {
            try {
                builtinFunction.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        });

    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedBuiltinFunctions.forEach(builtinFunction -> {
            try {
                builtinFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });

    }

    @Test
    public void getAvgValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = BUILTIN_FUNCTIONS.AVG.getValue(input1);
        assertEquals(103.8, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = BUILTIN_FUNCTIONS.AVG.getValue(input2);
        assertEquals(-35.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAvgValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.AVG.getValue(input);
    }

    @Test
    public void getDivisionValueCorrectInput() {
        final Object[] input = {35, 5};
        Object retrieved = BUILTIN_FUNCTIONS.DIVISION.getValue(input);
        assertEquals(7.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDivisionValueWrongSizeInput() {
        final Object[] input = {35};
        BUILTIN_FUNCTIONS.DIVISION.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDivisionValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.DIVISION.getValue(input);
    }

    @Test
    public void getLowercaseValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = BUILTIN_FUNCTIONS.LOWERCASE.getValue(input);
        assertEquals("awdc", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLowercaseValueWrongSizeInput() {
        final Object[] input = {"AwdC", "AwdB"};
        BUILTIN_FUNCTIONS.LOWERCASE.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLowercaseValueWrongTypeInput() {
        final Object[] input = {34};
        BUILTIN_FUNCTIONS.LOWERCASE.getValue(input);
    }

    @Test
    public void getMaxValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = BUILTIN_FUNCTIONS.MAX.getValue(input1);
        assertEquals(347.0, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = BUILTIN_FUNCTIONS.MAX.getValue(input2);
        assertEquals(123.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMaxValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.MAX.getValue(input);
    }

    @Test
    public void getMedianValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = BUILTIN_FUNCTIONS.MEDIAN.getValue(input1);
        assertEquals(35.0, retrieved);
        Object[] input2 = {35, 12, 2, 123};
        retrieved = BUILTIN_FUNCTIONS.MEDIAN.getValue(input2);
        assertEquals(23.5, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMedianValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.MEDIAN.getValue(input);
    }

    @Test
    public void getMinValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = BUILTIN_FUNCTIONS.MIN.getValue(input1);
        assertEquals(2.0, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = BUILTIN_FUNCTIONS.MIN.getValue(input2);
        assertEquals(-347.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.MIN.getValue(input);
    }

    @Test
    public void getMinusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = BUILTIN_FUNCTIONS.MINUS.getValue(input);
        assertEquals(23.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinusValueWrongSizeInput() {
        final Object[] input = {35};
        BUILTIN_FUNCTIONS.MINUS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinusValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.MINUS.getValue(input);
    }

    @Test
    public void getMultiValueCorrectInput() {
        final Object[] input = {7, 5};
        Object retrieved = BUILTIN_FUNCTIONS.MULTI.getValue(input);
        assertEquals(35.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMultiValueWrongSizeInput() {
        final Object[] input = {35};
        BUILTIN_FUNCTIONS.MULTI.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMultiValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.MULTI.getValue(input);
    }

    @Test
    public void getPlusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = BUILTIN_FUNCTIONS.PLUS.getValue(input);
        assertEquals(47.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPlusValueWrongSizeInput() {
        final Object[] input = {35};
        BUILTIN_FUNCTIONS.PLUS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPlusValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.PLUS.getValue(input);
    }

    @Test
    public void getProductValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = BUILTIN_FUNCTIONS.PRODUCT.getValue(input1);
        assertEquals(35852040.0, retrieved);
        Object[] input2 = {35, 12, -2, 123};
        retrieved = BUILTIN_FUNCTIONS.PRODUCT.getValue(input2);
        assertEquals(-103320.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getProductValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.PRODUCT.getValue(input);
    }

    @Test
    public void getSumValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = BUILTIN_FUNCTIONS.SUM.getValue(input1);
        assertEquals(519.0, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = BUILTIN_FUNCTIONS.SUM.getValue(input2);
        assertEquals(-175.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSumValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.SUM.getValue(input);
    }

    @Test
    public void getUppercaseValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = BUILTIN_FUNCTIONS.UPPERCASE.getValue(input);
        assertEquals("AWDC", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUppercaseValueWrongSizeInput() {
        final Object[] input = {"AwdC", "AwdB"};
        BUILTIN_FUNCTIONS.UPPERCASE.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUppercaseValueWrongTypeInput() {
        final Object[] input = {34};
        BUILTIN_FUNCTIONS.UPPERCASE.getValue(input);
    }
}