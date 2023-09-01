package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class StringLengthFunctionTest {

    private StringLengthFunction stringLengthFunction;

    @Before
    public void setUp() {
        stringLengthFunction = new StringLengthFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(stringLengthFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyString() {
        FunctionTestUtil.assertResult(stringLengthFunction.invoke(""), BigDecimal.ZERO);
    }

    @Test
    public void invoke() {
        FunctionTestUtil.assertResult(stringLengthFunction.invoke("testString"), BigDecimal.TEN);
    }
}