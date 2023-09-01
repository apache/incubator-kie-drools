package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SubstringBeforeFunctionTest {

    private SubstringBeforeFunction substringBeforeFunction;

    @Before
    public void setUp() {
        substringBeforeFunction = new SubstringBeforeFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(substringBeforeFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringBeforeFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringBeforeFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeMatchExists() {
        FunctionTestUtil.assertResult(substringBeforeFunction.invoke("foobar", "ob"), "fo");
        FunctionTestUtil.assertResult(substringBeforeFunction.invoke("foobar", "o"), "f");
    }

    @Test
    public void invokeMatchNotExists() {
        FunctionTestUtil.assertResult(substringBeforeFunction.invoke("foobar", "oook"), "");
    }
}