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
package org.kie.dmn.feel.lang.ast.forexpressioniterators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForIterationTest {

    @Test
    void hasNextValueBigDecimalTest() {
        BigDecimal start = BigDecimal.valueOf(1);
        BigDecimal end = BigDecimal.valueOf(3);
        ForIteration iteration = new ForIteration("iteration", start, end);
        assertTrue(iteration.hasNextValue());
        BigDecimal next = (BigDecimal) iteration.getNextValue();
        while (!next.equals(end)) {
            assertTrue(iteration.hasNextValue());
            next = (BigDecimal) iteration.getNextValue();
        }
        assertFalse(iteration.hasNextValue());
    }

    @Test
    void hasNextValueLocalDateTest() {
        LocalDate start = LocalDate.of(2021, 1, 1);
        LocalDate end = LocalDate.of(2021, 1, 3);
        ForIteration iteration = new ForIteration("iteration", start, end);
        assertTrue(iteration.hasNextValue());
        LocalDate next = (LocalDate) iteration.getNextValue();
        while (!next.equals(end)) {
            assertTrue(iteration.hasNextValue());
            next = (LocalDate) iteration.getNextValue();
        }
        assertFalse(iteration.hasNextValue());
    }

    @Test
    void getNextValueBigDecimalTest() {
        BigDecimal start = BigDecimal.valueOf(1);
        BigDecimal end = BigDecimal.valueOf(3);
        List<BigDecimal> expected = Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3));
        ForIteration iteration = new ForIteration("iteration", start, end);
        iteration.hasNextValue();
        IntStream.range(0, 3).forEach(i -> assertEquals(expected.get(i), iteration.getNextValue()));
    }

    @Test
    void getNextValueLocalDateTest() {
        LocalDate start = LocalDate.of(2021, 1, 3);
        LocalDate end = LocalDate.of(2021, 1, 1);
        List<LocalDate> expected = Arrays.asList(LocalDate.of(2021, 1, 3), LocalDate.of(2021, 1, 2), LocalDate.of(2021, 1, 1));
        ForIteration iteration = new ForIteration("iteration", start, end);
        iteration.hasNextValue();
        IntStream.range(0, 3).forEach(i -> assertEquals(expected.get(i), iteration.getNextValue()));
    }
}