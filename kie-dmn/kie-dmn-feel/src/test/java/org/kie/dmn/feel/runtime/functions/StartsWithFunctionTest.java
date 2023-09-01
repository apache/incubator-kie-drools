package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class StartsWithFunctionTest {

    private StartsWithFunction startsWithFunction;

    @Before
    public void setUp() {
        startsWithFunction = new StartsWithFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(startsWithFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(startsWithFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(startsWithFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyString() {
        FunctionTestUtil.assertResult(startsWithFunction.invoke("", ""), true);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("", "test"), false);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", ""), true);
    }

    @Test
    public void invokeStartsWith() {
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "te"), true);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "t"), true);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "test"), true);
    }

    @Test
    public void invokeNotStartsWith() {
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "tte"), false);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "tt"), false);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "ttest"), false);
    }
}