package org.kie.dmn.core.compiler.profiles;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;
import org.kie.dmn.feel.runtime.functions.extended.TimeFunction;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExtendedDMNProfileTest {
    private final DateFunction dateFunction = DateFunction.INSTANCE;
    private final TimeFunction timeFunction = TimeFunction.INSTANCE;

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

    private static <T> void assertResult(FEELFnResult<T> result, T val) {
        assertTrue(result.isRight());
        assertThat(result.getOrElse(null), Matchers.equalTo(val));
    }
}
