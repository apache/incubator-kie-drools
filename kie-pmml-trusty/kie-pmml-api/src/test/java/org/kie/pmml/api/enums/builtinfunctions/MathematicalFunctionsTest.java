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

public class MathematicalFunctionsTest {

    public final static List<MathematicalFunctions> supportedMathematicalFunctions;
    public final static List<MathematicalFunctions> unsupportedMathematicalFunctions;

    static {
        supportedMathematicalFunctions = new ArrayList<>();
        supportedMathematicalFunctions.add(MathematicalFunctions.EXPM1);
        supportedMathematicalFunctions.add(MathematicalFunctions.HYPOT);
        supportedMathematicalFunctions.add(MathematicalFunctions.LN1P);
        supportedMathematicalFunctions.add(MathematicalFunctions.RINT);
        supportedMathematicalFunctions.add(MathematicalFunctions.SIN);
        supportedMathematicalFunctions.add(MathematicalFunctions.ASIN);
        supportedMathematicalFunctions.add(MathematicalFunctions.SINH);
        supportedMathematicalFunctions.add(MathematicalFunctions.COS);
        supportedMathematicalFunctions.add(MathematicalFunctions.ACOS);
        supportedMathematicalFunctions.add(MathematicalFunctions.COSH);
        supportedMathematicalFunctions.add(MathematicalFunctions.TAN);
        supportedMathematicalFunctions.add(MathematicalFunctions.ATAN);
        supportedMathematicalFunctions.add(MathematicalFunctions.TANH);

        unsupportedMathematicalFunctions = new ArrayList<>();
    }

    @Test
    void getExpm1ValueCorrectInput() {
        Object[] input = {24.11};
        Object retrieved = MathematicalFunctions.EXPM1.getValue(input);
        assertThat((Double) retrieved).isCloseTo(2.956922613825104E10, Offset.offset(0.0000001));
    }

    @Test
    void getExpm1ValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 24.11};
            MathematicalFunctions.EXPM1.getValue(input);
        });
    }

    @Test
    void getExpm1ValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.EXPM1.getValue(input);
        });
    }

    @Test
    void getHypotValueCorrectInput() {
        Object[] input = {24.11, 11};
        Object retrieved = MathematicalFunctions.HYPOT.getValue(input);
        assertThat((Double) retrieved).isCloseTo(26.500, Offset.offset(0.001));
    }

    @Test
    void getHypotValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34};
            MathematicalFunctions.HYPOT.getValue(input);
        });
    }

    @Test
    void getHypotValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1", 11};
            MathematicalFunctions.HYPOT.getValue(input);
        });
    }

    @Test
    void getLn1pValueCorrectInput() {
        Object[] input = {24.11};
        Object retrieved = MathematicalFunctions.LN1P.getValue(input);
        assertThat((Double) retrieved).isCloseTo(3.223, Offset.offset(0.001));
    }

    @Test
    void getLn1pValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.LN1P.getValue(input);
        });
    }

    @Test
    void getLn1pValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.LN1P.getValue(input);
        });
    }

    @Test
    void getRintValueCorrectInput() {
        Object[] input1 = {24.11};
        Double retrieved = (Double) MathematicalFunctions.RINT.getValue(input1);
        assertThat(retrieved).isCloseTo(24.0, Offset.offset(0.0));
        Object[] input2 = {24.91};
        retrieved = (Double) MathematicalFunctions.RINT.getValue(input2);
        assertThat(retrieved).isCloseTo(25, Offset.offset(0.0));
    }

    @Test
    void getRintValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.RINT.getValue(input);
        });
    }

    @Test
    void getRintValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.RINT.getValue(input);
        });
    }

    @Test
    void getSinValueCorrectInput() {
        Object[] input = {24.11};
        Object retrieved = MathematicalFunctions.SIN.getValue(input);
        assertThat((Double) retrieved).isCloseTo(-0.8535, Offset.offset(0.0001));
    }

    @Test
    void getSinValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.SIN.getValue(input);
        });
    }

    @Test
    void getSinValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.SIN.getValue(input);
        });
    }

    @Test
    void getAsinValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.ASIN.getValue(input);
        assertThat((Double) retrieved).isCloseTo(1.1944, Offset.offset(0.0001));
    }

    @Test
    void getAsinValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.ASIN.getValue(input);
        });
    }

    @Test
    void getAsinValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.ASIN.getValue(input);
        });
    }

    @Test
    void getSinhValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.SINH.getValue(input);
        assertThat((Double) retrieved).isCloseTo(1.0699, Offset.offset(0.0001));
    }

    @Test
    void getSinhValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.SINH.getValue(input);
        });
    }

    @Test
    void getSinhValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.SINH.getValue(input);
        });
    }

    @Test
    void getCosValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.COS.getValue(input);
        assertThat((Double) retrieved).isCloseTo(0.5978, Offset.offset(0.0001));
    }

    @Test
    void getCosValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.COS.getValue(input);
        });
    }

    @Test
    void getCosValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.COS.getValue(input);
        });
    }

    @Test
    void getAcosValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.ACOS.getValue(input);
        assertThat((Double) retrieved).isCloseTo(0.3763, Offset.offset(0.0001));
    }

    @Test
    void getAcosValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.ACOS.getValue(input);
        });
    }

    @Test
    void getAcosValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.ACOS.getValue(input);
        });
    }

    @Test
    void getCoshValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.COSH.getValue(input);
        assertThat((Double) retrieved).isCloseTo(1.4645, Offset.offset(0.0001));
    }

    @Test
    void getCoshValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.COSH.getValue(input);
        });
    }

    @Test
    void getCoshValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.COSH.getValue(input);
        });
    }

    @Test
    void getTanValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.TAN.getValue(input);
        assertThat((Double) retrieved).isCloseTo(1.3408, Offset.offset(0.0001));
    }

    @Test
    void getTanValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.TAN.getValue(input);
        });
    }

    @Test
    void getTanValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.TAN.getValue(input);
        });
    }

    @Test
    void getAtanValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.ATAN.getValue(input);
        assertThat((Double) retrieved).isCloseTo(0.7491, Offset.offset(0.0001));
    }

    @Test
    void getAtanValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.ATAN.getValue(input);
        });
    }

    @Test
    void getAtanValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.ATAN.getValue(input);
        });
    }

    @Test
    void getTanhValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.TANH.getValue(input);
        assertThat((Double) retrieved).isCloseTo(0.7305, Offset.offset(0.0001));
    }

    @Test
    void getTanhValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34, 11};
            MathematicalFunctions.TANH.getValue(input);
        });
    }

    @Test
    void getTanhValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"22.1"};
            MathematicalFunctions.TANH.getValue(input);
        });
    }
}