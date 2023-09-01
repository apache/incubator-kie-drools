package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class IncludesFunctionTest {

    private IncludesFunction includesFunction;

    @Before
    public void setUp() {
        includesFunction = IncludesFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(includesFunction.invoke(null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(includesFunction.invoke(new RangeImpl(), (Comparable) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED,  1, 2, Range.RangeBoundary.CLOSED ) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamRangeAndSingle() {
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "c" ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "a"),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ),
                "f" ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "g" ),
                Boolean.FALSE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "c", "d", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "c", "d", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( includesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
    }

}