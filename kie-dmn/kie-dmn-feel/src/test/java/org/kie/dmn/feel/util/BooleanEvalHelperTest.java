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
package org.kie.dmn.feel.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanEvalHelperTest {


    @Test
    void numericValuesComparative() {
        assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), BigDecimal.valueOf(2), (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(BooleanEvalHelper.compare(1.0, 2.0, (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(BooleanEvalHelper.compare(1, 2, (l, r) -> l.compareTo(r) > 0)).isFalse();
        assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), 2, (l, r) -> l.compareTo(r) > 0)).isFalse();
        assertThat(BooleanEvalHelper.compare(1, BigDecimal.valueOf(2), (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), 2.3,  (l, r) -> l.compareTo(r) == 0)).isFalse();
        assertThat(BooleanEvalHelper.compare(1.2, BigDecimal.valueOf(1.2),  (l, r) -> l.compareTo(r) == 0)).isTrue();
        assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), 0L,  (l, r) -> l.compareTo(r) > 0)).isTrue();
        assertThat(BooleanEvalHelper.compare(10L, BigDecimal.valueOf(2),  (l, r) -> l.compareTo(r) < 0)).isFalse();
        assertThat(BooleanEvalHelper.compare(BigInteger.valueOf(1), BigInteger.valueOf(2), (l, r) -> l.compareTo(r) == 0)).isFalse();
        assertThat(BooleanEvalHelper.compare(BigInteger.valueOf(1), 2,  (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(BooleanEvalHelper.compare(BigInteger.valueOf(1), 2.3,  (l, r) -> l.compareTo(r) == 0)).isFalse();
    }

}
