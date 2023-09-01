package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class IndexOfFunctionTest {

    private IndexOfFunction indexOfFunction;

    @Before
    public void setUp() {
        indexOfFunction = new IndexOfFunction();
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResultError(indexOfFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(indexOfFunction.invoke(null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    public void invokeMatchNull() {
        FunctionTestUtil.assertResultList(indexOfFunction.invoke(Collections.emptyList(), null), Collections.emptyList());
        FunctionTestUtil.assertResultList(indexOfFunction.invoke(Collections.singletonList("test"), null), Collections.emptyList());
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList("test", null), null),
                Collections.singletonList(BigDecimal.valueOf(2)));
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList(null, "test"), null),
                Collections.singletonList(BigDecimal.ONE));
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList("test", null, BigDecimal.ZERO), null),
                Collections.singletonList(BigDecimal.valueOf(2)));
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList("test", null, null, BigDecimal.ZERO), null),
                Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3)));
    }

    @Test
    public void invokeBigDecimal() {
        FunctionTestUtil.assertResult(indexOfFunction.invoke(Arrays.asList("test", null, 12), BigDecimal.valueOf(12)), Collections.emptyList());
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(Arrays.asList("test", null, BigDecimal.valueOf(12)), BigDecimal.valueOf(12)),
                Collections.singletonList(BigDecimal.valueOf(3)));
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(
                        Arrays.asList("test", null, BigDecimal.valueOf(12)),
                        BigDecimal.valueOf(12).setScale(4, BigDecimal.ROUND_HALF_UP)),
                Collections.singletonList(BigDecimal.valueOf(3)));
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(
                        Arrays.asList(BigDecimal.valueOf(12.00), "test", null, BigDecimal.valueOf(12)),
                        BigDecimal.valueOf(12)),
                Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(4)));
    }

    @Test
    public void invokeMatchNotNull() {
        FunctionTestUtil.assertResult(indexOfFunction.invoke(Arrays.asList("test", null, 12), "testttt"), Collections.emptyList());
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(Arrays.asList("test", null, BigDecimal.valueOf(12)), "test"),
                Collections.singletonList(BigDecimal.valueOf(1)));
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(Arrays.asList("test", null, "test"),"test"),
                Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(3)));
    }
}