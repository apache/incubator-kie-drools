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

public class WeekOfYearTest {

    private WeekOfYearFunction fut;

    @Before
    public void setUp() {
        fut = WeekOfYearFunction.INSTANCE;
    }

    @Test
    public void testWeekOfYearFunctionTemporalAccessor() {
        FunctionTestUtil.assertResult(fut.invoke(LocalDate.of(2019, 9, 17)), valueOf(38));
        FunctionTestUtil.assertResult(fut.invoke(LocalDateTime.of(2019, 9, 17, 0, 0, 0)), valueOf(38));
        FunctionTestUtil.assertResult(fut.invoke(OffsetDateTime.of(2019, 9, 17, 0, 0, 0, 0, ZoneOffset.UTC)), valueOf(38));
        FunctionTestUtil.assertResult(fut.invoke(ZonedDateTime.of(2019, 9, 17, 0, 0, 0, 0, ZoneOffset.UTC)), valueOf(38));
        FunctionTestUtil.assertResultError(fut.invoke(null), InvalidParametersEvent.class);
    }
}
