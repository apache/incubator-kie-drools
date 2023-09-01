package org.kie.dmn.feel.runtime.functions;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;

import static java.math.BigDecimal.valueOf;

public class AbsFunctionTest {

    private AbsFunction absFunction;

    @Before
    public void setUp() {
        absFunction = AbsFunction.INSTANCE;
    }

    @Test
    public void testAbsFunctionNumber() {
        FunctionTestUtil.assertResult(absFunction.invoke(valueOf(10)), valueOf(10));
        FunctionTestUtil.assertResult(absFunction.invoke(valueOf(-10)), valueOf(10));
        FunctionTestUtil.assertResultError(absFunction.invoke((BigDecimal) null), InvalidParametersEvent.class);
    }

    @Test
    public void testAbsFunctionDuration() {
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(100, 50 )),
                Duration.ofSeconds(100, 50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(-100, 50 )),
                Duration.ofSeconds(100, -50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(100, -50 )),
                Duration.ofSeconds(100, -50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(-100, -50 )),
                Duration.ofSeconds(100, 50));
        FunctionTestUtil.assertResultError(absFunction.invoke((Duration)null),
                InvalidParametersEvent.class);
    }

    @Test
    public void testAbsFunctionPeriod() {
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( 100, 50, 0 ) ),
                Period.of(100, 50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -100, 50, 0 ) ),
                Period.of(100, -50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( 100, -50, 0 ) ),
                Period.of(100, -50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -100, -50, 0 ) ),
                Period.of(100, 50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -1, 30, 0 ) ),
                Period.of(-1, 30, 0));
        FunctionTestUtil.assertResultError(absFunction.invoke((Period) null ),
                InvalidParametersEvent.class);
    }


}