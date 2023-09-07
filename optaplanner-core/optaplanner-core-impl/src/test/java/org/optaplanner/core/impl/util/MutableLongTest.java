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

class MutableLongTest {

    @Test
    void arithmetic() {
        MutableLong mutableLong = new MutableLong(1);
        long result = mutableLong.increment();
        assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(2);
            softly.assertThat(mutableLong.longValue()).isEqualTo(2L);
        });
        long result2 = mutableLong.decrement();
        assertSoftly(softly -> {
            softly.assertThat(result2).isEqualTo(1);
            softly.assertThat(mutableLong.longValue()).isEqualTo(1L);
        });
    }

    @Test
    void comparison() {
        MutableLong mutableLong1 = new MutableLong(1);
        MutableLong mutableLong2 = new MutableLong(2);
        assertSoftly(softly -> {
            softly.assertThat(mutableLong1)
                    .isEqualTo(mutableLong1);
            softly.assertThat(mutableLong1)
                    .isNotEqualTo(mutableLong2);
            softly.assertThat(mutableLong1)
                    .usingComparator(MutableLong::compareTo)
                    .isEqualByComparingTo(mutableLong1);
            softly.assertThat(mutableLong1)
                    .usingComparator(MutableLong::compareTo)
                    .isLessThan(mutableLong2);
            softly.assertThat(mutableLong2)
                    .usingComparator(MutableLong::compareTo)
                    .isGreaterThan(mutableLong1);
        });
    }

    @Test
    void values() {
        MutableLong mutableLong = new MutableLong(Long.MAX_VALUE);
        assertSoftly(softly -> {
            softly.assertThat(mutableLong.intValue()).isEqualTo(-1); // Cast.
            softly.assertThat(mutableLong.longValue()).isEqualTo(Long.MAX_VALUE);
            softly.assertThat(mutableLong.floatValue()).isEqualTo(Long.MAX_VALUE);
            softly.assertThat(mutableLong.doubleValue()).isEqualTo(Long.MAX_VALUE);
        });
    }

}
