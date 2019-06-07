package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.math.BigDecimal;
import java.math.MathContext;

public class LogFunction
        extends BaseFEELFunction {
    public static final LogFunction INSTANCE = new LogFunction();

    LogFunction() {
        super("log");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "cannot be null" ) );
        }
        return FEELFnResult.ofResult( new BigDecimal( Math.log( number.doubleValue() ), MathContext.DECIMAL128 ) );
    }
}
