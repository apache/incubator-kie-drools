package org.kie.dmn.feel.runtime.functions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import static java.math.BigDecimal.valueOf;

public class DayOfYearTest {

    private DayOfYearFunction fut;

    @Before
    public void setUp() {
        fut = DayOfYearFunction.INSTANCE;
    }

    @Test
    public void testDayOfYearFunctionTemporalAccessor() {
        FunctionTestUtil.assertResult(fut.invoke(LocalDate.of(2019, 9, 17)), valueOf(260));
        FunctionTestUtil.assertResult(fut.invoke(LocalDateTime.of(2019, 9, 17, 0, 0, 0)), valueOf(260));
        FunctionTestUtil.assertResult(fut.invoke(OffsetDateTime.of(2019, 9, 17, 0, 0, 0, 0, ZoneOffset.UTC)), valueOf(260));
        FunctionTestUtil.assertResult(fut.invoke(ZonedDateTime.of(2019, 9, 17, 0, 0, 0, 0, ZoneOffset.UTC)), valueOf(260));
        FunctionTestUtil.assertResultError(fut.invoke(null), InvalidParametersEvent.class);
    }
}
