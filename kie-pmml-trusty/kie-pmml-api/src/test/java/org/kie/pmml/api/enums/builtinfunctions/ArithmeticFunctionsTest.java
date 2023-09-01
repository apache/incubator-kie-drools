/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.api.enums.builtinfunctions;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
    void getAvgValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.AVG.getValue(input1);
        assertThat(retrieved).isEqualTo(103.8);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.AVG.getValue(input2);
        assertThat(retrieved).isEqualTo(-35.0);
    }

    @Test
    void getAvgValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.AVG.getValue(input);
        });
    }

    @Test
    void getDivisionValueCorrectInput() {
        final Object[] input = {35, 5};
        Object retrieved = ArithmeticFunctions.DIVISION.getValue(input);
        assertThat(retrieved).isEqualTo(7.0);
    }

    @Test
    void getDivisionValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {35};
            ArithmeticFunctions.DIVISION.getValue(input);
        });
    }

    @Test
    void getDivisionValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.DIVISION.getValue(input);
        });
    }

    @Test
    void getMaxValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.MAX.getValue(input1);
        assertThat(retrieved).isEqualTo(347.0);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.MAX.getValue(input2);
        assertThat(retrieved).isEqualTo(123.0);
    }

    @Test
    void getMaxValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.MAX.getValue(input);
        });
    }

    @Test
    void getMedianValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.MEDIAN.getValue(input1);
        assertThat(retrieved).isEqualTo(35.0);
        Object[] input2 = {35, 12, 2, 123};
        retrieved = ArithmeticFunctions.MEDIAN.getValue(input2);
        assertThat(retrieved).isEqualTo(23.5);
    }

    @Test
    void getMedianValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.MEDIAN.getValue(input);
        });
    }

    @Test
    void getMinValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.MIN.getValue(input1);
        assertThat(retrieved).isEqualTo(2.0);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.MIN.getValue(input2);
        assertThat(retrieved).isEqualTo(-347.0);
    }

    @Test
    void getMinValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.MIN.getValue(input);
        });
    }

    @Test
    void getMinusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = ArithmeticFunctions.MINUS.getValue(input);
        assertThat(retrieved).isEqualTo(23.0);
    }

    @Test
    void getMinusValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {35};
            ArithmeticFunctions.MINUS.getValue(input);
        });
    }

    @Test
    void getMinusValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.MINUS.getValue(input);
        });
    }

    @Test
    void getMultiValueCorrectInput() {
        final Object[] input = {7, 5};
        Object retrieved = ArithmeticFunctions.MULTI.getValue(input);
        assertThat(retrieved).isEqualTo(35.0);
    }

    @Test
    void getMultiValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {35};
            ArithmeticFunctions.MULTI.getValue(input);
        });
    }

    @Test
    void getMultiValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.MULTI.getValue(input);
        });
    }

    @Test
    void getPlusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = ArithmeticFunctions.PLUS.getValue(input);
        assertThat(retrieved).isEqualTo(47.0);
    }

    @Test
    void getPlusValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {35};
            ArithmeticFunctions.PLUS.getValue(input);
        });
    }

    @Test
    void getPlusValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.PLUS.getValue(input);
        });
    }

    @Test
    void getProductValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.PRODUCT.getValue(input1);
        assertThat(retrieved).isEqualTo(35852040.0);
        Object[] input2 = {35, 12, -2, 123};
        retrieved = ArithmeticFunctions.PRODUCT.getValue(input2);
        assertThat(retrieved).isEqualTo(-103320.0);
    }

    @Test
    void getProductValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.PRODUCT.getValue(input);
        });
    }

    @Test
    void getSumValueCorrectInput() {
        Object[] input1 = {35, 12, 347, 2, 123};
        Object retrieved = ArithmeticFunctions.SUM.getValue(input1);
        assertThat(retrieved).isEqualTo(519.0);
        Object[] input2 = {35, 12, -347, 2, 123};
        retrieved = ArithmeticFunctions.SUM.getValue(input2);
        assertThat(retrieved).isEqualTo(-175.0);
    }

    @Test
    void getSumValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 34};
            ArithmeticFunctions.SUM.getValue(input);
        });
    }

    @Test
    void getLog10ValueCorrectInput() {
        Object[] input = {35};
        Object retrieved = ArithmeticFunctions.LOG10.getValue(input);
        assertThat((double) retrieved).isCloseTo(1.5440680443503, Offset.offset(0.00000001));
    }

    @Test
    void getLog10ValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.LOG10.getValue(input);
        });
    }

    @Test
    void getLnValueCorrectInput() {
        Object[] input = {35};
        Object retrieved = ArithmeticFunctions.LN.getValue(input);
        assertThat((double) retrieved).isCloseTo(3.5553480614894, Offset.offset(0.00000001));
    }

    @Test
    void getLnValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.LN.getValue(input);
        });
    }

    @Test
    void getSqrtValueCorrectInput() {
        Object[] input = {81};
        Object retrieved = ArithmeticFunctions.SQRT.getValue(input);
        assertThat((double) retrieved).isCloseTo(9, Offset.offset(0.00000001));
    }

    @Test
    void getSqrtValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.SQRT.getValue(input);
        });
    }

    @Test
    void getAbsValueCorrectInput() {
        Object[] input = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.ABS.getValue(input);
        assertThat((double) retrieved).isCloseTo(3.5553480614894, Offset.offset(0.00000001));
    }

    @Test
    void getAbsValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.ABS.getValue(input);
        });
    }

    @Test
    void getExpValueCorrectInput() {
        Object[] input = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.EXP.getValue(input);
        assertThat((double) retrieved).isCloseTo(0.0285714285714, Offset.offset(0.00000001));
    }

    @Test
    void getExpValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.EXP.getValue(input);
        });
    }

    @Test
    void getPowValueCorrectInput() {
        Object[] input = {3, 4};
        Object retrieved = ArithmeticFunctions.POW.getValue(input);
        assertThat(retrieved).isEqualTo(81.0);
    }

    @Test
    void getPowValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 32};
            ArithmeticFunctions.POW.getValue(input);
        });
    }

    @Test
    void getThresholdValueCorrectInput() {
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

    @Test
    void getThresholdValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 32};
            ArithmeticFunctions.THRESHOLD.getValue(input);
        });
    }

    @Test
    void getFloorValueCorrectInput() {
        Object[] input1 = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.FLOOR.getValue(input1);
        assertThat(retrieved).isEqualTo(-4.0);
        Object[] input2 = {3.5553480614894};
        retrieved = ArithmeticFunctions.FLOOR.getValue(input2);
        assertThat(retrieved).isEqualTo(3.0);
    }

    @Test
    void getFloorValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.FLOOR.getValue(input);
        });
    }

    @Test
    void getCeilValueCorrectInput() {
        Object[] input1 = {-3.5553480614894};
        Object retrieved = ArithmeticFunctions.CEIL.getValue(input1);
        assertThat(retrieved).isEqualTo(-3.0);
        Object[] input2 = {3.5553480614894};
        retrieved = ArithmeticFunctions.CEIL.getValue(input2);
        assertThat(retrieved).isEqualTo(4.0);
    }

    @Test
    void getCeilValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.CEIL.getValue(input);
        });
    }

    @Test
    void getRoundValueCorrectInput() {
        Object[] input1 = {3.3553480614894};
        Object retrieved = ArithmeticFunctions.ROUND.getValue(input1);
        assertThat(retrieved).isEqualTo(3.0);
        Object[] input2 = {3.5553480614894};
        retrieved = ArithmeticFunctions.ROUND.getValue(input2);
        assertThat(retrieved).isEqualTo(4.0);
    }

    @Test
    void getRoundValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A"};
            ArithmeticFunctions.ROUND.getValue(input);
        });
    }

    @Test
    void getModuloValueCorrectInput() {
        Object[] input = {35, 8};
        Object retrieved = ArithmeticFunctions.MODULO.getValue(input);
        assertThat(retrieved).isEqualTo(3.0);
    }

    @Test
    void getModuloValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"A", 35};
            ArithmeticFunctions.MODULO.getValue(input);
        });
    }
}