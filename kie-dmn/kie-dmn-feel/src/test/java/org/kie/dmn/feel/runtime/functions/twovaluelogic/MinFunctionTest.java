package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MinFunctionTest {

    private NNMinFunction minFunction;

    @Before
    public void setUp() {
        minFunction = new NNMinFunction();
    }

    @Test
    public void invokeNullList() {
        FunctionTestUtil.assertResult(minFunction.invoke((List) null), null);
    }

    @Test
    public void invokeEmptyList() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    public void invokeListWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(10.2))), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList(1)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(null, 1, 2, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, null, 1, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, 3, 1, null )), 1);
    }

    @Test
    public void invokeListOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList("a")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(null, "a", "b", "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "a", null, "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "c", "a", null)), "a");
    }

    @Test
    public void invokeNullArray() {
        FunctionTestUtil.assertResult(minFunction.invoke((Object[]) null), null);
    }

    @Test
    public void invokeEmptyArray() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{}), null);
    }

    @Test
    public void invokeArrayWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(new Object[]{1, "test", BigDecimal.valueOf(10.2)}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1, 2, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 1, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 3, 1}), 1);
    }

    @Test
    public void invokeArrayOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a", "b", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "a", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "c", "a"}), "a");
    }
}