package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class NotFunctionTest {

    private NotFunction notFunction;

    @Before
    public void setUp() {
        notFunction = new NotFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultNull(notFunction.invoke(null));
    }

    @Test
    public void invokeWrongType() {
        FunctionTestUtil.assertResultError(notFunction.invoke(1), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(notFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(notFunction.invoke(BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTrue() {
        FunctionTestUtil.assertResult(notFunction.invoke(true), false);
    }

    @Test
    public void invokeFalse() {
        FunctionTestUtil.assertResult(notFunction.invoke(false), true);
    }
}