package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ConcatenateFunctionTest {

    private ConcatenateFunction concatenateFunction;

    @Before
    public void setUp() {
        concatenateFunction = new ConcatenateFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(concatenateFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyArray() {
        FunctionTestUtil.assertResultList(concatenateFunction.invoke(new Object[]{}), Collections.emptyList());
    }

    @Test
    public void invokeArrayWithNull() {
        FunctionTestUtil.assertResultError(concatenateFunction.invoke(new Object[]{null}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(concatenateFunction.invoke(new Object[]{1, null}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayWithList() {
        FunctionTestUtil.assertResultList(concatenateFunction.invoke(new Object[]{"test", 2, Arrays.asList(2, 3)}), Arrays.asList("test", 2, 2, 3));
    }

    @Test
    public void invokeArrayWithoutList() {
        FunctionTestUtil.assertResultList(concatenateFunction.invoke(new Object[]{"test", 2, BigDecimal.valueOf(25.3)}), Arrays.asList("test", 2, BigDecimal.valueOf(25.3)));
    }

}