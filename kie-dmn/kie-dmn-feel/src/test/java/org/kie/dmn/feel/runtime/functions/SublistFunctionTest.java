package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SublistFunctionTest {

    private SublistFunction sublistFunction;

    @Before
    public void setUp() {
        sublistFunction = new SublistFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Collections.emptyList(), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartZero() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartOutOfListBounds() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.TEN), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.valueOf(-10)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthNegative() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.valueOf(1), BigDecimal.valueOf(-3)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthOutOfListBounds() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.ONE, BigDecimal.valueOf(3)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.valueOf(-1), BigDecimal.valueOf(3)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartPositive() {
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, 2, 3), BigDecimal.valueOf(2)), Arrays.asList(2, 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(2)), Arrays.asList("test", 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(2), BigDecimal.ONE), Collections.singletonList("test"));
    }

    @Test
    public void invokeStartNegative() {
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, 2, 3), BigDecimal.valueOf(-2)), Arrays.asList(2, 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(-2)), Arrays.asList("test", 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(-2), BigDecimal.ONE), Collections.singletonList("test"));
    }
}