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

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArithmeticFunctionsTest {

    public final static List<ArithmeticFunctions> supportedArithmeticFunctions;
    public final static List<ArithmeticFunctions> unsupportedArithmeticFunctions;

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
        supportedArithmeticFunctions.add(ArithmeticFunctions.LOG10);
        supportedArithmeticFunctions.add(ArithmeticFunctions.LN);
        supportedArithmeticFunctions.add(ArithmeticFunctions.SQRT);
        supportedArithmeticFunctions.add(ArithmeticFunctions.ABS);
        supportedArithmeticFunctions.add(ArithmeticFunctions.EXP);
        supportedArithmeticFunctions.add(ArithmeticFunctions.POW);
        supportedArithmeticFunctions.add(ArithmeticFunctions.THRESHOLD);
        supportedArithmeticFunctions.add(ArithmeticFunctions.FLOOR);
        supportedArithmeticFunctions.add(ArithmeticFunctions.CEIL);
        supportedArithmeticFunctions.add(ArithmeticFunctions.ROUND);
        supportedArithmeticFunctions.add(ArithmeticFunctions.MODULO);

        unsupportedArithmeticFunctions = new ArrayList<>();
    }

    @Test
    public void getAvgValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.AVG.getValue(input1);
        assertThat(retrieved).isEqualTo(103.8);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.AVG.getValue(input2);
        assertThat(retrieved).isEqualTo(-35.0);
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
        assertThat(retrieved).isEqualTo(7.0);
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
        assertThat(retrieved).isEqualTo(347.0);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.MAX.getValue(input2);
        assertThat(retrieved).isEqualTo(123.0);
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
        assertThat(retrieved).isEqualTo(35.0);
        Object[] input2 = {35, 12, 2, 123};
        retrieved = ArithmeticFunctions.MEDIAN.getValue(input2);
        assertThat(retrieved).isEqualTo(23.5);
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
        assertThat(retrieved).isEqualTo(2.0);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.MIN.getValue(input2);
        assertThat(retrieved).isEqualTo(-347.0);
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
        assertThat(retrieved).isEqualTo(23.0);
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
        assertThat(retrieved).isEqualTo(35.0);
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
        assertThat(retrieved).isEqualTo(47.0);
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
        assertThat(retrieved).isEqualTo(35852040.0);
        Object[] input2 = {35, 12, -2, 123};
        retrieved = ArithmeticFunctions.PRODUCT.getValue(input2);
        assertThat(retrieved).isEqualTo(-103320.0);
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
        assertThat(retrieved).isEqualTo(519.0);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.SUM.getValue(input2);
        assertThat(retrieved).isEqualTo(-175.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSumValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        ArithmeticFunctions.SUM.getValue(input);
    }

    @Test
    public void getLog10ValueCorrectInput() {
        Object[] input = {35};
        Object retrieved = ArithmeticFunctions.LOG10.getValue(input);
        assertThat((double)retrieved).isCloseTo(1.5440680443503, Offset.offset(0.00000001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLog10ValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.LOG10.getValue(input);
    }

    @Test
    public void getLnValueCorrectInput() {
        Object[] input = {35};
        Object retrieved = ArithmeticFunctions.LN.getValue(input);
        assertThat((double)retrieved).isCloseTo(3.5553480614894, Offset.offset(0.00000001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLnValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.LN.getValue(input);
    }

    @Test
    public void getSqrtValueCorrectInput() {
        Object[] input = {81};
        Object retrieved = ArithmeticFunctions.SQRT.getValue(input);
        assertThat((double)retrieved).isCloseTo(9, Offset.offset(0.00000001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSqrtValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.SQRT.getValue(input);
    }

    @Test
    public void getAbsValueCorrectInput() {
        Object[] input = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.ABS.getValue(input);
        assertThat((double)retrieved).isCloseTo(3.5553480614894, Offset.offset(0.00000001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAbsValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.ABS.getValue(input);
    }

    @Test
    public void getExpValueCorrectInput() {
        Object[] input = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.EXP.getValue(input);
        assertThat((double)retrieved).isCloseTo(0.0285714285714, Offset.offset(0.00000001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getExpValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.EXP.getValue(input);
    }

    @Test
    public void getPowValueCorrectInput() {
        Object[] input = {3, 4};
        Object retrieved = ArithmeticFunctions.POW.getValue(input);
        assertThat(retrieved).isEqualTo(81.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPowValueWrongTypeInput() {
        final Object[] input = {"A", 32};
        ArithmeticFunctions.POW.getValue(input);
    }

    @Test
    public void getThresholdValueCorrectInput() {
        Object[] input1 = {5, 4};
        Object retrieved = ArithmeticFunctions.THRESHOLD.getValue(input1);
        assertThat(retrieved).isEqualTo(1.0);
        Object[] input2 = {5, 5};
        retrieved = ArithmeticFunctions.THRESHOLD.getValue(input2);
        assertThat(retrieved).isEqualTo(0.0);
        Object[] input3 = {4, 5};
        retrieved = ArithmeticFunctions.THRESHOLD.getValue(input3);
        assertThat(retrieved).isEqualTo(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThresholdValueWrongTypeInput() {
        final Object[] input = {"A", 32};
        ArithmeticFunctions.THRESHOLD.getValue(input);
    }

    @Test
    public void getFloorValueCorrectInput() {
        Object[] input1 = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.FLOOR.getValue(input1);
        assertThat(retrieved).isEqualTo(-4.0);
        Object[] input2 = {3.5553480614894};
        retrieved = ArithmeticFunctions.FLOOR.getValue(input2);
        assertThat(retrieved).isEqualTo(3.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFloorValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.FLOOR.getValue(input);
    }

    @Test
    public void getCeilValueCorrectInput() {
        Object[] input1 = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.CEIL.getValue(input1);
        assertThat(retrieved).isEqualTo(-3.0);
        Object[] input2 = {3.5553480614894};
        retrieved = ArithmeticFunctions.CEIL.getValue(input2);
        assertThat(retrieved).isEqualTo(4.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCeilValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.CEIL.getValue(input);
    }

    @Test
    public void getRoundValueCorrectInput() {
        Object[] input1 = {3.3553480614894};
        Object retrieved = ArithmeticFunctions.ROUND.getValue(input1);
        assertThat(retrieved).isEqualTo(3.0);
        Object[] input2 = {3.5553480614894};
        retrieved = ArithmeticFunctions.ROUND.getValue(input2);
        assertThat(retrieved).isEqualTo(4.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRoundValueWrongTypeInput() {
        final Object[] input = {"A"};
        ArithmeticFunctions.ROUND.getValue(input);
    }

    @Test
    public void getModuloValueCorrectInput() {
        Object[] input = {35, 8};
        Object retrieved = ArithmeticFunctions.MODULO.getValue(input);
        assertThat(retrieved).isEqualTo(3.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getModuloValueWrongTypeInput() {
        final Object[] input = {"A", 35};
        ArithmeticFunctions.MODULO.getValue(input);
    }

}