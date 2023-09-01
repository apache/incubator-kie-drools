package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NNAllFunctionTest {

    private NNAllFunction NNAllFunction;

    @Before
    public void setUp() {
        NNAllFunction = new NNAllFunction();
    }

    @Test
    public void invokeBooleanParamNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke((Boolean) null), true);
    }

    @Test
    public void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(true), true);
    }

    @Test
    public void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(false), false);
    }

    @Test
    public void invokeArrayParamNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke((Object[]) null), true);
    }

    @Test
    public void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{}), true);
    }

    @Test
    public void invokeArrayParamReturnTrue() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
    }

    @Test
    public void invokeArrayParamReturnFalse() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), false);
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.FALSE}), false);
    }

    @Test
    public void invokeArrayParamReturnNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.TRUE}), true);
    }

    @Test
    public void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke((List) null), true);
    }

    @Test
    public void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Collections.emptyList()), true);
    }

    @Test
    public void invokeListParamReturnTrue() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
    }

    @Test
    public void invokeListParamReturnFalse() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), false);
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.FALSE)), false);
    }

    @Test
    public void invokeListParamReturnNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.TRUE)), true);
    }

    @Test
    public void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}