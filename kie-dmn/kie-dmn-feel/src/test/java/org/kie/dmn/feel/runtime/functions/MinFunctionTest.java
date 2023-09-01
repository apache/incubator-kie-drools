package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class MinFunctionTest {

    private MinFunction minFunction;

    @Before
    public void setUp() {
        minFunction = new MinFunction();
    }

    @Test
    public void invokeNullList() {
        FunctionTestUtil.assertResultError(minFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyList() {
        FunctionTestUtil.assertResultError(minFunction.invoke(Collections.emptyList()), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(10.2))), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList(1)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(1, 2, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, 1, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, 3, 1)), 1);
    }

    @Test
    public void invokeListOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList("a")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("a", "b", "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "a", "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "c", "a")), "a");
    }

    @Test
    public void invokeListOfChronoPeriods() {
        final ChronoPeriod p1Period = Period.parse("P1Y");
        final ChronoPeriod p1Comparable = ComparablePeriod.parse("P1Y");
        final ChronoPeriod p2Period = Period.parse("P1M");
        final ChronoPeriod p2Comparable = ComparablePeriod.parse("P1M");
        Predicate<ChronoPeriod> assertion = i -> i.get(ChronoUnit.YEARS) == 0 && i.get(ChronoUnit.MONTHS) == 1;
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Collections.singletonList(p2Period)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Collections.singletonList(p2Comparable)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Period, p2Period)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Comparable, p2Period)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Period, p2Comparable)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Comparable, p2Comparable)), ChronoPeriod.class, assertion);
    }

    @Test
    public void invokeNullArray() {
        FunctionTestUtil.assertResultError(minFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyArray() {
        FunctionTestUtil.assertResultError(minFunction.invoke(new Object[]{}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(new Object[]{1, "test", BigDecimal.valueOf(10.2)}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1, 2, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 1, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 3, 1}), 1);
    }

    @Test
    public void invokeArrayOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a", "b", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "a", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "c", "a"}), "a");
    }

    @Test
    public void invokeArrayOfChronoPeriods() {
        final ChronoPeriod p1Period = Period.parse("P1Y");
        final ChronoPeriod p1Comparable = ComparablePeriod.parse("P1Y");
        final ChronoPeriod p2Period = Period.parse("P1M");
        final ChronoPeriod p2Comparable = ComparablePeriod.parse("P1M");
        Predicate<ChronoPeriod> assertion = i -> i.get(ChronoUnit.YEARS) == 0 && i.get(ChronoUnit.MONTHS) == 1;
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p2Period}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p2Comparable}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Period, p2Period}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Comparable, p2Period}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Period, p2Comparable}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Comparable, p2Comparable}), ChronoPeriod.class, assertion);
    }
}