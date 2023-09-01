package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class FloorFunctionTest {

    private FloorFunction floorFunction;

    @Before
    public void setUp() {
        floorFunction = new FloorFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(floorFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeZero() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.ZERO), BigDecimal.ZERO);
    }

    @Test
    public void invokePositive() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.valueOf(10.2)), BigDecimal.valueOf(10));
    }

    @Test
    public void invokeNegative() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.valueOf(-10.2)), BigDecimal.valueOf(-11));
    }

}