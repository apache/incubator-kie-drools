package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class StringUpperCaseFunctionTest {

    private StringUpperCaseFunction stringUpperCaseFunction;

    @Before
    public void setUp() {
        stringUpperCaseFunction = new StringUpperCaseFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(stringUpperCaseFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLowercaseString() {
        FunctionTestUtil.assertResult(stringUpperCaseFunction.invoke("teststring"), "TESTSTRING");
    }

    @Test
    public void invokeUppercaseString() {
        FunctionTestUtil.assertResult(stringUpperCaseFunction.invoke("TESTSTRING"), "TESTSTRING");
    }

    @Test
    public void invokeMixedCaseString() {
        FunctionTestUtil.assertResult(stringUpperCaseFunction.invoke("testSTRing"), "TESTSTRING");
    }
}