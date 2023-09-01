package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SubstringFunctionTest {

    private SubstringFunction substringFunction;

    @Before
    public void setUp() {
        substringFunction = new SubstringFunction();
    }

    @Test
    public void invokeNull2ParamsMethod() {
        FunctionTestUtil.assertResultError(substringFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0), InvalidParametersEvent.class);
    }

    @Test
    public void invokeNull3ParamsMethod() {
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null, 2), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, null, 2), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0, 2), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartZero() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 0), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 0, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartOutOfListBounds() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 10, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", -10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", -10, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthNegative() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 1, -3), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthOutOfListBounds() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 3), "est");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -3, 3), "est");
    }

    @Test
    public void invokeStartPositive() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 1), "test");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2), "est");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 4), "t");

        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 1), "e");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 2), "es");
    }

    @Test
    public void invokeStartNegative() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -1), "t");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -2), "st");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -4), "test");

        FunctionTestUtil.assertResult(substringFunction.invoke("test", -2, 1), "s");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -3, 2), "es");
    }
}