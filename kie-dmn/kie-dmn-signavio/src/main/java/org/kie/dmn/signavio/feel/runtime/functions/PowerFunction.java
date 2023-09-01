package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.math.BigDecimal;
import java.math.MathContext;

public class PowerFunction
        extends BaseFEELFunction {

    public PowerFunction() {
        super( "power" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "base" ) BigDecimal base, @ParameterName( "exponent" ) BigDecimal exponent) {
        if ( base == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "base", "cannot be null"));
        }
        if ( exponent == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "exponent", "cannot be null"));
        }
        return FEELFnResult.ofResult( base.pow( exponent.intValue(), MathContext.DECIMAL128 ) );
    }
}
