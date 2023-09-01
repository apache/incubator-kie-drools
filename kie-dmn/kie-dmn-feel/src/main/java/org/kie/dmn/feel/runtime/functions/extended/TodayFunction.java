package org.kie.dmn.feel.runtime.functions.extended;

import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class TodayFunction
        extends BaseFEELFunction {

    public TodayFunction() {
        super( "today" );
    }

    public FEELFnResult<TemporalAccessor> invoke() {
        return FEELFnResult.ofResult( LocalDate.now() );
    }

}
