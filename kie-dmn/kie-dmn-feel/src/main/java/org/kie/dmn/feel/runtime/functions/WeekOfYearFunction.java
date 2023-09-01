package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class WeekOfYearFunction extends BaseFEELFunction {
    public static final WeekOfYearFunction INSTANCE = new WeekOfYearFunction();

    WeekOfYearFunction() {
        super("week of year");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("date") TemporalAccessor date) {
        if (date == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "date", "cannot be null"));
        }
        BigDecimal result = EvalHelper.getBigDecimalOrNull(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
        return FEELFnResult.ofResult(result);
    }

}
