package org.optaplanner.core.api.score.stream;

import java.util.Comparator;

import org.junit.Test;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

import static org.junit.Assert.assertEquals;

public class ConstraintCollectorsTest {

    // ************************************************************************
    // min
    // ************************************************************************

    @Test
    public void minComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.min();
        Object container = collector.supplier().get();
        // add first value, which becomes the min
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    public void minNotComparable() {
        UniConstraintCollector<Class, ?, Class> collector =
                ConstraintCollectors.min(Comparator.comparing(Class::getCanonicalName));
        Object container = collector.supplier().get();
        // add first value, which becomes the min
        Class firstValue = ConstraintCollectorsTest.class;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        Class secondValue = ConstraintCollectors.class;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    // ************************************************************************
    // max
    // ************************************************************************

    @Test
    public void maxComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.max();
        Object container = collector.supplier().get();
        // add first value, which becomes the max
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    public void maxNotComparable() {
        UniConstraintCollector<Class, ?, Class> collector =
                ConstraintCollectors.max(Comparator.comparing(Class::getCanonicalName));
        Object container = collector.supplier().get();
        // add first value, which becomes the max
        Class firstValue = ConstraintCollectorsTest.class;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        Class secondValue = ConstraintCollectors.class;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    private <A, B, C> Runnable accumulate(UniConstraintCollector<A, B, C> collector, Object container, A value) {
        return collector.accumulator().apply((B) container, value);
    }

    private <A, B, C> void assertResult(UniConstraintCollector<A, B, C> collector, Object container, C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertEquals("Collector (" + collector + ") did not produced expected result.", expectedResult, actualResult);
    }

}
