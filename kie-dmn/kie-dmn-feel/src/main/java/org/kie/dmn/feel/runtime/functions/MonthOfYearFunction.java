package org.kie.dmn.feel.runtime.functions;

import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class MonthOfYearFunction extends BaseFEELFunction {
    public static final MonthOfYearFunction INSTANCE = new MonthOfYearFunction();

    MonthOfYearFunction() {
        super("month of year");
    }

    public FEELFnResult<String> invoke(@ParameterName("date") TemporalAccessor date) {
        if (date == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "date", "cannot be null"));
        }

        String result = Month.from(date).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return FEELFnResult.ofResult(result);
    }

}
