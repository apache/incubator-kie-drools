package org.optaplanner.core.impl.domain.valuerange.buildin.primdouble;

import java.util.Random;

import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class DoubleValueRangeTest {

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
