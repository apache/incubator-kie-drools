package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundDownFunction
        extends BaseFEELFunction {

    public RoundDownFunction() {
        super( "roundDown" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number ) {
        return invoke( number, BigDecimal.ZERO );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number, @ParameterName( "digits" ) BigDecimal digits) {
        if ( number == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "number", "cannot be null"));
        }
        if( digits == null ) {
            digits = BigDecimal.ZERO;
        }
        return FEELFnResult.ofResult( number.setScale( digits.intValue(), RoundingMode.DOWN ) );
    }
}
