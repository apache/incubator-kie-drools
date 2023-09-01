package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class DuringFunctionTest {

    private DuringFunction duringFunction;

    @Before
    public void setUp() {
        duringFunction = DuringFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(duringFunction.invoke((Comparable) null, new RangeImpl()), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(duringFunction.invoke("a", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( duringFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED,  1, 2, Range.RangeBoundary.CLOSED ) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamSingleAndRange() {
        FunctionTestUtil.assertResult( duringFunction.invoke( "c",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( duringFunction.invoke( "f",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( duringFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( duringFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "b", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( duringFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( duringFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "c", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( duringFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "c", "d", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( duringFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
    }

}