package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class EndsWithFunctionTest {

    private EndsWithFunction endsWithFunction;

    @Before
    public void setUp() {
        endsWithFunction = new EndsWithFunction();
    }

    @Test
    public void invokeParamsNull() {
        FunctionTestUtil.assertResultError(endsWithFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(endsWithFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(endsWithFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEndsWith() {
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "t"), true);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "st"), true);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "est"), true);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "test"), true);
    }

    @Test
    public void invokeNotEndsWith() {
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "es"), false);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "ttttt"), false);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "estt"), false);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "tt"), false);
    }
}