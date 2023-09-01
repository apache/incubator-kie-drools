package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ContainsFunctionTest {

    private ContainsFunction containsFunction;

    @Before
    public void setUp() {
        containsFunction = new ContainsFunction();
    }

    @Test
    public void invokeParamsNull() {
        FunctionTestUtil.assertResultError(containsFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(containsFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(containsFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeContains() {
        FunctionTestUtil.assertResult(containsFunction.invoke("test", "es"), true);
        FunctionTestUtil.assertResult(containsFunction.invoke("test", "t"), true);
        FunctionTestUtil.assertResult(containsFunction.invoke("testy", "y"), true);
    }

    @Test
    public void invokeNotContains() {
        FunctionTestUtil.assertResult(containsFunction.invoke("test", "ex"), false);
        FunctionTestUtil.assertResult(containsFunction.invoke("test", "u"), false);
        FunctionTestUtil.assertResult(containsFunction.invoke("test", "esty"), false);
    }
}