package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AnyFunctionTest {

    private AnyFunction anyFunction;

    @Before
    public void setUp() {
        anyFunction = new AnyFunction();
    }

    @Test
    public void invokeBooleanParamNull() {
        FunctionTestUtil.assertResultNull(anyFunction.invoke((Boolean) null));
    }

    @Test
    public void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(true), true);
    }

    @Test
    public void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(false), false);
    }

    @Test
    public void invokeArrayParamNull() {
        FunctionTestUtil.assertResultError(anyFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{}), false);
    }

    @Test
    public void invokeArrayParamReturnTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, null}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.FALSE}), true);
    }

    @Test
    public void invokeArrayParamReturnFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.FALSE, Boolean.FALSE}), false);
    }

    @Test
    public void invokeArrayParamReturnNull() {
        FunctionTestUtil.assertResultNull(anyFunction.invoke(new Object[]{Boolean.FALSE, null, Boolean.FALSE}));
    }

    @Test
    public void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamNull() {
        FunctionTestUtil.assertResultError(anyFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Collections.emptyList()), false);
    }

    @Test
    public void invokeListParamReturnTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.FALSE)), true);
    }

    @Test
    public void invokeListParamReturnFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.FALSE, Boolean.FALSE)), false);
    }

    @Test
    public void invokeListParamReturnNull() {
        FunctionTestUtil.assertResultNull(anyFunction.invoke(Arrays.asList(Boolean.FALSE, null, Boolean.FALSE)));
    }

    @Test
    public void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}