package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.math.BigDecimal;

public class AfterFunctionTest {

    private AfterFunction afterFunction;

    @Before
    public void setUp() {
        afterFunction = AfterFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(afterFunction.invoke((Comparable) null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(afterFunction.invoke("a", (Comparable) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( afterFunction.invoke("a", BigDecimal.valueOf(2) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamSingles() {
        FunctionTestUtil.assertResult( afterFunction.invoke( "a", "b" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke( "a", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke( "b", "a" ), Boolean.TRUE );
        FunctionTestUtil.assertResult( afterFunction.invoke( BigDecimal.valueOf(2), BigDecimal.valueOf(1) ), Boolean.TRUE );
        FunctionTestUtil.assertResult( afterFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(2) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(1) ), Boolean.FALSE );
    }

    @Test
    public void invokeParamSingleAndRange() {
        FunctionTestUtil.assertResult( afterFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke( "f",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke( "f",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN )),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( afterFunction.invoke( "g",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
    }

    @Test
    public void invokeParamRangeAndSingle() {
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "f" ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "a"),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.CLOSED ),
                "a" ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "b", "f", Range.RangeBoundary.CLOSED ),
                "a" ),
                Boolean.TRUE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "g", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( afterFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ) ),
                Boolean.TRUE );
    }

}