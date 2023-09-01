package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModeFunctionTest {

    private NNModeFunction modeFunction;

    @Before
    public void setUp() {
        modeFunction = NNModeFunction.INSTANCE;
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke((List) null), null);
    }

    @Test
    public void invokeListEmpty() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(modeFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, null, BigDecimal.TEN)),
                                      List.of(BigDecimal.valueOf(20)));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, 30, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20), BigDecimal.valueOf(30) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(null, null, null)),
                null);
    }

    @Test
    public void invokeArrayNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke((Object[]) null), null);
    }

    @Test
    public void invokeArrayEmpty() {
        FunctionTestUtil.assertResult(modeFunction.invoke(new Object[]{}), null);
    }

    @Test
    public void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(modeFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, null, BigDecimal.TEN)),
                                      List.of(BigDecimal.valueOf(20)));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(20, 30, null, (long) 20, 30, BigDecimal.TEN)),
                Arrays.asList( BigDecimal.valueOf(20), BigDecimal.valueOf(30) ));
        FunctionTestUtil.assertResult(modeFunction.invoke(Arrays.asList(null, null, null)),
                null);
    }

}