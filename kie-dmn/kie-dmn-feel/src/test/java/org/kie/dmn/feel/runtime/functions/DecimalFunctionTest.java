package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DecimalFunctionTest {

    private DecimalFunction decimalFunction;

    @Before
    public void setUp() {
        decimalFunction = new DecimalFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(decimalFunction.invoke((BigDecimal) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(decimalFunction.invoke(BigDecimal.ONE, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(decimalFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
    }

    @Test
    public void invokeRoundingUp() {
        FunctionTestUtil.assertResult(decimalFunction.invoke(BigDecimal.valueOf(10.27), BigDecimal.ONE), BigDecimal.valueOf(10.3));
    }

    @Test
    public void invokeRoundingDown() {
        FunctionTestUtil.assertResult(decimalFunction.invoke(BigDecimal.valueOf(10.24), BigDecimal.ONE), BigDecimal.valueOf(10.2));
    }

    @Test
    public void invokeRoundingEven() {
        FunctionTestUtil.assertResult(decimalFunction.invoke(BigDecimal.valueOf(10.25), BigDecimal.ONE), BigDecimal.valueOf(10.2));
    }

    @Test
    public void invokeRoundingOdd() {
        FunctionTestUtil.assertResult(decimalFunction.invoke(BigDecimal.valueOf(10.35), BigDecimal.ONE), BigDecimal.valueOf(10.4));
    }

    @Test
    public void invokeLargerScale() {
        FunctionTestUtil.assertResult(decimalFunction.invoke(BigDecimal.valueOf(10.123456789), BigDecimal.valueOf(6)), BigDecimal.valueOf(10.123457));
    }
}