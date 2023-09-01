package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ReverseFunctionTest {

    private ReverseFunction reverseFunction;

    @Before
    public void setUp() {
        reverseFunction = new ReverseFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(reverseFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyList() {
        FunctionTestUtil.assertResultList(reverseFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void invokeListTypeHomogenous() {
        FunctionTestUtil.assertResultList(reverseFunction.invoke(Arrays.asList(1, 2, 3, 4)), Arrays.asList(4, 3, 2, 1));
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultList(
                reverseFunction.invoke(Arrays.asList(1, "test", BigDecimal.TEN, Collections.emptyList())),
                Arrays.asList(Collections.emptyList(), BigDecimal.TEN, "test", 1));

        FunctionTestUtil.assertResultList(
                reverseFunction.invoke(Arrays.asList(1, "test", BigDecimal.TEN, Arrays.asList(1, 2, 3))),
                Arrays.asList(Arrays.asList(1, 2, 3), BigDecimal.TEN, "test", 1));
    }
}