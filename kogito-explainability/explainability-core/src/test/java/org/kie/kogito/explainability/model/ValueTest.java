/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueTest {

    @Test
    void testNumericValue() {
        Value<Double> numericValue = new Value<>(1.1);
        assertNotNull(numericValue.getUnderlyingObject());
        assertEquals(1.1, numericValue.getUnderlyingObject());
        assertNotNull(numericValue.asString());
        assertEquals("1.1", numericValue.asString());
        assertEquals(1.1, numericValue.asNumber());
        double[] vector = numericValue.asVector();
        assertNotNull(vector);
        assertArrayEquals(new double[] { 1.1 }, vector);
    }

    @Test
    void testVectorValue() {
        double[] doubles = new double[3];
        doubles[0] = 0.1;
        doubles[1] = 0.2;
        doubles[2] = 0.3;
        Value<double[]> vectorValue = new Value<>(doubles);
        assertNotNull(vectorValue.getUnderlyingObject());
        assertEquals(doubles, vectorValue.getUnderlyingObject());
        assertNotNull(vectorValue.asString());
        assertEquals("{0.1,0.2,0.3}", vectorValue.asString());
        assertEquals(Double.NaN, vectorValue.asNumber());
        double[] vector = vectorValue.asVector();
        assertNotNull(vector);
        assertArrayEquals(doubles, vector);
    }

    @Test
    void testVectorStringValue() {
        double[] doubles = new double[3];
        doubles[0] = 0.1;
        doubles[1] = 0.2;
        doubles[2] = 0.3;
        String vectorString = Arrays.toString(doubles);
        Value<String> vectorValue = new Value<>(vectorString);
        assertNotNull(vectorValue.getUnderlyingObject());
        assertEquals(vectorString, vectorValue.getUnderlyingObject());
        assertNotNull(vectorValue.asString());
        assertEquals(vectorString, vectorValue.asString());
        assertEquals(Double.NaN, vectorValue.asNumber());
        double[] vector = vectorValue.asVector();
        assertNotNull(vector);
        assertArrayEquals(doubles, vector);
    }

    @Test
    void testVectorBytesValue() {
        double[] doubles = new double[3];
        doubles[0] = 0.1;
        doubles[1] = 0.2;
        doubles[2] = 0.3;
        String vectorString = Arrays.toString(doubles);
        ByteBuffer byteBuffer = ByteBuffer.wrap(vectorString.getBytes());
        Value<ByteBuffer> vectorValue = new Value<>(byteBuffer);
        assertNotNull(vectorValue.getUnderlyingObject());
        assertEquals(byteBuffer, vectorValue.getUnderlyingObject());
        assertNotNull(vectorValue.asString());
        assertEquals(vectorString, vectorValue.asString());
        assertEquals(Double.NaN, vectorValue.asNumber());
        double[] vector = vectorValue.asVector();
        assertNotNull(vector);
        assertArrayEquals(doubles, vector);
    }

    @Test
    void testStringVectorValue() {
        String vectorString = "0.1 0.2 0.3";
        Value<String> vectorValue = new Value<>(vectorString);
        assertNotNull(vectorValue.getUnderlyingObject());
        assertEquals(vectorString, vectorValue.getUnderlyingObject());
        assertNotNull(vectorValue.asString());
        assertEquals(vectorString, vectorValue.asString());
        assertEquals(Double.NaN, vectorValue.asNumber());
        double[] vector = vectorValue.asVector();
        assertNotNull(vector);
        assertArrayEquals(new double[] { 0.1, 0.2, 0.3 }, vector);
    }

    @Test
    void testStringVectorValueWithTabs() {
        String vectorString = "0.1  0.2 0.3";
        Value<String> vectorValue = new Value<>(vectorString);
        assertNotNull(vectorValue.getUnderlyingObject());
        assertEquals(vectorString, vectorValue.getUnderlyingObject());
        assertNotNull(vectorValue.asString());
        assertEquals(vectorString, vectorValue.asString());
        assertEquals(Double.NaN, vectorValue.asNumber());
        double[] vector = vectorValue.asVector();
        assertNotNull(vector);
        assertArrayEquals(new double[] { 0.1, 0.2, 0.3 }, vector);
    }
}