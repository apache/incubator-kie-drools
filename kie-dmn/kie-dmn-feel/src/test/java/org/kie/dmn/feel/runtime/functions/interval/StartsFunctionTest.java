package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class StartsFunctionTest {

    private StartsFunction startsFunction;

    @Before
    public void setUp() {
        startsFunction = StartsFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(startsFunction.invoke((Comparable) null, new RangeImpl()), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(startsFunction.invoke("a", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( startsFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED,  1, 2, Range.RangeBoundary.CLOSED ) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamSingleAndRange() {
        FunctionTestUtil.assertResult( startsFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( startsFunction.invoke( "f",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( startsFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( startsFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "b", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( startsFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( startsFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( startsFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( startsFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
    }

}