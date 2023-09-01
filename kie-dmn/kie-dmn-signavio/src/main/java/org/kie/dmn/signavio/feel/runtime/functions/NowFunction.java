package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

public class NowFunction
        extends BaseFEELFunction {
    public static final NowFunction INSTANCE = new NowFunction();

    NowFunction() {
        super( "now" );
    }

    public FEELFnResult<TemporalAccessor> invoke() {
        return FEELFnResult.ofResult( ZonedDateTime.now() );
    }

}
