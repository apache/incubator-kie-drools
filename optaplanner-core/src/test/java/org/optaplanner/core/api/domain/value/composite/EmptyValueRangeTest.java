package org.optaplanner.core.api.domain.value.composite;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.api.domain.value.buildin.collection.ListValueRange;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class EmptyValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(0L, new EmptyValueRange<Integer>().getSize());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get() {
        new EmptyValueRange<Integer>().get(0L);
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new EmptyValueRange<Integer>().createOriginalIterator());
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        assertElementsOfIterator(new EmptyValueRange<Integer>().createRandomIterator(workingRandom));
    }

}
