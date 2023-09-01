package org.kie.dmn.feel.runtime.functions.extended;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class NowFunction
        extends BaseFEELFunction {

    public NowFunction() {
        super( "now" );
    }

    public FEELFnResult<TemporalAccessor> invoke() {
        return FEELFnResult.ofResult( ZonedDateTime.now() );
    }

}
