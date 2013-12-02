/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.api.domain.value.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.api.domain.value.buildin.collection.ListValueRange;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class CompositeValueRangeTest {

    private <T> CompositeValueRange<T> createCompositeValueRange(List<T>... lists) {
        List<ListValueRange<T>> childValueRangeList = new ArrayList<ListValueRange<T>>(lists.length);
        for (List<T> list : lists) {
            childValueRangeList.add(new ListValueRange<T>(list));
        }
        return new CompositeValueRange<T>(childValueRangeList);
    }

    @Test
    public void getSize() {
        assertEquals(7L, createCompositeValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).getSize());
        assertEquals(4L, createCompositeValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).getSize());
        assertEquals(0L, createCompositeValueRange(Collections.<String>emptyList(), Collections.<String>emptyList()).getSize());
    }

    @Test
    public void get() {
        assertEquals(5, createCompositeValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).get(2L).intValue());
        assertEquals(-15, createCompositeValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).get(4L).intValue());
        assertEquals(-1, createCompositeValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).get(6L).intValue());
        assertEquals("c", createCompositeValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).get(2L));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(createCompositeValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1))
                .createOriginalIterator(), 0, 2, 5, 10, -15, 25, -1);
        assertAllElementsOfIterator(createCompositeValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d"))
                .createOriginalIterator(), "a", "b", "c", "d");
        assertAllElementsOfIterator(createCompositeValueRange(Collections.<String>emptyList(), Collections.<String>emptyList())
                .createOriginalIterator());
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0, 3, 0, 3, 0, 2, 0);

        assertElementsOfIterator(createCompositeValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1))
                .createRandomIterator(workingRandom), 10, 0);
        assertElementsOfIterator(createCompositeValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d"))
                .createRandomIterator(workingRandom), "d", "a");
        assertElementsOfIterator(createCompositeValueRange(Collections.<String>emptyList(), Collections.<String>emptyList())
                .createRandomIterator(workingRandom));
    }

}
