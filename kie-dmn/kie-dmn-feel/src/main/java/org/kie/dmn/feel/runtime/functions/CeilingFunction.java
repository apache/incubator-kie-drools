package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class CeilingFunction
        extends BaseFEELFunction {

    public CeilingFunction() {
        super( "ceiling" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal n) {
        if ( n == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "cannot be null"));
        }
        return FEELFnResult.ofResult( n.setScale( 0, RoundingMode.CEILING ) );
    }
}
