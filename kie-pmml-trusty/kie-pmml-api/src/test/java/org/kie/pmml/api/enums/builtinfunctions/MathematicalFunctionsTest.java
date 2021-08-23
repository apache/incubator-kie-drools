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

import static org.junit.Assert.assertEquals;

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
    public void getExpm1ValueCorrectInput() {
        Object[] input = {24.11};
        Object retrieved = MathematicalFunctions.EXPM1.getValue(input);
        assertEquals(2.956922613825104E10, (Double) retrieved, 0.0000001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getExpm1ValueWrongSizeInput() {
        final Object[] input = {34, 24.11};
        MathematicalFunctions.EXPM1.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getExpm1ValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.EXPM1.getValue(input);
    }

    @Test
    public void getHypotValueCorrectInput() {
        Object[] input = {24.11, 11};
        Object retrieved = MathematicalFunctions.HYPOT.getValue(input);
        assertEquals(26.500, (Double) retrieved, 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHypotValueWrongSizeInput() {
        final Object[] input = {34};
        MathematicalFunctions.HYPOT.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHypotValueWrongTypeInput() {
        final Object[] input = {"22.1", 11};
        MathematicalFunctions.HYPOT.getValue(input);
    }

    @Test
    public void getLn1pValueCorrectInput() {
        Object[] input = {24.11};
        Object retrieved = MathematicalFunctions.LN1P.getValue(input);
        assertEquals(3.223, (Double) retrieved, 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLn1pValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.LN1P.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLn1pValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.LN1P.getValue(input);
    }

    @Test
    public void getRintValueCorrectInput() {
        Object[] input1 = {24.11};
        Object retrieved = MathematicalFunctions.RINT.getValue(input1);
        assertEquals(24, (Double) retrieved, 0);
        Object[] input2 = {24.91};
        retrieved = MathematicalFunctions.RINT.getValue(input2);
        assertEquals(25, (Double) retrieved, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRintValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.RINT.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRintValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.RINT.getValue(input);
    }

    @Test
    public void getSinValueCorrectInput() {
        Object[] input = {24.11};
        Object retrieved = MathematicalFunctions.SIN.getValue(input);
        assertEquals(-0.8535, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSinValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.SIN.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSinValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.SIN.getValue(input);
    }

    @Test
    public void getAsinValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.ASIN.getValue(input);
        assertEquals(1.1944, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAsinValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.ASIN.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAsinValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.ASIN.getValue(input);
    }

    @Test
    public void getSinhValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.SINH.getValue(input);
        assertEquals(1.0699, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSinhValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.SINH.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSinhValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.SINH.getValue(input);
    }

    @Test
    public void getCosValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.COS.getValue(input);
        assertEquals(0.5978, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCosValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.COS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCosValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.COS.getValue(input);
    }

    @Test
    public void getAcosValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.ACOS.getValue(input);
        assertEquals(0.3763, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAcosValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.ACOS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAcosValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.ACOS.getValue(input);
    }

    @Test
    public void getCoshValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.COSH.getValue(input);
        assertEquals(1.4645, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCoshValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.COSH.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCoshValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.COSH.getValue(input);
    }

    @Test
    public void getTanValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.TAN.getValue(input);
        assertEquals(1.3408, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTanValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.TAN.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTanValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.TAN.getValue(input);
    }

    @Test
    public void getAtanValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.ATAN.getValue(input);
        assertEquals(0.7491, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAtanValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.ATAN.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAtanValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.ATAN.getValue(input);
    }

    @Test
    public void getTanhValueCorrectInput() {
        Object[] input = {0.93};
        Object retrieved = MathematicalFunctions.TANH.getValue(input);
        assertEquals(0.7305, (Double) retrieved, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTanhValueWrongSizeInput() {
        final Object[] input = {34, 11};
        MathematicalFunctions.TANH.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTanhValueWrongTypeInput() {
        final Object[] input = {"22.1"};
        MathematicalFunctions.TANH.getValue(input);
    }
}