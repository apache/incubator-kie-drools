/*
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

package org.optaplanner.core.impl.util;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class MutableIntTest {

    @Test
    void arithmetic() {
        MutableInt mutableInt = new MutableInt(1);
        int result = mutableInt.increment();
        assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(2);
            softly.assertThat(mutableInt.intValue()).isEqualTo(2);
        });
        int result2 = mutableInt.decrement();
        assertSoftly(softly -> {
            softly.assertThat(result2).isEqualTo(1);
            softly.assertThat(mutableInt.intValue()).isEqualTo(1);
        });
    }

    @Test
    void comparison() {
        MutableInt mutableInt1 = new MutableInt(1);
        MutableInt mutableInt2 = new MutableInt(2);
        assertSoftly(softly -> {
            softly.assertThat(mutableInt1)
                    .isEqualTo(mutableInt1);
            softly.assertThat(mutableInt1)
                    .isNotEqualTo(mutableInt2);
            softly.assertThat(mutableInt1)
                    .usingComparator(MutableInt::compareTo)
                    .isEqualByComparingTo(mutableInt1);
            softly.assertThat(mutableInt1)
                    .usingComparator(MutableInt::compareTo)
                    .isLessThan(mutableInt2);
            softly.assertThat(mutableInt2)
                    .usingComparator(MutableInt::compareTo)
                    .isGreaterThan(mutableInt1);
        });
    }

    @Test
    void values() {
        MutableInt mutableInt = new MutableInt(Integer.MAX_VALUE);
        assertSoftly(softly -> {
            softly.assertThat(mutableInt.intValue()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(mutableInt.longValue()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(mutableInt.floatValue()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(mutableInt.doubleValue()).isEqualTo(Integer.MAX_VALUE);
        });
    }
}
