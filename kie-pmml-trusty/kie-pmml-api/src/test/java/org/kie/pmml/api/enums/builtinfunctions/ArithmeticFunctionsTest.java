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

package org.kie.pmml.api.enums.builtinfunctions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ArithmeticFunctionsTest {

    private final static List<ArithmeticFunctions> supportedArithmeticFunctions;
    private final static List<ArithmeticFunctions> unsupportedArithmeticFunctions;

    static {
        supportedArithmeticFunctions = new ArrayList<>();
        supportedArithmeticFunctions.add(ArithmeticFunctions.AVG);
        supportedArithmeticFunctions.add(ArithmeticFunctions.MAX);
        supportedArithmeticFunctions.add(ArithmeticFunctions.MEDIAN);
        supportedArithmeticFunctions.add(ArithmeticFunctions.MIN);
        supportedArithmeticFunctions.add(ArithmeticFunctions.MINUS);
        supportedArithmeticFunctions.add(ArithmeticFunctions.MULTI);
        supportedArithmeticFunctions.add(ArithmeticFunctions.DIVISION);
        supportedArithmeticFunctions.add(ArithmeticFunctions.PLUS);
        supportedArithmeticFunctions.add(ArithmeticFunctions.PRODUCT);
        supportedArithmeticFunctions.add(ArithmeticFunctions.SUM);

        unsupportedArithmeticFunctions = new ArrayList<>();
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.LOG10);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.LN);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.SQRT);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.ABS);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.EXP);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.POW);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.THRESHOLD);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.FLOOR);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.CEIL);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.ROUND);
        unsupportedArithmeticFunctions.add(ArithmeticFunctions.MODULO);
    }

    @Test
    public void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedArithmeticFunctions.forEach(arithmeticFunction -> {
            try {
                arithmeticFunction.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        });

    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedArithmeticFunctions.forEach(arithmeticFunction -> {
            try {
                arithmeticFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });

    }

    @Test
    public void getAvgValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.AVG.getValue(input1);
        assertEquals(103.8, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.AVG.getValue(input2);
        assertEquals(-35.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAvgValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.AVG.getValue(input);
    }

    @Test
    public void getDivisionValueCorrectInput() {
        final Object[] input = {35, 5};
        Object retrieved = ArithmeticFunctions.DIVISION.getValue(input);
        assertEquals(7.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDivisionValueWrongSizeInput() {
        final Object[] input = {35};
        ArithmeticFunctions.DIVISION.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDivisionValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.DIVISION.getValue(input);
    }

    @Test
    public void getMaxValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.MAX.getValue(input1);
        assertEquals(347.0, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.MAX.getValue(input2);
        assertEquals(123.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMaxValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.MAX.getValue(input);
    }

    @Test
    public void getMedianValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.MEDIAN.getValue(input1);
        assertEquals(35.0, retrieved);
        Object[] input2 = {35, 12, 2, 123};
        retrieved = ArithmeticFunctions.MEDIAN.getValue(input2);
        assertEquals(23.5, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMedianValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.MEDIAN.getValue(input);
    }

    @Test
    public void getMinValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.MIN.getValue(input1);
        assertEquals(2.0, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.MIN.getValue(input2);
        assertEquals(-347.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.MIN.getValue(input);
    }

    @Test
    public void getMinusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = ArithmeticFunctions.MINUS.getValue(input);
        assertEquals(23.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinusValueWrongSizeInput() {
        final Object[] input = {35};
        ArithmeticFunctions.MINUS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMinusValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.MINUS.getValue(input);
    }

    @Test
    public void getMultiValueCorrectInput() {
        final Object[] input = {7, 5};
        Object retrieved = ArithmeticFunctions.MULTI.getValue(input);
        assertEquals(35.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMultiValueWrongSizeInput() {
        final Object[] input = {35};
        ArithmeticFunctions.MULTI.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMultiValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.MULTI.getValue(input);
    }

    @Test
    public void getPlusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = ArithmeticFunctions.PLUS.getValue(input);
        assertEquals(47.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPlusValueWrongSizeInput() {
        final Object[] input = {35};
        ArithmeticFunctions.PLUS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPlusValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.PLUS.getValue(input);
    }

    @Test
    public void getProductValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.PRODUCT.getValue(input1);
        assertEquals(35852040.0, retrieved);
        Object[] input2 = {35, 12, -2, 123};
        retrieved = ArithmeticFunctions.PRODUCT.getValue(input2);
        assertEquals(-103320.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getProductValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.PRODUCT.getValue(input);
    }

    @Test
    public void getSumValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.SUM.getValue(input1);
        assertEquals(519.0, retrieved);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.SUM.getValue(input2);
        assertEquals(-175.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSumValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.SUM.getValue(input);
    }

}