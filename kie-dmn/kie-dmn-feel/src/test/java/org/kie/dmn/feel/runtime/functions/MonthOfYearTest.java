package org.kie.dmn.feel.runtime.functions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class MonthOfYearTest {

    private MonthOfYearFunction fut;

    @Before
    public void setUp() {
        fut = MonthOfYearFunction.INSTANCE;
    }

    @Test
    public void testMonthOfYearFunctionTemporalAccessor() {
        FunctionTestUtil.assertResult(fut.invoke(LocalDate.of(2019, 9, 17)), "September");
        FunctionTestUtil.assertResult(fut.invoke(LocalDateTime.of(2019, 9, 17, 0, 0, 0)), "September");
        FunctionTestUtil.assertResult(fut.invoke(OffsetDateTime.of(2019, 9, 17, 0, 0, 0, 0, ZoneOffset.UTC)), "September");
        FunctionTestUtil.assertResult(fut.invoke(ZonedDateTime.of(2019, 9, 17, 0, 0, 0, 0, ZoneOffset.UTC)), "September");
        FunctionTestUtil.assertResultError(fut.invoke(null), InvalidParametersEvent.class);
    }
}
