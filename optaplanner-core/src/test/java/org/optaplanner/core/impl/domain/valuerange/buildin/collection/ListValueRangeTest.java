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

package org.optaplanner.core.impl.domain.valuerange.buildin.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class ListValueRangeTest {

    @Test
    public void getSize() {
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).getSize()).isEqualTo(4L);
        assertThat(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).getSize()).isEqualTo(5L);
        assertThat(new ListValueRange<>(Arrays.asList(-15, 25, 0)).getSize()).isEqualTo(3L);
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a")).getSize()).isEqualTo(3L);
        assertThat(new ListValueRange<>(Collections.emptyList()).getSize()).isEqualTo(0L);
    }

    @Test
    public void get() {
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).get(2L).intValue()).isEqualTo(5);
        assertThat(new ListValueRange<>(Arrays.asList(100, -120)).get(1L).intValue()).isEqualTo(-120);
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d")).get(3L)).isEqualTo("c");
    }

    @Test
    public void contains() {
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(5)).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(4)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(null)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).contains(7)).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).contains(9)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList(-15, 25, 0)).contains(-15)).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList(-15, 25, 0)).contains(-14)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a")).contains("a")).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a")).contains("n")).isFalse();
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).createOriginalIterator(), 0, 2, 5, 10);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).createOriginalIterator(), 100, 120,
                5, 7, 8);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(-15, 25, 0)).createOriginalIterator(), -15, 25, 0);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList("b", "z", "a")).createOriginalIterator(), "b", "z", "a");
        assertAllElementsOfIterator(new ListValueRange<>(Collections.emptyList()).createOriginalIterator());
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).createRandomIterator(workingRandom), 5, 0);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).createRandomIterator(workingRandom), 5,
                100);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(-15, 25, 0)).createRandomIterator(workingRandom), 0, -15);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList("b", "z", "a")).createRandomIterator(workingRandom), "a",
                "b");
        assertAllElementsOfIterator(new ListValueRange<>(Collections.emptyList()).createRandomIterator(workingRandom));
    }

}
