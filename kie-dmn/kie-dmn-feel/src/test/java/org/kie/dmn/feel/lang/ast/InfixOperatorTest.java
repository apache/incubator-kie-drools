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
package org.kie.dmn.feel.lang.ast;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InfixOperatorTest {

    @Test
    void addLocalDateAndDuration() {
        LocalDate left = LocalDate.of(2021, 1, 1);
        Duration right = Duration.of(-1, ChronoUnit.HOURS);
        LocalDate retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 31));
        right = Duration.of(-24, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 31));
        right = Duration.of(-25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 30));
        right = Duration.of(1, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 1));

        left = LocalDate.of(2021, 1, 2);
        right = Duration.of(1, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 2));
        right = Duration.of(24, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 3));
        right = Duration.of(25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 3));

        left = LocalDate.of(2021, 1, 3);
        right = Duration.of(25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 4));

        left = LocalDate.of(2020, 12, 30);
        right = Duration.of(-25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 28));

        left = LocalDate.of(2020, 12, 31);
        right = Duration.of(-1, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.ADD.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 30));
    }

    @Test
    void subLocalDateAndDuration() {
        LocalDate left = LocalDate.of(2021, 1, 1);
        Duration right = Duration.of(-1, ChronoUnit.HOURS);
        LocalDate retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 1));
        right = Duration.of(-24, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 2));
        right = Duration.of(-25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 2));
        right = Duration.of(1, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 31));

        left = LocalDate.of(2021, 1, 2);
        right = Duration.of(1, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 1));
        right = Duration.of(24, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 1));
        right = Duration.of(25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 31));

        left = LocalDate.of(2021, 1, 3);
        right = Duration.of(25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2021, 1, 1));

        left = LocalDate.of(2020, 12, 30);
        right = Duration.of(-25, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 31));

        left = LocalDate.of(2020, 12, 31);
        right = Duration.of(-1, ChronoUnit.HOURS);
        retrieved = (LocalDate) InfixOperator.SUB.evaluate(left, right, null);
        assertThat(retrieved).isEqualTo(LocalDate.of(2020, 12, 31));
    }

    @Test
    void mulDurationAndDuration() {
        Object left = Duration.of(5, DAYS);
        Object right = Duration.of(5, DAYS);
        assertThat(InfixOperator.MULT.evaluate(left, right, mock(EvaluationContext.class))).isNull();
    }

}