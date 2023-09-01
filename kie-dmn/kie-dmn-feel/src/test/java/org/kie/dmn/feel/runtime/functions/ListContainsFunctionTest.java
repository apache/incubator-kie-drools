package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ListContainsFunctionTest {

    private ListContainsFunction listContainsFunction;

    @Before
    public void setUp() {
        listContainsFunction = new ListContainsFunction();
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResultError(listContainsFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(listContainsFunction.invoke(null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    public void invokeContainsNull() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.singletonList(null), null), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, null), null), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(null, 1), null), true);
    }

    @Test
    public void invokeNotContainsNull() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.emptyList(), null), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.singletonList(1), null), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2), null), false);
    }

    @Test
    public void invokeContains() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), "test"), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), 1), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), BigDecimal.ONE), true);
    }

    @Test
    public void invokeNotContains() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), "testtt"), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), 3), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), BigDecimal.valueOf(3)), false);
    }
}