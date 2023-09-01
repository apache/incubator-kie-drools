package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CountFunctionTest {

    private NNCountFunction countFunction;

    @Before
    public void setUp() {
        countFunction = new NNCountFunction();
    }

    @Test
    public void invokeParamListNull() {
        FunctionTestUtil.assertResult(countFunction.invoke((List) null), BigDecimal.ZERO);
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
        FunctionTestUtil.assertResult(countFunction.invoke((Object[]) null), BigDecimal.ZERO);
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