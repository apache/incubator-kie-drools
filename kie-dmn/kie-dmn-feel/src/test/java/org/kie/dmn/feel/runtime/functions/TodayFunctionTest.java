package org.kie.dmn.feel.runtime.functions;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.extended.TodayFunction;

public class TodayFunctionTest {

    private TodayFunction todayFunction;

    @Before
    public void setUp() {
        todayFunction = new TodayFunction();
    }

    @Test
    public void invoke() {
        FunctionTestUtil.assertResult(todayFunction.invoke(), LocalDate.now());
    }

}