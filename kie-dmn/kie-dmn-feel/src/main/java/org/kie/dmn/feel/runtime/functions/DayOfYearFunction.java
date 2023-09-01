package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class DayOfYearFunction extends BaseFEELFunction {
    public static final DayOfYearFunction INSTANCE = new DayOfYearFunction();

    DayOfYearFunction() {
        super("day of year");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("date") TemporalAccessor date) {
        if (date == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "date", "cannot be null"));
        }

        BigDecimal result = EvalHelper.getBigDecimalOrNull(ChronoField.DAY_OF_YEAR.getFrom(date));
        return FEELFnResult.ofResult(result);
    }

}
