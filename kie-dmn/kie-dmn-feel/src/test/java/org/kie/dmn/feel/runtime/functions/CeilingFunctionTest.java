package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class CeilingFunctionTest {

    private CeilingFunction ceilingFunction;

    @Before
    public void setUp() {
        ceilingFunction = new CeilingFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(ceilingFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeZero() {
        FunctionTestUtil.assertResultBigDecimal(ceilingFunction.invoke(BigDecimal.ZERO), BigDecimal.ZERO);
    }

    @Test
    public void invokePositive() {
        FunctionTestUtil.assertResultBigDecimal(ceilingFunction.invoke(BigDecimal.valueOf(10.2)), BigDecimal.valueOf(11));
    }

    @Test
    public void invokeNegative() {
        FunctionTestUtil.assertResultBigDecimal(ceilingFunction.invoke(BigDecimal.valueOf(-10.2)), BigDecimal.valueOf(-10));
    }
}