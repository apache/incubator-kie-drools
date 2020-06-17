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

package org.optaplanner.core.impl.domain.valuerange.buildin.primdouble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class DoubleValueRangeTest {

    @Test
    public void contains() {
        assertThat(new DoubleValueRange(0.0, 10.0).contains(3.0)).isTrue();
        assertThat(new DoubleValueRange(0.0, 10.0).contains(10.0)).isFalse();
        assertThat(new DoubleValueRange(0.0, 10.0).contains(null)).isFalse();
        assertThat(new DoubleValueRange(100.0, 120.0).contains(100.0)).isTrue();
        assertThat(new DoubleValueRange(100.0, 120.0).contains(99.9)).isFalse();
        assertThat(new DoubleValueRange(-5.3, 25.2).contains(-5.2)).isTrue();
        assertThat(new DoubleValueRange(-5.3, 25.2).contains(-5.4)).isFalse();
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextDouble()).thenReturn(0.3, 0.0);
        assertElementsOfIterator(new DoubleValueRange(0.0, 1.0).createRandomIterator(workingRandom), 0.3, 0.0);
        when(workingRandom.nextDouble()).thenReturn(0.3, 0.0);
        assertElementsOfIterator(new DoubleValueRange(100.0, 104.0).createRandomIterator(workingRandom), 101.2, 100.0);
        when(workingRandom.nextDouble()).thenReturn(0.3, 0.0);
        assertElementsOfIterator(new DoubleValueRange(-5.0, 5.0).createRandomIterator(workingRandom), -2.0, -5.0);
        assertAllElementsOfIterator(new DoubleValueRange(7.0, 7.0).createRandomIterator(workingRandom));
        when(workingRandom.nextDouble()).thenReturn(Math.nextAfter(1.0, Double.NEGATIVE_INFINITY));
        assertElementsOfIterator(new DoubleValueRange(0.000001, 0.000002).createRandomIterator(workingRandom),
                Math.nextAfter(0.000002, Double.NEGATIVE_INFINITY));
        when(workingRandom.nextDouble()).thenReturn(Math.nextAfter(1.0, Double.NEGATIVE_INFINITY));
        assertElementsOfIterator(new DoubleValueRange(1000000.0, 2000000.0).createRandomIterator(workingRandom),
                Math.nextAfter(2000000.0, Double.NEGATIVE_INFINITY));
    }

}
