/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.valuerange.buildin.biginteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class BigIntegerValueRangeTest {

    @Test
    public void getSize() {
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).getSize()).isEqualTo(10L);
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).getSize()).isEqualTo(20L);
        assertThat(new BigIntegerValueRange(new BigInteger("-15"), new BigInteger("25")).getSize()).isEqualTo(40L);
        assertThat(new BigIntegerValueRange(new BigInteger("7"), new BigInteger("7")).getSize()).isEqualTo(0L);
        // IncrementUnit
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).getSize())
                .isEqualTo(5L);
        assertThat(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).getSize())
                .isEqualTo(5L);
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).getSize())
                .isEqualTo(4L);
    }

    @Test
    public void get() {
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).get(3L)).isEqualTo(new BigInteger("3"));
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).get(3L))
                .isEqualTo(new BigInteger("103"));
        assertThat(new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).get(1L))
                .isEqualTo(new BigInteger("-4"));
        assertThat(new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).get(6L)).isEqualTo(new BigInteger("1"));
        // IncrementUnit
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2")).get(3L))
                .isEqualTo(new BigInteger("6"));
        assertThat(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2")).get(3L))
                .isEqualTo(new BigInteger("5"));
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5")).get(3L))
                .isEqualTo(new BigInteger("115"));
    }

    @Test
    public void contains() {
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).contains(new BigInteger("3"))).isTrue();
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).contains(new BigInteger("10")))
                .isFalse();
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10")).contains(null)).isFalse();
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).contains(new BigInteger("100")))
                .isTrue();
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120")).contains(new BigInteger("99")))
                .isFalse();
        assertThat(new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).contains(new BigInteger("-4")))
                .isTrue();
        assertThat(new BigIntegerValueRange(new BigInteger("-5"), new BigInteger("25")).contains(new BigInteger("-20")))
                .isFalse();
        // IncrementUnit
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2"))
                .contains(new BigInteger("2"))).isTrue();
        assertThat(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2"))
                .contains(new BigInteger("3"))).isFalse();
        assertThat(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2"))
                .contains(new BigInteger("1"))).isTrue();
        assertThat(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2"))
                .contains(new BigInteger("2"))).isFalse();
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5"))
                .contains(new BigInteger("115"))).isTrue();
        assertThat(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5"))
                .contains(new BigInteger("114"))).isFalse();
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("4"))
                .createOriginalIterator(), new BigInteger("0"), new BigInteger("1"), new BigInteger("2"),
                new BigInteger("3"));
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("104"))
                .createOriginalIterator(), new BigInteger("100"), new BigInteger("101"), new BigInteger("102"),
                new BigInteger("103"));
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("-4"), new BigInteger("3"))
                .createOriginalIterator(), new BigInteger("-4"), new BigInteger("-3"), new BigInteger("-2"),
                new BigInteger("-1"), new BigInteger("0"), new BigInteger("1"), new BigInteger("2"));
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("7"), new BigInteger("7"))
                .createOriginalIterator());
        // IncrementUnit
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2"))
                .createOriginalIterator(), new BigInteger("0"), new BigInteger("2"), new BigInteger("4"),
                new BigInteger("6"), new BigInteger("8"));
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2"))
                .createOriginalIterator(), new BigInteger("-1"), new BigInteger("1"), new BigInteger("3"),
                new BigInteger("5"), new BigInteger("7"));
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5"))
                .createOriginalIterator(), new BigInteger("100"), new BigInteger("105"), new BigInteger("110"),
                new BigInteger("115"));
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("7"))
                .createRandomIterator(workingRandom), new BigInteger("3"), new BigInteger("0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("104"))
                .createRandomIterator(workingRandom), new BigInteger("103"), new BigInteger("100"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("-4"), new BigInteger("3"))
                .createRandomIterator(workingRandom), new BigInteger("-1"), new BigInteger("-4"));
        assertAllElementsOfIterator(new BigIntegerValueRange(new BigInteger("7"), new BigInteger("7"))
                .createRandomIterator(workingRandom));
        // IncrementUnit
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("0"), new BigInteger("10"), new BigInteger("2"))
                .createRandomIterator(workingRandom), new BigInteger("6"), new BigInteger("0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("-1"), new BigInteger("9"), new BigInteger("2"))
                .createRandomIterator(workingRandom), new BigInteger("5"), new BigInteger("-1"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigIntegerValueRange(new BigInteger("100"), new BigInteger("120"), new BigInteger("5"))
                .createRandomIterator(workingRandom), new BigInteger("115"), new BigInteger("100"));
    }

}
