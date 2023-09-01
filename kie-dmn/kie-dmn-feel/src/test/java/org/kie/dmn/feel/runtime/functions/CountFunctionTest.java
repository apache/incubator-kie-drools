package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class CountFunctionTest {

    private CountFunction countFunction;

    @Before
    public void setUp() {
        countFunction = new CountFunction();
    }

    @Test
    public void invokeParamListNull() {
        FunctionTestUtil.assertResultError(countFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamListEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(Collections.emptyList()), BigDecimal.ZERO);
    }

    @Test
    public void invokeParamListNonEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(Arrays.asList(1, 2, "test")), BigDecimal.valueOf(3));
    }

    @Test
    public void invokeParamArrayNull() {
        FunctionTestUtil.assertResultError(countFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamArrayEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(new Object[]{}), BigDecimal.ZERO);
    }

    @Test
    public void invokeParamArrayNonEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(new Object[]{1, 2, "test"}), BigDecimal.valueOf(3));
    }

}