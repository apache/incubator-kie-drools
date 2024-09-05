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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ForIterationTest {

    @Test
    void hasNextValueBigDecimalTest() {
        BigDecimal start = BigDecimal.valueOf(1);
        BigDecimal end = BigDecimal.valueOf(3);
        ForIteration iteration = new ForIteration("iteration", start, end);
        assertThat(iteration.hasNextValue()).isTrue();
        BigDecimal next = (BigDecimal) iteration.getNextValue();
        while (!next.equals(end)) {
            assertThat(iteration.hasNextValue()).isTrue();
            next = (BigDecimal) iteration.getNextValue();
        }
        assertThat(iteration.hasNextValue()).isFalse();
    }

    @Test
    void hasNextValueLocalDateTest() {
        LocalDate start = LocalDate.of(2021, 1, 1);
        LocalDate end = LocalDate.of(2021, 1, 3);
        ForIteration iteration = new ForIteration("iteration", start, end);
        assertThat(iteration.hasNextValue()).isTrue();
        LocalDate next = (LocalDate) iteration.getNextValue();
        while (!next.equals(end)) {
            assertThat(iteration.hasNextValue()).isTrue();
            next = (LocalDate) iteration.getNextValue();
        }
        assertThat(iteration.hasNextValue()).isFalse();
    }

    @Test
    void getNextValueBigDecimalTest() {
        BigDecimal start = BigDecimal.valueOf(1);
        BigDecimal end = BigDecimal.valueOf(3);
        ForIteration iteration = new ForIteration("iteration", start, end);
        assertThat(iteration.hasNextValue()).isTrue();
        assertThat(iteration.getNextValue()).isEqualTo(BigDecimal.valueOf(1));
        assertThat(iteration.getNextValue()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(iteration.getNextValue()).isEqualTo(BigDecimal.valueOf(3));
    }

    @Test
    void getNextValueLocalDateTest() {
        LocalDate start = LocalDate.of(2021, 1, 3);
        LocalDate end = LocalDate.of(2021, 1, 1);
        ForIteration iteration = new ForIteration("iteration", start, end);
        assertThat(iteration.hasNextValue()).isTrue();
        assertThat(iteration.getNextValue()).isEqualTo(LocalDate.of(2021, 1, 3));
        assertThat(iteration.getNextValue()).isEqualTo(LocalDate.of(2021, 1, 2));
        assertThat(iteration.getNextValue()).isEqualTo(LocalDate.of(2021, 1, 1));
    }
}