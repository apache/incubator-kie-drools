package org.optaplanner.core.api.score.stream;

import java.util.Comparator;
import java.util.SortedMap;

import org.junit.Test;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

import static org.junit.Assert.assertEquals;

public class ConstraintCollectorsTest {

    private <A, B, C> Runnable accumulate(UniConstraintCollector<A, B, C> collector, B container, A value) {
        return collector.accumulator().apply(container, value);
    }

    private <A, B, C> void assertResult(UniConstraintCollector<A, B, C> collector, B container, C expectedResult) {
        C actualResult = collector.finisher().apply(container);
        assertEquals("Collector (" + collector + ") did not produced expected result.", expectedResult, actualResult);
    }

    @Test
    public void maxComparable() {
        UniConstraintCollector<Integer, SortedMap<Integer, Long>, Integer> collector = ConstraintCollectors.max();
        SortedMap<Integer, Long> container = collector.supplier().get();
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
        UniConstraintCollector<Class, SortedMap<Class, Long>, Class> collector =
                ConstraintCollectors.max(Comparator.comparing(Class::getCanonicalName));
        SortedMap<Class, Long> container = collector.supplier().get();
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

}
