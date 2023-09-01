package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class DecimalFunction
        extends BaseFEELFunction {

    public DecimalFunction() {
        super( "decimal" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal n, @ParameterName( "scale" ) BigDecimal scale) {
        if ( n == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "cannot be null"));
        }
        if ( scale == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "scale", "cannot be null"));
        }
        
        return FEELFnResult.ofResult( n.setScale( scale.intValue(), RoundingMode.HALF_EVEN ) );
    }
}
