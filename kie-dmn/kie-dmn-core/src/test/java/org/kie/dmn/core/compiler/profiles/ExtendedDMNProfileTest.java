package org.kie.dmn.core.compiler.profiles;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.extended.AbsFunction;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;
import org.kie.dmn.feel.runtime.functions.extended.EvenFunction;
import org.kie.dmn.feel.runtime.functions.extended.ExpFunction;
import org.kie.dmn.feel.runtime.functions.extended.LogFunction;
import org.kie.dmn.feel.runtime.functions.extended.MedianFunction;
import org.kie.dmn.feel.runtime.functions.extended.ModeFunction;
import org.kie.dmn.feel.runtime.functions.extended.ModuloFunction;
import org.kie.dmn.feel.runtime.functions.extended.OddFunction;
import org.kie.dmn.feel.runtime.functions.extended.ProductFunction;
import org.kie.dmn.feel.runtime.functions.extended.SplitFunction;
import org.kie.dmn.feel.runtime.functions.extended.SqrtFunction;
import org.kie.dmn.feel.runtime.functions.extended.StddevFunction;
import org.kie.dmn.feel.runtime.functions.extended.TimeFunction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import static java.math.BigDecimal.valueOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExtendedDMNProfileTest {
    private final DateFunction dateFunction = DateFunction.INSTANCE;
    private final TimeFunction timeFunction = TimeFunction.INSTANCE;
    private final SplitFunction splitFunction = SplitFunction.INSTANCE;
    private final ProductFunction productFunction = ProductFunction.INSTANCE;
    private final MedianFunction medianFunction = MedianFunction.INSTANCE;
    private final StddevFunction stddevFunction = StddevFunction.INSTANCE;
    private final ModeFunction modeFunction = ModeFunction.INSTANCE;
    private final AbsFunction absFunction = AbsFunction.INSTANCE;
    private final ModuloFunction moduloFunction = ModuloFunction.INSTANCE;
    private final SqrtFunction sqrtFunction = SqrtFunction.INSTANCE;
    private final LogFunction logFunction = LogFunction.INSTANCE;
    private final ExpFunction expFunction = ExpFunction.INSTANCE;
    private final EvenFunction evenFunction = EvenFunction.INSTANCE;
    private final OddFunction oddFunction = OddFunction.INSTANCE;

    @Test
    public void testDateFunction_invokeParamStringDateTime() {
        assertResult(dateFunction.invoke("2017-09-07T10:20:30"), LocalDate.of(2017, 9, 7));
    }

    @Test
    public void testDateFunction_invokeExtended() {
        assertResult(dateFunction.invoke("2016-12-20T14:30:22"), DateTimeFormatter.ISO_DATE.parse( "2016-12-20", LocalDate::from ));
        assertResult(dateFunction.invoke("2016-12-20T14:30:22-05:00"), DateTimeFormatter.ISO_DATE.parse( "2016-12-20", LocalDate::from ));
        assertResult(dateFunction.invoke("2016-12-20T14:30:22z"), DateTimeFormatter.ISO_DATE.parse( "2016-12-20", LocalDate::from ));
    }

    @Test
    public void testTimeFunction_invokeStringParamDate() {
        assertResult(timeFunction.invoke("2017-10-09"), LocalTime.of(0,0,0));
        assertResult(timeFunction.invoke("2017-10-09T10:15:06"), LocalTime.of(10,15,6));
    }

    @Test
    public void testTimeFunction_invokeExtended() {
        assertResult(timeFunction.invoke("2016-12-20T14:30:22"), DateTimeFormatter.ISO_TIME.parse( "14:30:22", LocalTime::from ));
        assertResult(timeFunction.invoke("2016-12-20T14:30:22-05:00"), DateTimeFormatter.ISO_TIME.parse( "14:30:22-05:00", OffsetTime::from ));
        assertResult(timeFunction.invoke("2016-12-20T14:30:22z"), DateTimeFormatter.ISO_TIME.parse( "14:30:22z", OffsetTime::from ));
    }

    @Test
    public void testSplitFunction() {
        assertResult(splitFunction.invoke("John Doe", "\\s"), Arrays.asList("John", "Doe"));
        assertResult(splitFunction.invoke("a;b;c;;", ";"), Arrays.asList("a", "b", "c", "", ""));
    }

    @Test
    public void testProductFunction() {
        assertResult(productFunction.invoke(Arrays.asList(valueOf(2), valueOf(3), valueOf(4))), valueOf(24));
    }

    @Test
    public void testMedianFunction() {
        assertResult(medianFunction.invoke(new Object[]{valueOf(8), valueOf(2), valueOf(5), valueOf(3), valueOf(4)}), valueOf(4));
        assertResult(medianFunction.invoke(Arrays.asList(valueOf(6), valueOf(1), valueOf(2), valueOf(3))), valueOf(2.5));
        assertNull(medianFunction.invoke(new Object[]{}));
    }

    @Test
    public void testStddevFunction() {
        assertResultDoublePrecision(stddevFunction.invoke(new Object[]{2, 4, 7, 5}), valueOf(2.0816659994661326));
    }

    @Test
    public void testModeFunction() {
        assertResult(modeFunction.invoke(new Object[]{6, 3, 9, 6, 6}), Collections.singletonList(valueOf(6)));
        assertResult(modeFunction.invoke(Arrays.asList(6, 1, 9, 6, 1)), Arrays.asList(valueOf(1), valueOf(6)));
        assertResult(modeFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void testAbsFunction() {
        assertResult(absFunction.invoke(valueOf(10)), valueOf(10));
        assertResult(absFunction.invoke(valueOf(-10)), valueOf(10));
    }

    @Test
    public void testModuloFunction() {
        assertResult(moduloFunction.invoke(valueOf(12), valueOf(5)), valueOf(2));
    }

    @Test
    public void testSqrtFunction() {
        assertResultDoublePrecision(sqrtFunction.invoke(valueOf(16)), valueOf(4));
        assertResultDoublePrecision(sqrtFunction.invoke(valueOf(2)), valueOf(1.4142135623730951));
    }

    @Test
    public void testLogFunction() {
        assertResultDoublePrecision(logFunction.invoke(valueOf(10)), valueOf(2.302585092994046));
    }

    @Test
    public void testExpFunction() {
        assertResultDoublePrecision(expFunction.invoke(valueOf(5)), valueOf(148.4131591025766));
    }

    @Test
    public void testOddFunction() {
        assertResult(oddFunction.invoke(valueOf(5)), Boolean.TRUE);
        assertResult(oddFunction.invoke(valueOf(2)), Boolean.FALSE);
    }

    @Test
    public void testOddFunction_fractional() {
        assertNull(oddFunction.invoke(valueOf(5.5)));
        assertResult(oddFunction.invoke(valueOf(5.0)), Boolean.TRUE);
    }

    @Test
    public void testEvenFunction() {
        assertResult(evenFunction.invoke(valueOf(5)), Boolean.FALSE);
        assertResult(evenFunction.invoke(valueOf(2)), Boolean.TRUE);
    }

    @Test
    public void testEvenFunction_fractional() {
        assertNull(evenFunction.invoke(valueOf(5.5)));
        assertResult(evenFunction.invoke(valueOf(2.0)), Boolean.TRUE);
    }

    private static <T> void assertResult(FEELFnResult<T> result, T val) {
        assertTrue(result.isRight());
        assertThat(result.getOrElse(null), Matchers.equalTo(val));
    }

    private static void assertResultDoublePrecision(FEELFnResult<BigDecimal> result, BigDecimal val) {
        assertTrue(result.isRight());
        assertThat(Double.compare(result.getOrElse(null).doubleValue(), val.doubleValue()), Matchers.equalTo(0));
    }

    private static void assertNull(FEELFnResult<?> result) {
        Assert.assertNull(result.getOrElse(null));
    }
}
