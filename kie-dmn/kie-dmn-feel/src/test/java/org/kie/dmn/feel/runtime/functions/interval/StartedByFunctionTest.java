package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class StartedByFunctionTest {

    private StartedByFunction startedByFunction;

    @Before
    public void setUp() {
        startedByFunction = StartedByFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(startedByFunction.invoke(null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(startedByFunction.invoke(new RangeImpl(), (Comparable) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED,  1, 2, Range.RangeBoundary.CLOSED ) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamRangeAndSingle() {
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "f" ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "a"),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.OPEN ),
                "a" ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "g" ),
                Boolean.FALSE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( startedByFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
    }

}