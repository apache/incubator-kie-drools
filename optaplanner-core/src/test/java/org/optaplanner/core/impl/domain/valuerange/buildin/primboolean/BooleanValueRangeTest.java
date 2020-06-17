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

package org.optaplanner.core.impl.domain.valuerange.buildin.primboolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class BooleanValueRangeTest {

    @Test
    public void getSize() {
        assertThat(new BooleanValueRange().getSize()).isEqualTo(2L);
    }

    @Test
    public void get() {
        assertThat(new BooleanValueRange().get(0L)).isEqualTo(Boolean.FALSE);
        assertThat(new BooleanValueRange().get(1L)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void contains() {
        assertThat(new BooleanValueRange().contains(Boolean.FALSE)).isTrue();
        assertThat(new BooleanValueRange().contains(Boolean.TRUE)).isTrue();
        assertThat(new BooleanValueRange().contains(null)).isFalse();
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new BooleanValueRange().createOriginalIterator(), Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);

        when(workingRandom.nextBoolean()).thenReturn(true, true, false, true);
        assertElementsOfIterator(new BooleanValueRange().createRandomIterator(workingRandom),
                Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void getIndexNegative() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> new BooleanValueRange().get(-1));
    }

    @Test
    public void getIndexGreaterThanSize() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> new BooleanValueRange().get(2));
    }

}
