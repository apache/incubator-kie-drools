package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.math.BigDecimal;

public class CoincidesFunctionTest {

    private CoincidesFunction coincidesFunction;

    @Before
    public void setUp() {
        coincidesFunction = CoincidesFunction.INSTANCE;
    }

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(coincidesFunction.invoke(null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(coincidesFunction.invoke("a", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( coincidesFunction.invoke("a", BigDecimal.valueOf(2) ), InvalidParametersEvent.class );
    }

    @Test
    public void invokeParamSingles() {
        FunctionTestUtil.assertResult( coincidesFunction.invoke( "a", "b" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( "a", "a" ), Boolean.TRUE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( "b", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( BigDecimal.valueOf(2), BigDecimal.valueOf(1) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(2) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(1) ), Boolean.TRUE );
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.OPEN ),
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.OPEN ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "g", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ) ),
                Boolean.FALSE );
    }

}