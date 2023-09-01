package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

public class StddevFunctionTest {

    private NNStddevFunction stddevFunction;

    @Before
    public void setUp() {
        stddevFunction = NNStddevFunction.INSTANCE;
    }

    @Test
    public void invokeNumberNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke((Number) null), null);
    }

    @Test
    public void invokeSingleNumber() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(BigDecimal.TEN), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10d), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10.1d), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10.1f), InvalidParametersEvent.class);
    }

    @Test
    public void invokeUnconvertableNumber() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Double.POSITIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Double.NEGATIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Double.NaN), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke((List) null), null);
    }

    @Test
    public void invokeListEmpty() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Arrays.asList(20, 30, null, (long) 40, null, BigDecimal.TEN)),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    public void invokeListWithIntegers() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Arrays.asList(10, 20, 30, 40)),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    public void invokeListWithDoubles() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Arrays.asList(10.0d, 20.0d, 30.0d, 40.0d)),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    public void invokeArrayNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke((Object[]) null), null);
    }

    @Test
    public void invokeArrayEmpty() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{}), null);
    }

    @Test
    public void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayWithIntegers() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{10, 20, 30, 40}),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    public void invokeArrayWithDoubles() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{10.0d, 20.0d, 30.0d, 40.0d}),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    public void invokeArrayParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{20, 30, null, (long) 40, null, BigDecimal.TEN}),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

}