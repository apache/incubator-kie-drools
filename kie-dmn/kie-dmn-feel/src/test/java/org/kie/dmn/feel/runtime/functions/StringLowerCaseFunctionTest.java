package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class StringLowerCaseFunctionTest {

    private StringLowerCaseFunction stringLowerCaseFunction;

    @Before
    public void setUp() {
        stringLowerCaseFunction = new StringLowerCaseFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(stringLowerCaseFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLowercaseString() {
        FunctionTestUtil.assertResult(stringLowerCaseFunction.invoke("teststring"), "teststring");
    }

    @Test
    public void invokeUppercaseString() {
        FunctionTestUtil.assertResult(stringLowerCaseFunction.invoke("TESTSTRING"), "teststring");
    }

    @Test
    public void invokeMixedCaseString() {
        FunctionTestUtil.assertResult(stringLowerCaseFunction.invoke("testSTRing"), "teststring");
    }
}