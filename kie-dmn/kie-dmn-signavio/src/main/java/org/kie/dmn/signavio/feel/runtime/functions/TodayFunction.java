package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;

public class TodayFunction
        extends BaseFEELFunction {
    public static final TodayFunction INSTANCE = new TodayFunction();

    TodayFunction() {
        super( "today" );
    }

    public FEELFnResult<TemporalAccessor> invoke() {
        return FEELFnResult.ofResult( LocalDate.now() );
    }

}
