package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class RemoveFunctionTest {

    private RemoveFunction removeFunction;

    @Before
    public void setUp() {
        removeFunction = new RemoveFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(removeFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.emptyList(), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokePositionZero() {
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    public void invokePositionOutOfListBounds() {
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(2)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(154)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(-2)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(-154)), InvalidParametersEvent.class);
    }

    @Test
    public void invokePositionPositive() {
        FunctionTestUtil.assertResultList(removeFunction.invoke(Collections.singletonList(1), BigDecimal.ONE), Collections.emptyList());
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.ONE),
                Arrays.asList("test", BigDecimal.valueOf(14)));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(2)),
                Arrays.asList(1, BigDecimal.valueOf(14)));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(3)),
                Arrays.asList(1, "test"));
    }

    @Test
    public void invokePositionNegative() {
        FunctionTestUtil.assertResultList(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(-1)), Collections.emptyList());
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(-1)),
                Arrays.asList(1, "test"));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(-2)),
                Arrays.asList(1, BigDecimal.valueOf(14)));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(-3)),
                Arrays.asList("test", BigDecimal.valueOf(14)));
    }
}