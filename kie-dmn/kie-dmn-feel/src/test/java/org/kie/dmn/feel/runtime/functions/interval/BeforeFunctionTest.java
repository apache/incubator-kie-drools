package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.math.BigDecimal;

public class BeforeFunctionTest {

    private BeforeFunction beforeFunction;

    @Before
    public void setUp() {
        beforeFunction = BeforeFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(beforeFunction.invoke((Comparable) null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(beforeFunction.invoke("a", (Comparable) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( beforeFunction.invoke("a", BigDecimal.valueOf(2) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamSingles() {
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a", "b" ), Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "b", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( BigDecimal.valueOf(2), BigDecimal.valueOf(1) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(2) ), Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(1) ), Boolean.FALSE );
    }

    @Test
    public void invokeParamSingleAndRange() {
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "f",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "b", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
    }

    @Test
    public void invokeParamRangeAndSingle() {
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "f" ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "a"),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ),
                "f" ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "g" ),
                Boolean.TRUE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "g", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.OPEN, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
    }

}