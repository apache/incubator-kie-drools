package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DistinctValuesFunctionTest {

    private DistinctValuesFunction distinctValuesFunction;

    @Before
    public void setUp() {
        distinctValuesFunction = new DistinctValuesFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(distinctValuesFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamNotCollection() {
        FunctionTestUtil.assertResultList(
                distinctValuesFunction.invoke(BigDecimal.valueOf(10.1)),
                Collections.singletonList(BigDecimal.valueOf(10.1)));
    }

    @Test
    public void invokeParamArray() {
        FunctionTestUtil.assertResultList(
                distinctValuesFunction.invoke(new Object[]{BigDecimal.valueOf(10.1)}),
                Collections.singletonList(new Object[]{BigDecimal.valueOf(10.1)}));
    }

    @Test
    public void invokeEmptyList() {
        FunctionTestUtil.assertResultList(distinctValuesFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void invokeList() {
        final List testValues = Arrays.asList(1, BigDecimal.valueOf(10.1), "test", 1, "test", BigDecimal.valueOf(10.1));
        FunctionTestUtil.assertResultList(
                distinctValuesFunction.invoke(testValues),
                Arrays.asList(1, BigDecimal.valueOf(10.1), "test"));
    }
}