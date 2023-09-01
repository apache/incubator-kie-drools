package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.math.BigDecimal;
import java.math.MathContext;

public class PercentFunction
        extends BaseFEELFunction {

    private static final BigDecimal HUNDRED = new BigDecimal( "100" );

    public PercentFunction() {
        super( "percent" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "number", "cannot be null"));
        }
        return FEELFnResult.ofResult( number.divide( HUNDRED, MathContext.DECIMAL128 ) );
    }
}
