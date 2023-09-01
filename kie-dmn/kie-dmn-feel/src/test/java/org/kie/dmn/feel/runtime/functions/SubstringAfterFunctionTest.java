package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SubstringAfterFunctionTest {

    private SubstringAfterFunction substringAfterFunction;

    @Before
    public void setUp() {
        substringAfterFunction = new SubstringAfterFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(substringAfterFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringAfterFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringAfterFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeMatchExists() {
        FunctionTestUtil.assertResult(substringAfterFunction.invoke("foobar", "ob"), "ar");
        FunctionTestUtil.assertResult(substringAfterFunction.invoke("foobar", "o"), "obar");
    }

    @Test
    public void invokeMatchNotExists() {
        FunctionTestUtil.assertResult(substringAfterFunction.invoke("foobar", "oook"), "");
    }
}