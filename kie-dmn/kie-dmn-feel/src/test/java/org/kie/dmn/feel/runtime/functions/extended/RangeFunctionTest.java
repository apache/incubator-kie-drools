package org.kie.dmn.feel.runtime.functions.extended;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.model.api.GwtIncompatible;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

@GwtIncompatible
public class RangeFunctionTest {

    private RangeFunction rangeFunction;

    @Before
    public void setUp() {
        rangeFunction = new RangeFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(rangeFunction.invoke(null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(rangeFunction.invoke(" "), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(rangeFunction.invoke(""), InvalidParametersEvent.class);
    }

    @Test
    public void invokeDifferentTypes() {
        FunctionTestUtil.assertResult(rangeFunction.invoke("[1..\"cheese\"]"), new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, "cheese", Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[1..date(\"1978-09-12\")]"), new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, LocalDate.of(1978, 9, 12), Range.RangeBoundary.CLOSED));
    }

    @Test
    public void invoke_OpenOpenBoundaries() {
        FunctionTestUtil.assertResult(rangeFunction.invoke("(1..2)"), new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(\"a\"..\"z\")"), new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(\"ab\"..\"yz\")"), new RangeImpl(Range.RangeBoundary.OPEN, "ab", "yz", Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(date(\"1978-09-12\")..date(\"1978-10-13\"))"),
                new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))"),
                new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(duration(\"P1Y6M\")..duration(\"P2Y6M\"))"),
                new RangeImpl(Range.RangeBoundary.OPEN,
                        new ComparablePeriod(Period.parse("P1Y6M")),
                        new ComparablePeriod(Period.parse("P2Y6M")),
                        Range.RangeBoundary.OPEN));
    }

    @Test
    public void invoke_OpenClosedBoundaries() {
        FunctionTestUtil.assertResult(rangeFunction.invoke("(1..2]"), new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(\"a\"..\"z\"]"), new RangeImpl(Range.RangeBoundary.OPEN, "a", "z", Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(date(\"1978-09-12\")..date(\"1978-10-13\")]"),
                new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("(duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]"),
                new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED));
    }

    @Test
    public void invoke_ClosedOpenBoundaries() {
        FunctionTestUtil.assertResult(rangeFunction.invoke("[1..2)"), new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[\"a\"..\"z\")"), new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[date(\"1978-09-12\")..date(\"1978-10-13\"))"),
                new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.OPEN));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\"))"),
                new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.OPEN));
    }

    @Test
    public void invoke_ClosedClosedBoundaries() {
        FunctionTestUtil.assertResult(rangeFunction.invoke("[1..2]"), new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.ONE, BigDecimal.valueOf(2), Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[2..1]"), new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.valueOf(2), BigDecimal.ONE, Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[\"a\"..\"z\"]"), new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[date(\"1978-09-12\")..date(\"1978-10-13\")]"),
                new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(1978, 9, 12), LocalDate.of(1978, 10, 13), Range.RangeBoundary.CLOSED));
        FunctionTestUtil.assertResult(rangeFunction.invoke("[duration(\"P2DT20H14M\")..duration(\"P3DT20H14M\")]"),
                new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED));
    }
}